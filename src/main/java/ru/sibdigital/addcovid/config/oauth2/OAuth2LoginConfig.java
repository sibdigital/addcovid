package ru.sibdigital.addcovid.config.oauth2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.RequestEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.jwt.JwtDecoderFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import ru.sibdigital.addcovid.utils.EsiaUtil;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@Slf4j
public class OAuth2LoginConfig {

    @Value("${esia.client-id}")
    private String esiaClientId;

    @Value("${esia.authorization.url}")
    private String esiaAuthorizationUrl;

    @Value("${esia.token.url}")
    private String esiaTokenUrl;

    @Value("${esia.user.scopes}")
    private String userScopes;

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(this.esiaClientRegistration());
    }

    private ClientRegistration esiaClientRegistration() {
        String scopes = userScopes != null && !userScopes.isBlank() ? "openid " + userScopes : "openid";
        return ClientRegistration.withRegistrationId("esia")
                .clientId(esiaClientId)
                .clientAuthenticationMethod(ClientAuthenticationMethod.POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUriTemplate("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope(StringUtils.delimitedListToStringArray(scopes, " "))
                .authorizationUri(esiaAuthorizationUrl)
                .tokenUri(esiaTokenUrl)
//                .userInfoUri("")
                .clientName("ЕСИА")
                .build();
    }

    @Bean
    public JwtDecoderFactory<ClientRegistration> idTokenDecoderFactory() {
        CustomOidcIdTokenDecoderFactory idTokenDecoderFactory = new CustomOidcIdTokenDecoderFactory();
        return idTokenDecoderFactory;
    }

    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
        DefaultAuthorizationCodeTokenResponseClient accessTokenResponseClient =
                new DefaultAuthorizationCodeTokenResponseClient();
        accessTokenResponseClient.setRequestEntityConverter(new CustomRequestEntityConverter());

        OAuth2AccessTokenResponseHttpMessageConverter tokenResponseHttpMessageConverter =
                new OAuth2AccessTokenResponseHttpMessageConverter();
        tokenResponseHttpMessageConverter.setTokenResponseConverter(new CustomTokenResponseConverter());
        RestTemplate restTemplate = new RestTemplate(Arrays.asList(
                new FormHttpMessageConverter(), tokenResponseHttpMessageConverter));
        restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());

        accessTokenResponseClient.setRestOperations(restTemplate);
        return accessTokenResponseClient;
    }

    public class CustomRequestEntityConverter implements
            Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> {

        private OAuth2AuthorizationCodeGrantRequestEntityConverter defaultConverter;

        public CustomRequestEntityConverter() {
            defaultConverter = new OAuth2AuthorizationCodeGrantRequestEntityConverter();
        }

        @Override
        public RequestEntity<?> convert(OAuth2AuthorizationCodeGrantRequest authorizationCodeGrantRequest) {
            RequestEntity<?> entity = defaultConverter.convert(authorizationCodeGrantRequest);
            MultiValueMap<String, String> formParameters = (MultiValueMap<String,String>) entity.getBody();

            String clientId = formParameters.getFirst("client_id");
            String scope = "openid " + userScopes;
            String state = EsiaUtil.getState();
            String timestamp = EsiaUtil.getTimestamp();
            String clientSecret = "";
            try {
                clientSecret = EsiaUtil.getClientSecret(clientId, scope, state, timestamp);
            } catch (Exception e) {
                log.error("Client secret not generated", e);
            }

            formParameters.add("scope", scope);
            formParameters.add("state", state);
            formParameters.add("timestamp", timestamp);
            formParameters.add("token_type", OAuth2AccessToken.TokenType.BEARER.getValue());
            formParameters.add("client_secret", clientSecret);
            return new RequestEntity<>(formParameters, entity.getHeaders(),
                    entity.getMethod(), entity.getUrl());
        }
    }

    public class CustomTokenResponseConverter implements
            Converter<Map<String, String>, OAuth2AccessTokenResponse> {

        private final Set<String> TOKEN_RESPONSE_PARAMETER_NAMES = Stream.of(
                OAuth2ParameterNames.ACCESS_TOKEN,
                OAuth2ParameterNames.TOKEN_TYPE,
                OAuth2ParameterNames.EXPIRES_IN,
                OAuth2ParameterNames.REFRESH_TOKEN,
                OAuth2ParameterNames.SCOPE).collect(Collectors.toSet());

        @Override
        public OAuth2AccessTokenResponse convert(Map<String, String> tokenResponseParameters) {
            String accessToken = tokenResponseParameters.get(OAuth2ParameterNames.ACCESS_TOKEN);
            OAuth2AccessToken.TokenType accessTokenType = null;
            if (OAuth2AccessToken.TokenType.BEARER.getValue().equalsIgnoreCase(
                    tokenResponseParameters.get(OAuth2ParameterNames.TOKEN_TYPE))) {
                accessTokenType = OAuth2AccessToken.TokenType.BEARER;
            }

            long expiresIn = 0;
            if (tokenResponseParameters.containsKey(OAuth2ParameterNames.EXPIRES_IN)) {
                try {
                    expiresIn = Long.valueOf(tokenResponseParameters.get(OAuth2ParameterNames.EXPIRES_IN));
                } catch (NumberFormatException ex) {
                }
            }

            Set<String> scopes = Collections.emptySet();
            if (tokenResponseParameters.containsKey(OAuth2ParameterNames.SCOPE)) {
                String scope = tokenResponseParameters.get(OAuth2ParameterNames.SCOPE);
                scopes = Arrays.stream(StringUtils.delimitedListToStringArray(scope, " "))
                        .collect(Collectors.toSet());
            }

            String refreshToken = tokenResponseParameters.get(OAuth2ParameterNames.REFRESH_TOKEN);
            Map<String, Object> additionalParameters = new LinkedHashMap<>();
            tokenResponseParameters.entrySet().stream()
                    .filter(e -> !TOKEN_RESPONSE_PARAMETER_NAMES.contains(e.getKey()))
                    .forEach(e -> additionalParameters.put(e.getKey(), e.getValue()));

            return OAuth2AccessTokenResponse.withToken(accessToken)
                    .tokenType(accessTokenType)
                    .expiresIn(expiresIn)
                    .scopes(scopes)
                    .refreshToken(refreshToken)
                    .additionalParameters(additionalParameters)
                    .build();
        }
    }
}

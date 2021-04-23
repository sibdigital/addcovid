package ru.sibdigital.addcovid.config.oauth2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.jwt.JwtDecoderFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import ru.sibdigital.addcovid.service.SettingService;
import ru.sibdigital.addcovid.utils.ConstantNames;
import ru.sibdigital.addcovid.utils.EsiaUtil;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@Slf4j
public class OAuth2LoginConfig {

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private SettingService settingService;

    @Bean
    public OAuth2AuthorizationRequestResolver customAuthorizationRequestResolver() {
        CustomAuthorizationRequestResolver customAuthorizationRequestResolver =
                new CustomAuthorizationRequestResolver(this.clientRegistrationRepository, this.settingService);
        return customAuthorizationRequestResolver;
    }

    public final class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

        private final OAuth2AuthorizationRequestResolver defaultAuthorizationRequestResolver;
        private final SettingService settingService;

        public CustomAuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository,
                                                  SettingService settingService) {
            this.defaultAuthorizationRequestResolver =
                    new DefaultOAuth2AuthorizationRequestResolver(
                            clientRegistrationRepository, "/oauth2/authorization");
            this.settingService = settingService;
        }

        @Override
        public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
            OAuth2AuthorizationRequest authorizationRequest =
                    this.defaultAuthorizationRequestResolver.resolve(request);

            return authorizationRequest != null ?
                    customAuthorizationRequest(authorizationRequest) :
                    null;
        }

        @Override
        public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
            OAuth2AuthorizationRequest authorizationRequest =
                    this.defaultAuthorizationRequestResolver.resolve(
                            request, clientRegistrationId);

            return authorizationRequest != null ?
                    customAuthorizationRequest(authorizationRequest) :
                    null;
        }

        private OAuth2AuthorizationRequest customAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest) {
            String clientId = authorizationRequest.getClientId();
            String scope = StringUtils.collectionToDelimitedString(authorizationRequest.getScopes(), " ");
            String timestamp = EsiaUtil.getTimestamp();
            String state = EsiaUtil.getState();

            String alias = this.settingService.findActualByKey(ConstantNames.SETTING_ESIA_KEYSTORE_ALIAS, "");
            String password = this.settingService.findActualByKey(ConstantNames.SETTING_ESIA_KEYSTORE_PASSWORD, "");
            String clientSecret = EsiaUtil.getClientSecret(scope + timestamp + clientId + state, alias, password);

            String params = "?"
                    .concat("client_id=" + authorizationRequest.getClientId() + "&")
                    .concat("scope=" + scope + "&")
                    .concat("state=" + state + "&")
                    .concat("timestamp=" + URLEncoder.encode(timestamp, StandardCharsets.UTF_8) + "&")
                    .concat("response_type=" + authorizationRequest.getResponseType().getValue() + "&")
                    .concat("redirect_uri=" + URLEncoder.encode(authorizationRequest.getRedirectUri(), StandardCharsets.UTF_8) + "&")
                    .concat("client_secret=" + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8));

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("registration_id", authorizationRequest.getAttribute("registration_id"));

            return OAuth2AuthorizationRequest.from(authorizationRequest)
                    .authorizationRequestUri(authorizationRequest.getAuthorizationUri() + params)
                    .state(state)
                    .scope(scope.split(" "))
                    .attributes(attributes)
                    .build();
        }
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
        accessTokenResponseClient.setRequestEntityConverter(new CustomRequestEntityConverter(this.settingService));

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
        private SettingService settingService;

        public CustomRequestEntityConverter(SettingService settingService) {
            this.defaultConverter = new OAuth2AuthorizationCodeGrantRequestEntityConverter();
            this.settingService = settingService;
        }

        @Override
        public RequestEntity<?> convert(OAuth2AuthorizationCodeGrantRequest authorizationCodeGrantRequest) {
            RequestEntity<?> entity = defaultConverter.convert(authorizationCodeGrantRequest);
            MultiValueMap<String, String> formParameters = (MultiValueMap<String,String>) entity.getBody();

            String clientId = formParameters.getFirst("client_id");
            String scope = "openid " + settingService.findActualByKey(ConstantNames.SETTING_ESIA_USER_SCOPES, "");
            String state = EsiaUtil.getState();
            String timestamp = EsiaUtil.getTimestamp();

            String alias = settingService.findActualByKey(ConstantNames.SETTING_ESIA_KEYSTORE_ALIAS, "");
            String password = settingService.findActualByKey(ConstantNames.SETTING_ESIA_KEYSTORE_PASSWORD, "");
            String clientSecret = EsiaUtil.getClientSecret(scope + timestamp + clientId + state, alias, password);

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

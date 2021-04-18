package ru.sibdigital.addcovid.config.oauth2;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.util.StringUtils;
import ru.sibdigital.addcovid.utils.EsiaUtil;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final OAuth2AuthorizationRequestResolver defaultAuthorizationRequestResolver;

    public CustomAuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        this.defaultAuthorizationRequestResolver =
                new DefaultOAuth2AuthorizationRequestResolver(
                        clientRegistrationRepository, "/oauth2/authorization");
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
        String clientSecret = "";
        try {
            clientSecret = EsiaUtil.getClientSecret(clientId, scope, state, timestamp);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String params = "?"
                .concat("client_id=" + authorizationRequest.getClientId() + "&")
                .concat("scope=" + scope + "&")
                .concat("state=" + state + "&")
                .concat("timestamp=" + URLEncoder.encode(timestamp, StandardCharsets.UTF_8) + "&")
                .concat("response_type=" + authorizationRequest.getResponseType().getValue() + "&")
                .concat("redirect_uri=" + URLEncoder.encode(authorizationRequest.getRedirectUri(), StandardCharsets.UTF_8) + "&")
                .concat("client_secret=" + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8));

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("registration_id", "esia");

        return OAuth2AuthorizationRequest.from(authorizationRequest)
                .authorizationRequestUri(authorizationRequest.getAuthorizationUri() + params)
                .state(state)
                .scope(scope.split(" "))
                .attributes(attributes)
                .build();
    }
}

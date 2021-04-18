package ru.sibdigital.addcovid.service.esia;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import ru.sibdigital.addcovid.dto.esia.*;
import ru.sibdigital.addcovid.utils.EsiaUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EsiaServiceImpl implements EsiaService {

    @Value("${esia.client-id}")
    private String clientId;

    @Value("${esia.token.url}")
    private String tokenUri;

    @Value("${esia.api.url}")
    private String apiUrl;

    @Value("${esia.organization.scopes}")
    private String organizationScopes;

    @Autowired
    private OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    private static OAuth2AuthenticationToken getAuthentication() {
        return (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Метод для получения маркера доступа на scopes
     *
     * @param scopes
     * @return Token
     */
    public Token getAccessToken(List<String> scopes) {
        String scope = StringUtils.collectionToDelimitedString(scopes, " ");
        String state = EsiaUtil.getState();
        String timestamp = EsiaUtil.getTimestamp();
        String clientSecret;
        try {
            clientSecret = EsiaUtil.getClientSecret(clientId, scope, state, timestamp);
        } catch (Exception e) {
            log.error("Client secret not generated", e);
            return null;
        }

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("response_type", "token");
        params.add("grant_type", "client_credentials");
        params.add("scope", scope);
        params.add("state", state);
        params.add("timestamp", timestamp);
        params.add("token_type", "Bearer");
        params.add("client_secret", clientSecret);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity request = new HttpEntity(params, headers);
        try {
            ResponseEntity<Token> response = restTemplate.postForEntity(tokenUri, request, Token.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Token not received: " + e.getMessage(), e);
        }
        return null;
    }

    public User getUser() {
        OAuth2AuthenticationToken oAuth2AuthenticationToken = getAuthentication();
        OidcUser oidcUser = (OidcUser) oAuth2AuthenticationToken.getPrincipal();
        Long userId = oidcUser.getAttribute("sub");

        OAuth2AuthorizedClient client = oAuth2AuthorizedClientService.loadAuthorizedClient(
                oAuth2AuthenticationToken.getAuthorizedClientRegistrationId(), oAuth2AuthenticationToken.getName());
        if (client == null) {
            log.error("Client not received! User is not authenticated");
            return null;
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + client.getAccessToken().getTokenValue());
        HttpEntity request = new HttpEntity(headers);
        try {
            ResponseEntity<User> response = restTemplate.exchange(apiUrl + "/prns/{id}", HttpMethod.GET, request, User.class, userId);
            return response.getBody();
        } catch (Exception e) {
            log.error("User not received: " + e.getMessage(), e);
        }
        return null;
    }

    public Roles getUserRoles() {
        OAuth2AuthenticationToken oAuth2AuthenticationToken = getAuthentication();
        OidcUser user = (OidcUser) oAuth2AuthenticationToken.getPrincipal();
        Long userId = user.getAttribute("sub");

        OAuth2AuthorizedClient client = oAuth2AuthorizedClientService.loadAuthorizedClient(
                oAuth2AuthenticationToken.getAuthorizedClientRegistrationId(), oAuth2AuthenticationToken.getName());
        if (client == null) {
            log.error("Client not received! User is not authenticated");
            return null;
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + client.getAccessToken().getTokenValue());
        HttpEntity request = new HttpEntity(headers);
        try {
            ResponseEntity<Roles> response = restTemplate.exchange(apiUrl + "/prns/{id}/roles", HttpMethod.GET, request, Roles.class, userId);
            return response.getBody();
        } catch (Exception e) {
            log.error("Roles not received: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Organization> getUserOrganizations() {
        Roles roles = getUserRoles();
        if (roles == null || roles.getOrganizations() == null) {
            return new ArrayList<>(0);
        }

        List<UserOrganization> userOrganizations = roles.getOrganizations().stream()
                .filter(o -> o.isActive() && !o.isLiquidated() && (o.isChief() || o.isAdmin()))
                .collect(Collectors.toList());
        if (userOrganizations.isEmpty()) {
            return new ArrayList<>(0);
        }

        List<Long> orgIds = userOrganizations.stream().map(o -> o.getOid()).collect(Collectors.toList());

        List<String> scopes = Arrays.asList(StringUtils.delimitedListToStringArray(organizationScopes, " "));
        List<String> scopeLinks = new ArrayList<>();
        scopes.forEach(scope -> {
            orgIds.forEach(id -> {
                scopeLinks.add("http://esia.gosuslugi.ru/" + scope + "?org_oid=" + id);
            });
        });

        Token token = getAccessToken(scopeLinks);
        if (token == null) {
            return new ArrayList<>(0);
        }

        List<Organization> organizations = new ArrayList<>();
        for (Long id: orgIds) {
            Organization organization = getOrganization(token, id);
            if (organization != null) {
                UserOrganization userOrganization = userOrganizations.stream()
                        .filter(o -> o.getOid().equals(id)).findFirst().get();
                organization.setUserOrganization(userOrganization);
                organizations.add(organization);
            }
        }
        return organizations;
    }

    public Organization getOrganization(Token token, Long orgId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token.getTokenType() + " " + token.getAccessToken());
        HttpEntity request = new HttpEntity(headers);
        try {
            ResponseEntity<Organization> response = restTemplate.exchange(apiUrl + "/orgs/{id}", HttpMethod.GET, request, Organization.class, orgId);
            return response.getBody();
        } catch (Exception e) {
            log.error("Organization not received: " + e.getMessage(), e);
        }
        return null;
    }
}

package ru.sibdigital.addcovid.config.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.util.StringUtils;
import ru.sibdigital.addcovid.service.SettingService;
import ru.sibdigital.addcovid.utils.ConstantNames;

@Configuration
public class ClientRegistrationConfig {

    @Autowired
    private SettingService settingService;

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(this.esiaClientRegistration());
    }

    private ClientRegistration esiaClientRegistration() {
        String userScopes = settingService.findActualByKey(ConstantNames.SETTING_ESIA_USER_SCOPES, "");
        String scopes = userScopes != null && !userScopes.isBlank() ? "openid " + userScopes : "openid";
        return ClientRegistration.withRegistrationId("esia")
                .clientId(settingService.findActualByKey(ConstantNames.SETTING_ESIA_CLIENT_ID, ""))
                .clientAuthenticationMethod(ClientAuthenticationMethod.POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUriTemplate("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope(StringUtils.delimitedListToStringArray(scopes, " "))
                .authorizationUri(settingService.findActualByKey(ConstantNames.SETTING_ESIA_AUTHORIZATION_URL, ""))
                .tokenUri(settingService.findActualByKey(ConstantNames.SETTING_ESIA_TOKEN_URL, ""))
//                .userInfoUri("")
                .clientName("ЕСИА")
                .build();
    }
}

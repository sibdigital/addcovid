package ru.sibdigital.addcovid.config.oauth2;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.converter.ClaimConversionService;
import org.springframework.security.oauth2.core.converter.ClaimTypeConverter;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoderFactory;
import org.springframework.util.Assert;

import java.net.URL;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class CustomOidcIdTokenDecoderFactory implements JwtDecoderFactory<ClientRegistration> {

    private static Map<String, Converter<Object, ?>> createDefaultClaimTypeConverters() {
        Converter<Object, ?> booleanConverter = getConverter(TypeDescriptor.valueOf(Boolean.class));
        Converter<Object, ?> instantConverter = getConverter(TypeDescriptor.valueOf(Instant.class));
        Converter<Object, ?> urlConverter = getConverter(TypeDescriptor.valueOf(URL.class));
        Converter<Object, ?> stringConverter = getConverter(TypeDescriptor.valueOf(String.class));
        Converter<Object, ?> collectionStringConverter = getConverter(
                TypeDescriptor.collection(Collection.class, TypeDescriptor.valueOf(String.class)));

        Map<String, Converter<Object, ?>> claimTypeConverters = new HashMap<>();
        claimTypeConverters.put(IdTokenClaimNames.ISS, urlConverter);
        claimTypeConverters.put(IdTokenClaimNames.AUD, collectionStringConverter);
        claimTypeConverters.put(IdTokenClaimNames.NONCE, stringConverter);
        claimTypeConverters.put(IdTokenClaimNames.EXP, instantConverter);
        claimTypeConverters.put(IdTokenClaimNames.IAT, instantConverter);
        claimTypeConverters.put(IdTokenClaimNames.AUTH_TIME, instantConverter);
        claimTypeConverters.put(IdTokenClaimNames.AMR, collectionStringConverter);
        claimTypeConverters.put(StandardClaimNames.EMAIL_VERIFIED, booleanConverter);
        claimTypeConverters.put(StandardClaimNames.PHONE_NUMBER_VERIFIED, booleanConverter);
        claimTypeConverters.put(StandardClaimNames.UPDATED_AT, instantConverter);
        return claimTypeConverters;
    }

    private static Converter<Object, ?> getConverter(TypeDescriptor targetDescriptor) {
        final TypeDescriptor sourceDescriptor = TypeDescriptor.valueOf(Object.class);
        return source -> ClaimConversionService.getSharedInstance().convert(source, sourceDescriptor, targetDescriptor);
    }

    public JwtDecoder createDecoder(ClientRegistration clientRegistration) {
        Assert.notNull(clientRegistration, "clientRegistration cannot be null");
        EsiaJwtDecoder jwtDecoder = new EsiaJwtDecoder();
        jwtDecoder.setClaimSetConverter(new ClaimTypeConverter(createDefaultClaimTypeConverters()));
        return jwtDecoder;
    }
}

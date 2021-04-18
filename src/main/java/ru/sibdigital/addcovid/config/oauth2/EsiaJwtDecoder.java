package ru.sibdigital.addcovid.config.oauth2;

import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jwt.*;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.*;

@Slf4j
public final class EsiaJwtDecoder implements JwtDecoder  {

    private static final String DECODING_ERROR_MESSAGE_TEMPLATE = "An error occurred while attempting to decode the Jwt: %s";

    private Converter<Map<String, Object>, Map<String, Object>> claimSetConverter = MappedJwtClaimSetConverter.withDefaults(Collections.emptyMap());
    private OAuth2TokenValidator<Jwt> jwtValidator = JwtValidators.createDefault();

    public void setJwtValidator(OAuth2TokenValidator<Jwt> jwtValidator) {
        Assert.notNull(jwtValidator, "jwtValidator cannot be null");
        this.jwtValidator = jwtValidator;
    }

    public void setClaimSetConverter(Converter<Map<String, Object>, Map<String, Object>> claimSetConverter) {
        Assert.notNull(claimSetConverter, "claimSetConverter cannot be null");
        this.claimSetConverter = claimSetConverter;
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        JWT jwt = parse(token);
        if (jwt instanceof PlainJWT) {
            throw new JwtException("Unsupported algorithm of " + jwt.getHeader().getAlgorithm());
        }
        Jwt createdJwt = createJwt(token, jwt);
        return validateJwt(createdJwt);
    }

    private JWT parse(String token) {
        try {
            return JWTParser.parse(token);
        } catch (Exception ex) {
            throw new JwtException(String.format(DECODING_ERROR_MESSAGE_TEMPLATE, ex.getMessage()), ex);
        }
    }

    private Jwt createJwt(String token, JWT parsedJwt) {
        try {
            // Verify the signature
            Map<String, Object> headers = new LinkedHashMap<>(parsedJwt.getHeader().toJSONObject());
            Map<String, Object> claims = this.claimSetConverter.convert(getClaims(((SignedJWT) parsedJwt).getPayload().toJSONObject()));

            return Jwt.withTokenValue(token)
                    .headers(h -> h.putAll(headers))
                    .claims(c -> c.putAll(claims))
                    .build();
        } catch (Exception ex) {
            if (ex.getCause() instanceof ParseException) {
                throw new JwtException(String.format(DECODING_ERROR_MESSAGE_TEMPLATE, "Malformed payload"));
            } else {
                throw new JwtException(String.format(DECODING_ERROR_MESSAGE_TEMPLATE, ex.getMessage()), ex);
            }
        }
    }

    private Map<String, Object> getClaims(JSONObject json) {
        Map<String, Object> claims = new HashMap<>();

        json.keySet().forEach(name -> {
            try {
                if (name.equals("iss")) {
                    claims.put("iss", JSONObjectUtils.getString(json, "iss"));
                } else if (name.equals("sub")) {
                    claims.put("sub", JSONObjectUtils.getLong(json, "sub"));
                } else if (name.equals("aud")) {
                    Object audValue = json.get("aud");
                    if (audValue instanceof String) {
                        List<String> singleAud = new ArrayList();
                        singleAud.add(JSONObjectUtils.getString(json, "aud"));
                        claims.put("aud", (List)singleAud);
                    } else if (audValue instanceof List) {
                        claims.put("aud", JSONObjectUtils.getStringList(json, "aud"));
                    } else if (audValue == null) {
                        claims.put("aud", (String)null);
                    }
                } else if (name.equals("exp")) {
                    claims.put("exp", new Date(JSONObjectUtils.getLong(json, "exp") * 1000L));
                } else if (name.equals("nbf")) {
                    claims.put("nbf", new Date(JSONObjectUtils.getLong(json, "nbf") * 1000L));
                } else if (name.equals("iat")) {
                    claims.put("iat", new Date(JSONObjectUtils.getLong(json, "iat") * 1000L));
                } else if (name.equals("jti")) {
                    claims.put("jti", JSONObjectUtils.getString(json, "jti"));
                } else {
                    claims.put(name, json.get(name));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        return claims;
    }

    private Jwt validateJwt(Jwt jwt) {
        OAuth2TokenValidatorResult result = this.jwtValidator.validate(jwt);
        if (result.hasErrors()) {
            Collection<OAuth2Error> errors = result.getErrors();
            String validationErrorString = "Unable to validate Jwt";
            for (OAuth2Error oAuth2Error : errors) {
                if (!StringUtils.isEmpty(oAuth2Error.getDescription())) {
                    validationErrorString = String.format(DECODING_ERROR_MESSAGE_TEMPLATE, oAuth2Error.getDescription());
                    break;
                }
            }
            throw new JwtValidationException(validationErrorString, result.getErrors());
        }

        return jwt;
    }
}

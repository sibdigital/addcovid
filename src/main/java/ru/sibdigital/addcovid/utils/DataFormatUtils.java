package ru.sibdigital.addcovid.utils;

import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Objects;

public class DataFormatUtils {
    public static  ResponseEntity buildResponse(ResponseEntity.BodyBuilder builder, Map<Object, Object> entries){
        String body = "";
        String sentries = entries.entrySet().stream()
                .filter(e -> e.getKey() != null)
                .map(e -> "\"" + e.getKey() + "\" : \"" + Objects.toString(e.getValue(), "") + "\"")
                .reduce((s1, s2) -> s1 + "," + s2)
                .orElse("");
        body = "{" + sentries + "}";
        return builder.body(body);
    }

    public static  ResponseEntity buildResponse(ResponseEntity.BodyBuilder builder, String property, Object value){
        return buildResponse(builder, Map.of(property, value));
    }
}

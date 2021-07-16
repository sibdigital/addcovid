package ru.sibdigital.addcovid.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Objects;

public class DataFormatUtils {
    public static ResponseEntity<String> buildResponse(ResponseEntity.BodyBuilder builder, Map<Object, Object> entries){
        String body = "";
        String sentries = entries.entrySet().stream()
                .filter(e -> e.getKey() != null)
                .map(e -> "\"" + e.getKey() + "\" : \"" + Objects.toString(e.getValue(), "") + "\"")
                .reduce((s1, s2) -> s1 + "," + s2)
                .orElse("");
        body = "{" + sentries + "}";
        final ResponseEntity<String> sb = builder.body(body);
        return sb;
    }

    public static ResponseEntity<String> buildResponse(ResponseEntity.BodyBuilder builder, String property, Object value){
        return buildResponse(builder, Map.of(property, value));
    }

    public static ResponseEntity<String> buildInternalServerErrorResponse(Map<Object, Object> entries){
        return buildResponse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR), entries);
    }

    public static ResponseEntity<String> buildOkResponse(Map<Object, Object> entries){
        return buildResponse(ResponseEntity.ok(), entries);
    }

    //DataFormatUtils.buildResponse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR),
    //                    Map.of("cause", "Не найден id организации", "status", "error", "sname", ""));
}

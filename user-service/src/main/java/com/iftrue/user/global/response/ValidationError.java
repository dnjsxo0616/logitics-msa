package com.iftrue.user.global.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.FieldError;

@Getter
@Builder
@AllArgsConstructor
public class ValidationError {

    private String field;
    private String message;


    public static ValidationError from(FieldError error) {
        return ValidationError.builder()
                .field(error.getField())
                .message(error.getDefaultMessage())
                .build();
    }
}

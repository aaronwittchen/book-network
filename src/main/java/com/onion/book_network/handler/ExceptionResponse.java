package com.onion.book_network.handler;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ExceptionResponse {

    private Integer businessErrorCode;
    private String businessErrorDescription;
    private String error;
    private Set<String> validationErrors;
    private Map<String, String> errors;

    // Helper factory methods for convenience
    public static ExceptionResponse fromBusinessError(int code, String description, String error) {
        return ExceptionResponse.builder()
                .businessErrorCode(code)
                .businessErrorDescription(description)
                .error(error)
                .build();
    }

    public static ExceptionResponse fromBusinessError(BusinessErrorCodes code, String error) {
        return fromBusinessError(code.getCode(), code.getDescription(), error);
    }

    public static ExceptionResponse fromValidationErrors(Set<String> validationErrors) {
        return ExceptionResponse.builder()
                .validationErrors(validationErrors)
                .build();
    }

    public static ExceptionResponse fromFieldErrors(Map<String, String> errors) {
        return ExceptionResponse.builder()
                .errors(errors)
                .build();
    }
}

package com.onion.book_network.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {

    @JsonProperty("email")
    @Email(message = "Email is not well formatted")
    @NotEmpty(message = "Email is mandatory")
    private String email;

    @JsonProperty("password")
    @NotEmpty(message = "Password is mandatory")
    @Size(min = 8, message = "Password should be at least 8 characters long")
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).*$",
        message = "Password must contain at least one uppercase letter, one number, and one special character"
    )
    private String password;

    @JsonProperty("firstName")
    @NotEmpty(message = "First name is mandatory")
    @Size(max = 50, message = "First name must be 50 characters or less")
    private String firstName;

    @JsonProperty("lastName")
    @NotEmpty(message = "Last name is mandatory")
    @Size(max = 50, message = "Last name must be 50 characters or less")
    private String lastName;
}

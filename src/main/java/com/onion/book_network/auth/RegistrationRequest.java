package com.onion.book_network.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {

    @Email(message = "Email is not well formatted")
    @NotEmpty(message = "Email is mandatory")
    private String email;

    @NotEmpty(message = "Password is mandatory")
    @Size(min = 8, message = "Password should be at least 8 characters long")
    @Pattern(
    regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
    message = "Password must contain at least one uppercase letter, one number, and one special character"
)
private String password;

    @NotEmpty(message = "First name is mandatory")
    @Size(max = 50, message = "First name must be 50 characters or less")
    private String firstname;

    @NotEmpty(message = "Last name is mandatory")
    @Size(max = 50, message = "Last name must be 50 characters or less")
    private String lastname;
}

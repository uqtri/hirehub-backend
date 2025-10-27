package org.example.hirehub.dto.auth;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

    private String email;
    private String name;
    private String password;
    private String confirmPassword;
    private Integer foundedYear;
    private Integer numberOfEmployees;
}

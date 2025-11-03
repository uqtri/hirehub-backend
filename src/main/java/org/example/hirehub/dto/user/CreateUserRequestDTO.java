package org.example.hirehub.dto.user;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CreateUserRequestDTO {
    @NotBlank(message = "Email là bắt buôc")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 32, message = "Mật khẩu phải có từ 6 đến 32 ký tự")
    private String password;

    private String numberOfEmployees;
    private Integer foundedYear = 0;
    private String address;
    private Long roleId;
    private List<Long> skillIds;
    private String field;
}

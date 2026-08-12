package com.iftrue.user.presentation.request;

import com.iftrue.user.domain.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class SignUpRequest {

    @NotBlank(message = "아이디는 필수입니다.")
    @Pattern(
            regexp = "^[a-z0-9]{4,10}$",
            message = "아이디는 4~10자의 소문자(a~z)와 숫자(0~9)로만 구성되어야 합니다."
    )
    private String username;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,15}$",
            message = "password는 8~15자이며 대소문자, 숫자, 특수문자를 모두 포함해야 합니다."
    )
    String password;

    @NotBlank(message = "슬랙ID는 필수입니다.")
    String slackId;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    String email;


    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(min = 2, max = 20, message = "닉네임은 2~20자여야 합니다.")
    String nickname;

    @NotNull(message = "role은 필수입니다.")
    UserRole role;

    // HUB_MANAGER, DELIVERY_MANAGER 가입 시 필수
    UUID hubId;

    // SUPPLIER_MANAGER 가입 시 필수
    UUID companyId;
}

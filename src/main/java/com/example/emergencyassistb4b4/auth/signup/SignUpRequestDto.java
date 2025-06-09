package com.example.emergencyassistb4b4.auth.signup;

import com.example.emergencyassistb4b4.user.domain.LoginType;
import com.example.emergencyassistb4b4.user.domain.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpRequestDto {
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private String password; // local만 필수

     private String name; // IND
     private String phoneNumber; // IND

    @NotNull(message = "사용자 역할은 필수 선택값입니다.")
    private UserRole userRole;

    @NotNull
    private LoginType loginType;

    // GOV/NGO 역할에 필요한 추가 정보 (IDV 는 null 허용)
    private String businessNumber; // 사업자 번호
    private String organizationName; //기관, 단체명

}

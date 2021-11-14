package cloneproject.Instagram.dto.member;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    
    @NotBlank(message = "ID를 입력해주세요")
    @Length(min = 4, max = 12, message = "ID는 4문자 이상 12문자 이하여야 합니다")
    private String username;

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Length(max = 20, message = "비밀번호는 20문자 이하여야 합니다")
    private String password;

}

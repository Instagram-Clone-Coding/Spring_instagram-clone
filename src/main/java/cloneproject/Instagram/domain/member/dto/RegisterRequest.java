package cloneproject.Instagram.domain.member.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import cloneproject.Instagram.domain.member.entity.Member;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    
    @ApiModelProperty(value = "유저네임", example = "dlwlrma", required = true)
    @NotBlank(message = "username을 입력해주세요")
    @Length(min = 4, max = 12, message = "사용자 이름은 4문자 이상 12문자 이하여야 합니다")
    @Pattern(regexp = "^[0-9a-zA-Z]+$", message = "username엔 대소문자, 숫자만 사용할 수 있습니다.")
    private String username;
    
    @ApiModelProperty(value = "이름", example = "이지금", required = true)
    @NotBlank(message = "이름을 입력해주세요")
    @Length(min = 2, max = 12, message = "이름은 2문자 이상 12문자 이하여야 합니다")
    private String name;

    @ApiModelProperty(value = "비밀번호", example = "a12341234", required = true)
    @NotBlank(message = "비밀번호를 입력해주세요")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", 
                message = "비밀번호는 8자 이상, 최소 하나의 문자와 숫자가 필요합니다")
    @Length(max = 20, message = "비밀번호는 20문자 이하여야 합니다")
    private String password;
    
    @ApiModelProperty(value = "이메일", example = "aaa@gmail.com", required = true)
    @NotBlank(message = "이메일을 입력해주세요")
    @Email(message = "이메일의 형식이 맞지 않습니다")
    String email;

    @ApiModelProperty(value = "이메일 인증코드", example = "ABC123", required = true)
    @NotBlank(message = "이메일 인증코드를 입력해주세요")
    @Length(max = 6, min = 6, message = "인증코드는 6자리 입니다.")
    String code;
    
    public Member convert(){
        return Member.builder()
                    .username(getUsername())
                    .name(getName())
                    .password(getPassword())
                    .email(getEmail())
                    .build();
    }    

}

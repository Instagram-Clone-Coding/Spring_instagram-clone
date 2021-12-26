package cloneproject.Instagram.dto.member;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import cloneproject.Instagram.entity.member.Member;
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
    @Length(min = 4, max = 12, message = "ID는 4문자 이상 12문자 이하여야 합니다")
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

    @ApiModelProperty(value = "휴대폰 번호", example = "010-0000-0000", required = true)
    @NotBlank(message = "휴대폰 번호를 입력해주세요")
    @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$", message = "휴대폰 번호 양식이 맞지 않습니다")
    private String phone;
    
    public Member convert(){
        return Member.builder()
                    .username(getUsername())
                    .name(getName())
                    .password(getPassword())
                    .phone(getPhone())
                    .build();
    }    

}

package cloneproject.Instagram.domain.member.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordRequest {

    @ApiModelProperty(value = "이전비밀번호", example = "b12341234", required = true)
    @NotBlank(message = "이전비밀번호를 입력해주세요")
    @Length(max = 20, message = "비밀번호는 20문자 이하여야 합니다")
    private String oldPassword;
    
    
    @ApiModelProperty(value = "새비밀번호", example = "a12341234", required = true)
    @NotBlank(message = "새비밀번호를 입력해주세요")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", 
                message = "비밀번호는 8자 이상, 최소 하나의 문자와 숫자가 필요합니다")
    @Length(max = 20, message = "비밀번호는 20문자 이하여야 합니다")
    private String newPassword;
    
}

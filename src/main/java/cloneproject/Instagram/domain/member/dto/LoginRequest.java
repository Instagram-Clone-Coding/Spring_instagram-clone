package cloneproject.Instagram.domain.member.dto;

import javax.validation.constraints.NotBlank;

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
public class LoginRequest {

	@ApiModelProperty(value = "유저네임", example = "dlwlrma", required = true)
	@NotBlank(message = "ID를 입력해주세요")
	@Length(min = 4, max = 12, message = "ID는 4문자 이상 12문자 이하여야 합니다")
	private String username;

	@ApiModelProperty(value = "비밀번호", example = "a12341234", required = true)
	@NotBlank(message = "비밀번호를 입력해주세요")
	@Length(max = 20, message = "비밀번호는 20문자 이하여야 합니다")
	private String password;

}

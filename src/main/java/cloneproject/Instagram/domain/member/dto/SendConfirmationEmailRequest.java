package cloneproject.Instagram.domain.member.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendConfirmationEmailRequest {

	@ApiModelProperty(value = "유저네임", example = "dlwlrma", required = true)
	@NotBlank(message = "username을 입력해주세요")
	@Length(min = 4, max = 12, message = "사용자 이름은 4문자 이상 12문자 이하여야 합니다")
	@Pattern(regexp = "^[0-9a-zA-Z]+$", message = "username엔 대소문자, 숫자만 사용할 수 있습니다.")
	String username;

	@ApiModelProperty(value = "이메일", example = "aaa@gmail.com", required = true)
	@NotBlank(message = "이메일을 입력해주세요")
	@Email(message = "이메일의 형식이 맞지 않습니다")
	String email;

}

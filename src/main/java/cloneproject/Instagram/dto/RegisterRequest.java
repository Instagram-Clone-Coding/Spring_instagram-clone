package cloneproject.Instagram.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import cloneproject.Instagram.entity.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// TODO valdiation
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    
    @NotBlank(message = "ID를 입력해주세요")
    private String userid;
    
    @NotBlank(message = "이름을 입력해주세요")
    private String username;

    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;

    @NotBlank(message = "휴대폰 번호를 입력해주세요")
    private String phone;
    
    public Member convert(){
        return Member.builder()
                    .userid(getUserid())
                    .username(getUsername())
                    .password(getPassword())
                    .phone(getPhone())
                    .build();
    }    

}

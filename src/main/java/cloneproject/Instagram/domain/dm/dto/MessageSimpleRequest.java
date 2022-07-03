package cloneproject.Instagram.domain.dm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MessageSimpleRequest {

    @NotNull(message = "메시지 PK는 필수입니다.")
    private Long messageId;

    @NotNull(message = "회원 PK는 필수입니다.")
    private Long memberId;

}

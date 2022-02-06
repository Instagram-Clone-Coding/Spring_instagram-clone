package cloneproject.Instagram.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IndicateDTO {

    private Long roomId;
    private Long senderId;
    private Long ttl;
}

package cloneproject.Instagram.dto.member;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtDto {
    private String type;
    private String accessToken;
    private String refreshToken;
    private Date accessTokenExpires;
}

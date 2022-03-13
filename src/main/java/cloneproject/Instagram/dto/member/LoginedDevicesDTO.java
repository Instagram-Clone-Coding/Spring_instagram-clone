package cloneproject.Instagram.dto.member;


import cloneproject.Instagram.vo.GeoIP;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class LoginedDevicesDTO {
    
    private String tokenId;
    private GeoIP location;
    private String device;

}

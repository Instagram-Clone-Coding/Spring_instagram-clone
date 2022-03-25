package cloneproject.Instagram.domain.member.dto;


import cloneproject.Instagram.infra.geoip.dto.GeoIP;
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

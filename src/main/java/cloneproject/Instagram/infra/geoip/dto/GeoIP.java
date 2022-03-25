package cloneproject.Instagram.infra.geoip.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeoIP {
    
    private String city;
    private String longitude;
    private String latitude;

}

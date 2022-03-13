package cloneproject.Instagram.entity.member;

import lombok.*;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import cloneproject.Instagram.vo.GeoIP;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;


@RedisHash("refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken implements Serializable{

    @Id
    @Indexed
    private String id;
    // Long으로 하면 find 쿼리가 정상 작동하지 않음

    @Indexed
    private String value;

    @Indexed
    private Long memberId;

    @TimeToLive(unit = TimeUnit.DAYS)
    private Long timeout = 7L;

    private LocalDateTime createdAt;

    private String city;

    private String longitude;

    private String latitude;

    private String device;

    public void updateToken(String newValue){
        this.value = newValue;  
    }

    public GeoIP getGeoIP(){
        return new GeoIP(city, longitude, latitude);
    }

    @Builder
    public RefreshToken(Long memberId, String value, GeoIP geoip, String device){
        this.createdAt = LocalDateTime.now();
        this.memberId = memberId;
        this.value = value;
        this.city = geoip.getCity();
        this.longitude = geoip.getLongitude();
        this.latitude = geoip.getLatitude();
        this.device = device;
    }

}

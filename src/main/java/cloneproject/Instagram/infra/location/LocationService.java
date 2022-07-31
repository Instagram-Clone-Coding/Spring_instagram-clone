package cloneproject.Instagram.infra.location;

import java.net.InetAddress;

import org.springframework.stereotype.Service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;

import lombok.RequiredArgsConstructor;

import cloneproject.Instagram.infra.kakao.KakaoMap;
import cloneproject.Instagram.infra.location.dto.Location;

@Service
@RequiredArgsConstructor
public class LocationService {

	private final DatabaseReader databaseReader;
	private final KakaoMap kakaoMap;

	public Location getLocation(String ip) {
		try {
			final InetAddress ipAddress = InetAddress.getByName(ip);
			final CityResponse response = databaseReader.city(ipAddress);
			final String city = kakaoMap.getCity(response.getLocation().getLongitude().toString(),
				response.getLocation().getLatitude().toString());
			return new Location(
				city,
				response.getLocation().getLongitude().toString(),
				response.getLocation().getLatitude().toString());
		} catch (Exception e) {
			return new Location("Unknown", "0", "0");
		}
	}

}

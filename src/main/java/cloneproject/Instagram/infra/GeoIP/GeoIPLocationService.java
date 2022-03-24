package cloneproject.Instagram.infra.GeoIP;

import java.net.InetAddress;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GeoIPLocationService {

    private final DatabaseReader databaseReader;

    public GeoIP getLocation(String ip){
        try{
            InetAddress ipAddress = InetAddress.getByName(ip);
            CityResponse response = databaseReader.city(ipAddress);
            
            return new GeoIP(
                response.getCity().getName(), 
                response.getLocation().getLongitude().toString(), 
                response.getLocation().getLatitude().toString()
            );
        }catch(Exception e){
            return new GeoIP("Unknown", "0", "0");
        }
    }

}

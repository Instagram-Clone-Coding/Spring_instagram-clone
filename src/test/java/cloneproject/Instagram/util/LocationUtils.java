package cloneproject.Instagram.util;

import org.apache.commons.lang3.RandomStringUtils;

import cloneproject.Instagram.infra.location.dto.Location;

public class LocationUtils {

	public static Location newInstance() {
		final String city = RandomStringUtils.random(10, true, false);
		final String longitude = RandomStringUtils.random(3, false, true);
		final String latitude = RandomStringUtils.random(3, false, true);
		return of(city, longitude, latitude);
	}

	public static Location of(String city, String longitude, String latitude) {
		return Location.builder()
			.city(city)
			.longitude(longitude)
			.latitude(latitude)
			.build();
	}

}

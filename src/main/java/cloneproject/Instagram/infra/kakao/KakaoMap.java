package cloneproject.Instagram.infra.kakao;

import java.io.IOException;
import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import cloneproject.Instagram.infra.kakao.dto.KakaoSearchResponse;
import cloneproject.Instagram.infra.kakao.exception.KakaoMapApiFailException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KakaoMap {

	@Value("${kakao.rest.key}")
	private String kakaoKey;

	private final RestTemplate restTemplate = new RestTemplate();
	private final ObjectMapper objectMapper = new ObjectMapper();

	public String getCity(String longitude, String latitude) {
		URI uri = UriComponentsBuilder
			.fromUriString("https://dapi.kakao.com")
			.path("/v2/local/geo/coord2address.json")
			.queryParam("x", longitude)
			.queryParam("y", latitude)
			.queryParam("input_coord", "WGS84")
			.encode()
			.build()
			.toUri();

		RequestEntity<Void> req = RequestEntity
			.get(uri)
			.header("Host", "dapi.kakao.com")
			.header("Authorization", "KakaoAK " + kakaoKey)
			.build();

		ResponseEntity<String> result = restTemplate.exchange(req, String.class);
		try {
			final KakaoSearchResponse kakaoSearchResponse = objectMapper.readValue(result.getBody(),
				KakaoSearchResponse.class);
			return kakaoSearchResponse.getDocuments()[0].getRoadAddress().getRegion1DepthName() + " "
				+ kakaoSearchResponse.getDocuments()[0].getRoadAddress().getRegion2DepthName();
		} catch (IOException e) {
			throw new KakaoMapApiFailException();
		} catch (NullPointerException e) {
			return "Unknown";
		}
	}

}

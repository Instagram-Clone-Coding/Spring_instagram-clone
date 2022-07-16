package cloneproject.Instagram.infra.kakao.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoSearchResponse {
    
    private Document[] documents;

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Document {
        
        @JsonProperty("road_address")
        private RoadAddress roadAddress;

        @Getter
        @NoArgsConstructor(access = AccessLevel.PROTECTED)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class RoadAddress {
            @JsonProperty("region_1depth_name")
            private String region1DepthName;
            @JsonProperty("region_2depth_name")
            private String region2DepthName;
        }

    }

}

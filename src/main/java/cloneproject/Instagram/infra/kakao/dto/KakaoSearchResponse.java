package cloneproject.Instagram.infra.kakao.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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
        
        @JsonProperty("address")
        private Address address;

        @Getter
        @NoArgsConstructor(access = AccessLevel.PROTECTED)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Address {
            @JsonProperty("region_1depth_name")
            private String region1DepthName;
            @JsonProperty("region_2depth_name")
            private String region2DepthName;
        }

    }

}

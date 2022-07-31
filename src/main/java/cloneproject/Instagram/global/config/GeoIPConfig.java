package cloneproject.Instagram.global.config;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.maxmind.geoip2.DatabaseReader;

@Configuration
public class GeoIPConfig {

	@Bean
	public DatabaseReader databaseReader() throws Exception {
		ClassPathResource classPathResource = new ClassPathResource("GeoLite2-City.mmdb");
		InputStream inputStream = classPathResource.getInputStream();
		File database = File.createTempFile("GL2", ".mmdb");
		FileUtils.copyInputStreamToFile(inputStream, database);
		inputStream.close();
		return new DatabaseReader.Builder(database).build();
	}

}

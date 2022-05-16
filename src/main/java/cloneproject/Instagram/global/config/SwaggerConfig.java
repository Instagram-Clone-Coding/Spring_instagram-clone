package cloneproject.Instagram.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@EnableSwagger2
@Configuration
public class SwaggerConfig {

	@Bean
	public Docket api() {
		// Authorization header 글로벌 처리
		final List<Parameter> global = new ArrayList<>();
		global.add(new ParameterBuilder()
			.name("Authorization")
			.description("Access Token")
			.parameterType("header")
			.required(false)
			.modelRef(new ModelRef("string"))
			.scalarExample("Bearer AAA.BBB.CCC")
			.build());

		final List<ResponseMessage> responseMessages = new ArrayList<>();
		responseMessages.add(new ResponseMessageBuilder().code(405)
			.message("G002 - 허용되지 않은 HTTP method입니다.").build());
		responseMessages.add(new ResponseMessageBuilder().code(500)
			.message("G001 - 내부 서버 오류입니다.").build());

		return new Docket(DocumentationType.SWAGGER_2)
			.globalOperationParameters(global)
			.useDefaultResponseMessages(false)
			.globalResponseMessage(RequestMethod.POST, responseMessages)
			.globalResponseMessage(RequestMethod.GET, responseMessages)
			.globalResponseMessage(RequestMethod.DELETE, responseMessages)
			.globalResponseMessage(RequestMethod.PUT, responseMessages)
			.apiInfo(apiInfo())
			.select()
			.apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
			.paths(PathSelectors.any())
			.build();
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
			.title("Instagram's API Docs")
			.version("1.0")
			.description("API 명세서")
			.build();
	}

}

package com.poc.interfaces.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author sravantatikonda
 */
@Configuration
@EnableSwagger2
@Slf4j
public class SwaggerConfig implements WebMvcConfigurer {

  private static final String BASE_PACKAGE = "com.poc.interfaces.controller";

  @Bean
  public Docket api() {
    log.debug("Starting Swagger");
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(metaData())
        .select()
        .apis(RequestHandlerSelectors.basePackage(BASE_PACKAGE))
        .paths(PathSelectors.regex("/.*"))
        .build();
  }

  private ApiInfo metaData() {
    Contact contact = new Contact(
        "Sravan Tatikonda",
        "https://poc-utils.com",
        "sravan.tatikonda@test.com");
    return new ApiInfoBuilder().title("Interface UTIL APIs")
        .description("\" Rest APIs forInterfaces\"")
        .version("1.0")
        .contact(contact).build();
  }
}

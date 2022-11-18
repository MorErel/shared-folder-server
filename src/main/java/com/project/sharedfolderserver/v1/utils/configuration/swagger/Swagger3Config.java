//package com.project.sharedfolderserver.v1.utils.configuration.swagger;
//package com.project.sharedfolderserver.v1.utils.configuration.swagger;
//
//
//import com.google.common.base.Predicate;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.Contact;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import springfox.documentation.RequestHandler;
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//
//@Configuration
//@ConditionalOnProperty(value = "springfox.documentation.enabled", havingValue = "true", matchIfMissing = true)
//class Swagger3Config {
//    @Bean
//    public Docket createRestApi() {
//        return new Docket(DocumentationType.OAS_30)
//                .apiInfo(apiInfo())
//                .select()
//                .apis((java.util.function.Predicate<RequestHandler>) RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
//                .paths((java.util.function.Predicate<String>) PathSelectors.any())
//                .build();
//    }
//
//    private ApiInfo apiInfo() {
//        return new ApiInfoBuilder()
//                .title("Swagger3接口文档")
//                .description("更多请咨询felord.cn")
//                .version("1.0.0")
//                .build();
//    }
//}


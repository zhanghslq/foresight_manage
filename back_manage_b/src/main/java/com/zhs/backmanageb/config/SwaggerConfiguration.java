package com.zhs.backmanageb.config;
import com.fasterxml.classmate.TypeResolver;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

@Configuration
@EnableSwagger2
@EnableKnife4j
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfiguration {
    private final TypeResolver typeResolver;

    @Autowired
    public SwaggerConfiguration(TypeResolver typeResolver) {
        this.typeResolver = typeResolver;
    }
    @Bean(value = "defaultApi")
    public Docket defaultApi() {
        ParameterBuilder parameterBuilder=new ParameterBuilder();
        List<Parameter> parameters= Lists.newArrayList();
        parameterBuilder.name("token").description("token令牌").modelRef(new ModelRef("String"))
                .parameterType("header")
                .required(true).build();
        // parameters.add(parameterBuilder.build());

        Docket docket=new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName("远见后台管理B系统")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.zhs.backmanageb"))
                .paths(PathSelectors.any())
                .build().globalOperationParameters(parameters)
                .securityContexts(Lists.newArrayList(securityContext())).securitySchemes(Lists.<SecurityScheme>newArrayList(apiKey()));
        return docket;
    }
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("远见后台管理的接口文档")
                .description("远见后台管理的接口文档")
                .termsOfServiceUrl("http://localhost:8080/")
                .version("1.0")
                .build();
    }

    private ApiKey apiKey() {
        return new ApiKey("BearerToken", "Authorization", "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("/.*"))
                .build();
    }


    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Lists.newArrayList(new SecurityReference("BearerToken", authorizationScopes));
    }

}

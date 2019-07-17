package org.bestfeng.template;

import org.hswebframework.web.authorization.Authentication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wangzheng
 * @see
 * @since 1.0
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    private List<Parameter> createParameters(){
        ParameterBuilder customerUserToken=new ParameterBuilder()
                .parameterType("header")
                .name("app-token")
                .description("认证TOKEN")
                .modelRef(new ModelRef("string"));

        List<Parameter> parameters = new ArrayList<>();
        parameters.add(customerUserToken.build());
        return parameters;
    }

    @Bean
    public Docket createDeviceApi() {
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("文件服务API")
                .description("")
                .version("1.0")
                .build();
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo)
                .groupName("file-api")
                .ignoredParameterTypes(HttpSession.class, Authentication.class, HttpServletRequest.class, HttpServletResponse.class)
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.hswebframework.web"))
                .paths(PathSelectors.ant("/file/upload-static"))
//                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(createParameters());
    }
}

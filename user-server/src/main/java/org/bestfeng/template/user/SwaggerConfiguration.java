package org.bestfeng.template.user;

import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bestfeng
 * @see
 * @since 1.0
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    public List<Parameter>  createParameters(){
        ParameterBuilder customerUsertoken = new ParameterBuilder()
                .parameterType("header")
                .name("app-token")
                .description("认证TOKEN")
                .modelRef(new ModelRef("string"));
        ParameterBuilder userToken = new ParameterBuilder()
                .parameterType("header")
                .name("template-user")
                .description("认证TOKEN")
                .modelRef(new ModelRef("string"));
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(customerUsertoken.build());
        parameters.add(userToken.build());
        return parameters;
    }
 /*   @Bean
    public Docket createDeviceApi() {
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("用户服务API")
                .description("")
                .version("1.0")
                .build();
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo)
                .groupName("user-api")
                .ignoredParameterTypes(HttpSession.class, Authentication.class, HttpServletRequest.class, HttpServletResponse.class)
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.bsetfeng.template.user"))
                .paths(PathSelectors.ant("/app/**"))
                //.paths(PathSelectors.any())
                .build()
                .globalOperationParameters( createParameters())
                ;
    }
*/

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("hsweb 3.0 api")
                .description("hsweb 企业后台管理基础框架")
                .termsOfServiceUrl("http://www.hsweb.me/")
                .license("apache 2.0")
                .version("3.0")
                .build();
    }
}

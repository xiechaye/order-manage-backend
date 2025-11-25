package chaye.com.ordermanage.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j配置 - 简化版本适配Spring Boot 3
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("订单管理后台API")
                        .description("订单管理后台API文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("系统管理员")
                                .email("admin@example.com")));
    }
}
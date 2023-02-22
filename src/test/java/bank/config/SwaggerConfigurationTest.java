package bank.config;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apiguardian.api.API;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@EnableWebMvc
@Configuration
public class SwaggerConfigurationTest {

    @Test
    public void testClientAPI() {
        // Cria um objeto do tipo SwaggerConfiguration para teste
        SwaggerConfiguration configuration = new SwaggerConfiguration();
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("Simulacao de Banco para a ZUP")
                .version("1.0.0")
                .build();

        // Cria um objeto Docket simulado para teste
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("bank.BankApplication"))
                .build()
                .apiInfo(apiInfo);

        assertNotNull(docket);
    }

        @Test
        public void testApiInfo() {
        // Cria um objeto do tipo SwaggerConfiguration para teste
        SwaggerConfiguration configuration = new SwaggerConfiguration();

        // Verifica se o método apiInfo() retorna um objeto ApiInfo com o título e a versão corretos
        assertEquals("Simulacao de Banco para a ZUP", configuration.apiInfo().getTitle());
        assertEquals("1.0.0", configuration.apiInfo().getVersion());
    }

}
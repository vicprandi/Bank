package bank.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.Assert;

import java.util.List;

public class CustomJwtAuthenticationConverterTest {

    @Test
    public void testConvert() {
        CustomJwtAuthenticationConverter converter = new CustomJwtAuthenticationConverter();

        // Crie um Jwt mock com alguns dados de exemplo
        Jwt jwt = Jwt.withTokenValue("tokenValue")
                .header("header", "value")
                .claim("roles", List.of("ROLE_admin", "ROLE_user"))
                .build();

        JwtAuthenticationToken result = (JwtAuthenticationToken) converter.convert(jwt);

        Assert.notNull(result, "The result must not be null");
        Assert.isTrue(result.getAuthorities().size() == 2, "The number of authorities must be 2");
    }
}

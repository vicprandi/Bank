package bank.security;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public AbstractAuthenticationToken convert(@NotNull Jwt jwt) {
        // Adiciona as authorities do JWT
        Collection<? extends GrantedAuthority> jwtAuthorities = jwtGrantedAuthoritiesConverter.convert(jwt);
        List<GrantedAuthority> authorities = new ArrayList<>(jwtAuthorities);

        // Adiciona as authorities do grupo "roles"
        List<String> roles = jwt.getClaimAsStringList("roles");
        if (roles != null) {
            List<SimpleGrantedAuthority> roleAuthorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority(role.toUpperCase())) // certifique-se que as autoridades estão em maiúsculas
                    .toList();
            authorities.addAll(roleAuthorities);
        }
        return new JwtAuthenticationToken(jwt, authorities);
    }
}
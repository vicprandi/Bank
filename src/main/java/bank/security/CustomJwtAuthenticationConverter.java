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

public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public AbstractAuthenticationToken convert(@NotNull Jwt jwt) {

        // Adiciona as authorities do JWT
        Collection<? extends GrantedAuthority> jwtAuthorities = jwtGrantedAuthoritiesConverter.convert(jwt);
        List<GrantedAuthority> authorities = new ArrayList<>(jwtAuthorities);

        // Adiciona as authorities do grupo "groups"
        List<String> groups = jwt.getClaimAsStringList("groups");
        if (groups != null) {
            List<SimpleGrantedAuthority> groupAuthorities = groups.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();
            authorities.addAll(groupAuthorities);
        }
        return new JwtAuthenticationToken(jwt, authorities);
    }
}
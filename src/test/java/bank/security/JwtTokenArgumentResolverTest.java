package bank.security;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.Assert;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;

import java.lang.reflect.Method;

public class JwtTokenArgumentResolverTest {

    @Test
    public void testResolveArgument() throws Exception {
        JwtTokenArgumentResolver resolver = new JwtTokenArgumentResolver();

        // Um método de exemplo que retorna Jwt
        Method method = this.getClass().getDeclaredMethod("methodExample");
        MethodParameter parameter = new MethodParameter(method, -1);

        ModelAndViewContainer mavContainer = Mockito.mock(ModelAndViewContainer.class);
        NativeWebRequest webRequest = Mockito.mock(NativeWebRequest.class);
        WebDataBinderFactory binderFactory = Mockito.mock(WebDataBinderFactory.class);

        Authentication authentication = Mockito.mock(Authentication.class);
        Jwt jwt = Mockito.mock(Jwt.class);
        Mockito.when(authentication.getDetails()).thenReturn(jwt);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Object result = resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

        Assert.notNull(result, "The result must not be null");
        Assert.isTrue(result instanceof Jwt, "The result must be a Jwt instance");
    }

    // Método exemplo para criar o MethodParameter
    public Jwt methodExample() {
        return null;
    }
}

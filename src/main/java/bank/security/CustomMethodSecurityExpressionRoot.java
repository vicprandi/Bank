package bank.security;

import bank.security.exceptions.CustomAuthorizationException;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;

import java.util.Arrays;

public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot
        implements MethodSecurityExpressionOperations {

    private Object filterObject;
    private Object returnObject;

    public CustomMethodSecurityExpressionRoot(Authentication authentication) {
        super(authentication);
    }

    public boolean hasScope(String... scopes) {
        boolean hasAnyScope = Arrays.stream(scopes)
                .anyMatch(scope -> getAuthentication().getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals(scope)));

        if (!hasAnyScope) {
            throw new CustomAuthorizationException("Acesso negado");
        }
        return true;
    }

    @Bean
    public MethodSecurityExpressionHandler expressionHandler() {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler() {
            @Override
            public MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {
                CustomMethodSecurityExpressionRoot root = new CustomMethodSecurityExpressionRoot(authentication);
                root.setPermissionEvaluator(getPermissionEvaluator());
                root.setTrustResolver(new AuthenticationTrustResolverImpl());
                root.setRoleHierarchy(getRoleHierarchy());
                root.setDefaultRolePrefix(getDefaultRolePrefix());
                return root;
            }
        };
        return handler;
    }

    @Override
    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }

    @Override
    public Object getFilterObject() {
        return this.filterObject;
    }

    @Override
    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    @Override
    public Object getReturnObject() {
        return this.returnObject;
    }

    @Override
    public Object getThis() {
        return this;
    }
}

package com.fiap.techchallenge5.infrastructure.security;

import com.fiap.techchallenge5.useCase.token.TokenUseCase;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    final TokenUseCase service;

    public SecurityFilter(final TokenUseCase service){
        this.service = service;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final var token = this.recuperaToken(request);
        if(Objects.nonNull(token)){
            final var jwt = this.service.pegaJwt(token);
            if(Objects.nonNull(jwt)){

                final var usuario = this.service.pegaUsuario(jwt.getSubject());
                final var authentication = new UsernamePasswordAuthenticationToken(
                        usuario,
                        null,
                        usuario.getAuthorities()
                );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
            }
            else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token inválido");
                return;
            }

        }
        filterChain.doFilter(request, response);
    }

    private String recuperaToken(final HttpServletRequest request){
        final var tokenNoHeader = request.getHeader("Authorization");
        if(Objects.nonNull(tokenNoHeader)) {
            return tokenNoHeader.replace("Bearer ", "");
        }
        return null;
    }

}
package com.fiap.techchallenge5.useCase.token.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fiap.techchallenge5.infrastructure.usuario.model.UsuarioEntity;
import com.fiap.techchallenge5.infrastructure.usuario.repository.UsuarioRepository;
import com.fiap.techchallenge5.useCase.token.TokenUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


@Service
@Slf4j
public class TokenUseCaseImpl implements TokenUseCase {

    @Value("${api.security.token.secret}")
    private String secret;

    final UsuarioRepository repository;

    public TokenUseCaseImpl(final UsuarioRepository repository){
        this.repository = repository;
    }

    @Override
    public DecodedJWT pegaJwt(String token) {
        try {
            final var algorithm = Algorithm.HMAC256(this.secret);
            return JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token);
        } catch (Exception error) {
            log.error("Erro ao decodificar o token", error);
            return null;
        }
    }

    @Override
    public UserDetails pegaUsuario(final String login) {
        return this.repository.findByLogin(login);
    }

    @Override
    public String geraToken(final UsuarioEntity usuarioAutenticado) {
        try {
            final var algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("auth-api")
                    .withSubject(usuarioAutenticado.getLogin())
                    .withClaim("role", usuarioAutenticado.getRole().name())
                    .withExpiresAt(this.criaDataDeExpiracao())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro para gerar o token", exception);
        }
    }

    private Instant criaDataDeExpiracao(){
        return LocalDateTime.now()
                .plusHours(2)
                .toInstant(ZoneOffset.of("-03:00"));
    }

}

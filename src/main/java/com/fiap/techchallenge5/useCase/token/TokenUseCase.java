package com.fiap.techchallenge5.useCase.token;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fiap.techchallenge5.infrastructure.usuario.model.UsuarioEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface TokenUseCase {

    DecodedJWT pegaJwt(String token);

    UserDetails pegaUsuario(final String login);

    String geraToken(final UsuarioEntity usuarioAutenticado);

}

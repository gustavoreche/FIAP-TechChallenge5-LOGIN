package com.fiap.techchallenge5.useCase.token.impl;

import com.fiap.techchallenge5.infrastructure.token.controller.dto.LoginDTO;
import com.fiap.techchallenge5.infrastructure.usuario.model.UsuarioEntity;
import com.fiap.techchallenge5.useCase.token.AuthUseCase;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthUseCaseImpl implements AuthUseCase {

    private final AuthenticationManager authManager;

    public AuthUseCaseImpl(final AuthenticationManager authManager) {
        this.authManager = authManager;
    }

    @Override
    public UsuarioEntity pegaUsuarioAutenticado(final LoginDTO login) {
        final var usuarioESenha = new UsernamePasswordAuthenticationToken(
                login.login(),
                login.senha()
        );

        final var auth = this.authManager.authenticate(usuarioESenha);
        return (UsuarioEntity) auth.getPrincipal();
    }

}

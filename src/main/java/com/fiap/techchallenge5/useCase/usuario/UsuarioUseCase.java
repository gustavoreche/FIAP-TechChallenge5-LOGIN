package com.fiap.techchallenge5.useCase.usuario;

import com.fiap.techchallenge5.infrastructure.usuario.controller.dto.AtualizaUsuarioDTO;
import com.fiap.techchallenge5.infrastructure.usuario.controller.dto.CriaUsuarioDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UsuarioUseCase extends UserDetailsService {

    boolean cadastra(final CriaUsuarioDTO dadosUsuario);

    boolean atualiza(final String login,
                     final AtualizaUsuarioDTO dadosUsuario);

    boolean deleta(final String login);

    boolean existe(final String login);

}

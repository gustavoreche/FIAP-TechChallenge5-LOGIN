package com.fiap.techchallenge5.infrastructure.usuario.controller.dto;

import com.fiap.techchallenge5.domain.token.UsuarioRoleEnum;

public record CriaUsuarioDTO(
        String login,
        String senha
) {
}

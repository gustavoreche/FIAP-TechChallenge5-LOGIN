package com.fiap.techchallenge5.useCase.token;

import com.fiap.techchallenge5.infrastructure.token.controller.dto.LoginDTO;
import com.fiap.techchallenge5.infrastructure.usuario.model.UsuarioEntity;

public interface AuthUseCase {

    UsuarioEntity pegaUsuarioAutenticado(final LoginDTO login);

}

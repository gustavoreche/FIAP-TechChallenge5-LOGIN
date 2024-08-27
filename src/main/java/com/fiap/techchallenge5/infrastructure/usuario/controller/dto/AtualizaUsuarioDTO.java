package com.fiap.techchallenge5.infrastructure.usuario.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fiap.techchallenge5.domain.token.UsuarioRoleEnum;

public record AtualizaUsuarioDTO(

		@JsonInclude(JsonInclude.Include.NON_NULL)
		String senha,

		@JsonInclude(JsonInclude.Include.NON_NULL)
		UsuarioRoleEnum role
) {}

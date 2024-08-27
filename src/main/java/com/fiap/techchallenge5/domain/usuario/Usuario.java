package com.fiap.techchallenge5.domain.usuario;

import com.fiap.techchallenge5.domain.token.UsuarioRoleEnum;

import java.util.Objects;


public record Usuario(
        String login,
        String senha,
        UsuarioRoleEnum role
) {

        public Usuario {
            if (Objects.isNull(senha) || senha.isEmpty()) {
                throw new IllegalArgumentException("SENHA NAO PODE SER NULO OU VAZIO!");
            }
            if (senha.length() < 3 || senha.length() > 50) {
                throw new IllegalArgumentException("A SENHA deve ter no mínimo 3 letras e no máximo 50 letras");
            }

            if (Objects.isNull(role)) {
                throw new IllegalArgumentException("ROLE NAO PODE SER NULO OU VAZIO!");
            }

            login = new Login(login).id();

        }
}

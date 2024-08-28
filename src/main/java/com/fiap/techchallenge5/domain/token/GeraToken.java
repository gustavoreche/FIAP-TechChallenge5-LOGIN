package com.fiap.techchallenge5.domain.token;

import com.fiap.techchallenge5.domain.usuario.Login;

import java.util.Objects;

public record GeraToken(
        String login,
        String senha
) {
    public GeraToken {
        if (Objects.isNull(login) || login.isEmpty()) {
            throw new IllegalArgumentException("LOGIN NAO PODE SER NULO OU VAZIO!");
        }
        if (login.length() < 3 || login.length() > 50) {
            throw new IllegalArgumentException("O LOGIN deve ter no mínimo 3 letras e no máximo 50 letras");
        }

        if (Objects.isNull(senha) || senha.isEmpty()) {
            throw new IllegalArgumentException("SENHA NAO PODE SER NULO OU VAZIO!");
        }
        if (senha.length() < 3 || senha.length() > 50) {
            throw new IllegalArgumentException("A SENHA deve ter no mínimo 3 letras e no máximo 50 letras");
        }

    }
}

package com.fiap.techchallenge5.domain.usuario;

import java.util.Objects;


public record Login(
        String id
) {

        public Login {
            if (Objects.isNull(id) || id.isEmpty()) {
                throw new IllegalArgumentException("LOGIN NAO PODE SER NULO OU VAZIO!");
            }
            if (id.length() < 3 || id.length() > 50) {
                throw new IllegalArgumentException("O LOGIN deve ter no mínimo 3 letras e no máximo 50 letras");
            }

        }
}

package com.fiap.techchallenge5.unitario;

import com.fiap.techchallenge5.domain.token.UsuarioRoleEnum;
import com.fiap.techchallenge5.infrastructure.token.controller.TokenController;
import com.fiap.techchallenge5.infrastructure.token.controller.dto.LoginDTO;
import com.fiap.techchallenge5.infrastructure.usuario.model.UsuarioEntity;
import com.fiap.techchallenge5.useCase.token.impl.AuthUseCaseImpl;
import com.fiap.techchallenge5.useCase.token.impl.TokenUseCaseImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;

public class TokenControllerTest {

    @Test
    public void cadastra_deveRetornar201_salvaNaBaseDeDados() {
        // preparação
        var serviceAuth = Mockito.mock(AuthUseCaseImpl.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);
        Mockito.when(serviceAuth.pegaUsuarioAutenticado(
                                any(LoginDTO.class)
                        )
                )
                .thenReturn(
                        new UsuarioEntity(
                                "testeLogin",
                                "testeSenha",
                                UsuarioRoleEnum.USER
                        )
                );

        Mockito.when(serviceToken.geraToken(
                                any(UsuarioEntity.class)
                        )
                )
                .thenReturn(
                        "testeToken"
                );

        var controller = new TokenController(serviceAuth, serviceToken);

        // execução
        var cliente = controller.geraToken(
                any(LoginDTO.class)
        );

        // avaliação
        Assertions.assertEquals(HttpStatus.OK, cliente.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("requestValidandoCampos")
    public void cadastra_camposInvalidos_naoBuscaNaBaseDeDados(String login,
                                                               String senha) {
        // preparação
        var serviceAuth = Mockito.mock(AuthUseCaseImpl.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);
        Mockito.doThrow(
                        new IllegalArgumentException("Campos inválidos!")
                )
                .when(serviceAuth)
                .pegaUsuarioAutenticado(
                        any(LoginDTO.class)
                );

        Mockito.when(serviceToken.geraToken(
                                any(UsuarioEntity.class)
                        )
                )
                .thenReturn(
                        any(String.class)
                );

        var controller = new TokenController(serviceAuth, serviceToken);

        // execução e avaliação
        var excecao = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            controller.geraToken(
                    new LoginDTO(
                            login,
                            senha
                    )
            );
        });
    }

    private static Stream<Arguments> requestValidandoCampos() {
        return Stream.of(
                Arguments.of(null, "testeLogin"),
                Arguments.of("", "testeLogin"),
                Arguments.of(" ", "testeLogin"),
                Arguments.of("ab", "testeLogin"),
                Arguments.of("aaaaaaaaaabbbbbbbbbbaaaaaaaaaabbbbbbbbbbaaaaaaaaaad", "testeLogin"),
                Arguments.of("testeLogin", null),
                Arguments.of("testeLogin", ""),
                Arguments.of("testeLogin", " "),
                Arguments.of("testeLogin", "ab"),
                Arguments.of("testeLogin", "aaaaaaaaaabbbbbbbbbbaaaaaaaaaabbbbbbbbbbaaaaaaaaaad")
        );
    }

}

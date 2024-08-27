package com.fiap.techchallenge5.unitario;

import com.fiap.techchallenge5.domain.token.UsuarioRoleEnum;
import com.fiap.techchallenge5.infrastructure.usuario.controller.UsuarioController;
import com.fiap.techchallenge5.infrastructure.usuario.controller.dto.AtualizaUsuarioDTO;
import com.fiap.techchallenge5.infrastructure.usuario.controller.dto.CriaUsuarioDTO;
import com.fiap.techchallenge5.useCase.usuario.impl.UsuarioUseCaseImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import java.util.Objects;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;

public class UsuarioControllerTest {

    @Test
    public void cadastra_deveRetornar201_salvaNaBaseDeDados() {
        // preparação
        var service = Mockito.mock(UsuarioUseCaseImpl.class);
        Mockito.when(service.cadastra(
                                any(CriaUsuarioDTO.class)
                        )
                )
                .thenReturn(
                        true
                );

        var controller = new UsuarioController(service);

        // execução
        var cliente = controller.cadastra(
                new CriaUsuarioDTO(
                        "testeLogin",
                        "testeSenha"
                )
        );

        // avaliação
        Assertions.assertEquals(HttpStatus.CREATED, cliente.getStatusCode());
    }

    @Test
    public void cadastra_deveRetornar409_naoSalvaNaBaseDeDados() {
        // preparação
        var service = Mockito.mock(UsuarioUseCaseImpl.class);
        Mockito.when(service.cadastra(
                        any(CriaUsuarioDTO.class)
                        )
                )
                .thenReturn(
                        false
                );

        var controller = new UsuarioController(service);

        // execução
        var cliente = controller.cadastra(
                new CriaUsuarioDTO(
                        "testeLogin",
                        "testeSenha"
                )
        );

        // avaliação
        Assertions.assertEquals(HttpStatus.CONFLICT, cliente.getStatusCode());
    }

    @Test
    public void atualiza_deveRetornar200_salvaNaBaseDeDados() {
        // preparação
        var service = Mockito.mock(UsuarioUseCaseImpl.class);
        Mockito.when(service.atualiza(
                                any(String.class),
                                any(AtualizaUsuarioDTO.class)
                        )
                )
                .thenReturn(
                        true
                );

        var controller = new UsuarioController(service);

        // execução
        var cliente = controller.atualiza(
                "testeLogin",
                new AtualizaUsuarioDTO(
                        "testeSenha",
                        UsuarioRoleEnum.ADMIN
                )
        );

        // avaliação
        Assertions.assertEquals(HttpStatus.OK, cliente.getStatusCode());
    }

    @Test
    public void atualiza_deveRetornar204_naoSalvaNaBaseDeDados() {
        // preparação
        var service = Mockito.mock(UsuarioUseCaseImpl.class);
        Mockito.when(service.atualiza(
                                any(String.class),
                                any(AtualizaUsuarioDTO.class)
                        )
                )
                .thenReturn(
                        false
                );

        var controller = new UsuarioController(service);

        // execução
        var cliente = controller.atualiza(
                "testeLogin",
                new AtualizaUsuarioDTO(
                        "testeSenha",
                        UsuarioRoleEnum.ADMIN
                )
        );

        // avaliação
        Assertions.assertEquals(HttpStatus.NO_CONTENT, cliente.getStatusCode());
    }

    @Test
    public void deleta_deveRetornar200_deletaNaBaseDeDados() {
        // preparação
        var service = Mockito.mock(UsuarioUseCaseImpl.class);
        Mockito.when(service.deleta(
                                any(String.class)
                        )
                )
                .thenReturn(
                        true
                );

        var controller = new UsuarioController(service);

        // execução
        var cliente = controller.deleta(
                "testeLogin"
        );

        // avaliação
        Assertions.assertEquals(HttpStatus.OK, cliente.getStatusCode());
    }

    @Test
    public void deleta_deveRetornar204_naoDeletaNaBaseDeDados() {
        // preparação
        var service = Mockito.mock(UsuarioUseCaseImpl.class);
        Mockito.when(service.deleta(
                                any(String.class)
                        )
                )
                .thenReturn(
                        false
                );

        var controller = new UsuarioController(service);

        // execução
        var cliente = controller.deleta(
                "testeLogin"
        );

        // avaliação
        Assertions.assertEquals(HttpStatus.NO_CONTENT, cliente.getStatusCode());
    }

    @Test
    public void existe_deveRetornar200_buscaNaBaseDeDados() {
        // preparação
        var service = Mockito.mock(UsuarioUseCaseImpl.class);
        Mockito.when(service.existe(
                        "testeLogin"
                        )
                )
                .thenReturn(
                        true
                );

        var controller = new UsuarioController(service);

        // execução
        var cliente = controller.busca(
                "testeLogin"
        );

        // avaliação
        Assertions.assertEquals(HttpStatus.OK, cliente.getStatusCode());
    }

    @Test
    public void existe_deveRetornar204_naoEncontraNaBaseDeDados() {
        // preparação
        var service = Mockito.mock(UsuarioUseCaseImpl.class);
        Mockito.when(service.existe(
                        "testeLogin"
                        )
                )
                .thenReturn(
                        false
                );

        var controller = new UsuarioController(service);

        // execução
        var cliente = controller.busca(
                "testeLogin"
        );

        // avaliação
        Assertions.assertEquals(HttpStatus.NO_CONTENT, cliente.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("requestValidandoCampos")
    public void cadastra_camposInvalidos_naoBuscaNaBaseDeDados(String login,
                                                               String senha) {
        // preparação
        var service = Mockito.mock(UsuarioUseCaseImpl.class);
        Mockito.doThrow(
                        new IllegalArgumentException("Campos inválidos!")
                )
                .when(service)
                .cadastra(
                        any(CriaUsuarioDTO.class)
                );

        var controller = new UsuarioController(service);

        // execução e avaliação
        var excecao = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            controller.cadastra(
                    new CriaUsuarioDTO(
                            login,
                            senha
                    )
            );
        });
    }

    @ParameterizedTest
    @MethodSource("requestValidandoCamposAtualiza")
    public void atualiza_camposInvalidos_naoBuscaNaBaseDeDados(String senha,
                                                               String role) {
        // preparação
        var service = Mockito.mock(UsuarioUseCaseImpl.class);
        Mockito.doThrow(
                        new IllegalArgumentException("Campos inválidos!")
                )
                .when(service)
                .atualiza(
                        any(String.class),
                        any(AtualizaUsuarioDTO.class)
                );

        var controller = new UsuarioController(service);

        // execução e avaliação
        var excecao = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            controller.atualiza(
                    "testeLogin",
                    new AtualizaUsuarioDTO(
                            senha,
                            Objects.isNull(role) ? null : UsuarioRoleEnum.valueOf(role)
                    )
            );
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "-1000",
            "",
            " ",
            "ab",
            "aaaaaaaaaabbbbbbbbbbaaaaaaaaaabbbbbbbbbbaaaaaaaaaad"
    })
    public void deleta_camposInvalidos_naoDeletaNaBaseDeDados(String login) {
        // preparação
        var service = Mockito.mock(UsuarioUseCaseImpl.class);
        Mockito.doThrow(
                        new IllegalArgumentException("Campos inválidos!")
                )
                .when(service)
                .deleta(
                        any(String.class)
                );

        var controller = new UsuarioController(service);

        // execução e avaliação
        var excecao = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            controller.deleta(
                    login
            );
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "-1000",
            "",
            " ",
            "ab",
            "aaaaaaaaaabbbbbbbbbbaaaaaaaaaabbbbbbbbbbaaaaaaaaaad"
    })
    public void existe_camposInvalidos_naoBuscaNaBaseDeDados(String login) {
        // preparação
        var service = Mockito.mock(UsuarioUseCaseImpl.class);
        Mockito.doThrow(
                        new IllegalArgumentException("Campos inválidos!")
                )
                .when(service)
                .existe(
                        any(String.class)
                );

        var controller = new UsuarioController(service);

        // execução e avaliação
        var excecao = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            controller.busca(
                    login
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

    private static Stream<Arguments> requestValidandoCamposAtualiza() {
        return Stream.of(
                Arguments.of(null, UsuarioRoleEnum.ADMIN.name()),
                Arguments.of("", UsuarioRoleEnum.ADMIN.name()),
                Arguments.of(" ", UsuarioRoleEnum.ADMIN.name()),
                Arguments.of("ab", UsuarioRoleEnum.ADMIN.name()),
                Arguments.of("aaaaaaaaaabbbbbbbbbbaaaaaaaaaabbbbbbbbbbaaaaaaaaaad", UsuarioRoleEnum.ADMIN.name()),
                Arguments.of("testeLogin", null),
                Arguments.of("testeLogin", "")
        );
    }

}

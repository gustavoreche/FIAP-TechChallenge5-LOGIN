package com.fiap.techchallenge5.unitario;

import com.fiap.techchallenge5.domain.token.UsuarioRoleEnum;
import com.fiap.techchallenge5.infrastructure.usuario.controller.dto.AtualizaUsuarioDTO;
import com.fiap.techchallenge5.infrastructure.usuario.controller.dto.CriaUsuarioDTO;
import com.fiap.techchallenge5.infrastructure.usuario.model.UsuarioEntity;
import com.fiap.techchallenge5.infrastructure.usuario.repository.UsuarioRepository;
import com.fiap.techchallenge5.useCase.usuario.impl.UsuarioUseCaseImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.util.stream.Stream;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class UsuarioUseCaseTest {

    @Test
    public void cadastra_salvaNaBaseDeDados() {
        // preparação
        var repository = Mockito.mock(UsuarioRepository.class);

        Mockito.when(repository.save(Mockito.any()))
                .thenReturn(
                        new UsuarioEntity(
                                "testeLogin",
                                "testeSenha",
                                UsuarioRoleEnum.USER
                        )
                );

        var service = new UsuarioUseCaseImpl(repository);

        // execução
        service.cadastra(
                new CriaUsuarioDTO(
                        "testeLogin",
                        "testeSenha"
                )
        );

        // avaliação
        verify(repository, times(1)).findByLogin(Mockito.any());
        verify(repository, times(1)).save(Mockito.any());
    }

    @Test
    public void cadastra_clienteJaCadastrado_naoSalvaNaBaseDeDados() {
        // preparação
        var repository = Mockito.mock(UsuarioRepository.class);

        Mockito.when(repository.findByLogin(Mockito.any()))
                .thenReturn(
                        new UsuarioEntity(
                                "testeLogin",
                                "testeSenha",
                                UsuarioRoleEnum.USER
                        )
                );

        var service = new UsuarioUseCaseImpl(repository);

        // execução
        service.cadastra(
                new CriaUsuarioDTO(
                        "testeLogin",
                        "testeSenha"
                )
        );

        // avaliação
        verify(repository, times(1)).findByLogin(Mockito.any());
        verify(repository, times(0)).save(Mockito.any());
    }

    @Test
    public void atualiza_salvaNaBaseDeDados() {
        // preparação
        var repository = Mockito.mock(UsuarioRepository.class);

        Mockito.when(repository.save(Mockito.any()))
                .thenReturn(
                        new UsuarioEntity(
                                "testeLogin",
                                "testeSenha",
                                UsuarioRoleEnum.ADMIN
                        )
                );
        Mockito.when(repository.findByLogin(Mockito.any()))
                .thenReturn(
                        new UsuarioEntity(
                                "testeLogin",
                                "testeSenha",
                                UsuarioRoleEnum.USER
                        )
                );

        var service = new UsuarioUseCaseImpl(repository);

        // execução
        service.atualiza(
                "testeLogin",
                new AtualizaUsuarioDTO(
                        "testeSenha",
                        UsuarioRoleEnum.ADMIN
                )
        );

        // avaliação
        verify(repository, times(1)).findByLogin(Mockito.any());
        verify(repository, times(1)).save(Mockito.any());
    }

    @Test
    public void atualiza_clienteNaoEstaCadastrado_naoSalvaNaBaseDeDados() {
        // preparação
        var repository = Mockito.mock(UsuarioRepository.class);

        Mockito.when(repository.findByLogin(Mockito.any()))
                .thenReturn(
                        null
                );

        var service = new UsuarioUseCaseImpl(repository);

        // execução
        service.atualiza(
                "testeLogin",
                new AtualizaUsuarioDTO(
                        "testeSenha",
                        UsuarioRoleEnum.ADMIN
                )
        );

        // avaliação
        verify(repository, times(1)).findByLogin(Mockito.any());
        verify(repository, times(0)).save(Mockito.any());
    }

    @Test
    public void deleta_deletaNaBaseDeDados() {
        // preparação
        var repository = Mockito.mock(UsuarioRepository.class);

        Mockito.doNothing().when(repository).deleteById(Mockito.any());
        Mockito.when(repository.findByLogin(Mockito.any()))
                .thenReturn(
                        new UsuarioEntity(
                                "testeLogin",
                                "testeSenha",
                                UsuarioRoleEnum.USER
                        )
                );

        var service = new UsuarioUseCaseImpl(repository);

        // execução
        service.deleta(
                "testeLogin"
        );

        // avaliação
        verify(repository, times(1)).findByLogin(Mockito.any());
        verify(repository, times(1)).deleteById(Mockito.any());
    }

    @Test
    public void deleta_clienteNaoEstaCadastrado_naoDeletaNaBaseDeDados() {
        // preparação
        var repository = Mockito.mock(UsuarioRepository.class);

        Mockito.when(repository.findByLogin(Mockito.any()))
                .thenReturn(
                        null
                );

        var service = new UsuarioUseCaseImpl(repository);

        // execução
        service.deleta(
                "testeLogin"
        );

        // avaliação
        verify(repository, times(1)).findByLogin(Mockito.any());
        verify(repository, times(0)).deleteById(Mockito.any());
    }

    @Test
    public void existe_buscaNaBaseDeDados() {
        // preparação
        var repository = Mockito.mock(UsuarioRepository.class);

        Mockito.when(repository.findByLogin(Mockito.any()))
                .thenReturn(
                        new UsuarioEntity(
                                "testeLogin",
                                "testeSenha",
                                UsuarioRoleEnum.USER
                        )
                );

        var service = new UsuarioUseCaseImpl(repository);

        // execução
        service.existe(
                "testeLogin"
        );

        // avaliação
        verify(repository, times(1)).findByLogin(Mockito.any());
    }

    @Test
    public void existe_clienteNaoEstaCadastrado_naoEncontraNaBaseDeDados() {
        // preparação
        var repository = Mockito.mock(UsuarioRepository.class);

        Mockito.when(repository.findByLogin(Mockito.any()))
                .thenReturn(
                        null
                );

        var service = new UsuarioUseCaseImpl(repository);

        // execução
        service.existe(
                "testeLogin"
        );

        // avaliação
        verify(repository, times(1)).findByLogin(Mockito.any());
    }

    @ParameterizedTest
    @MethodSource("requestValidandoCampos")
    public void cadastra_camposInvalidos_naoSalvaNaBaseDeDados(String login,
                                                               String senha) {
        // preparação
        var repository = Mockito.mock(UsuarioRepository.class);

        Mockito.when(repository.save(Mockito.any()))
                .thenReturn(
                        new UsuarioEntity(
                                login,
                                senha,
                                UsuarioRoleEnum.USER
                        )
                );

        var service = new UsuarioUseCaseImpl(repository);

        // execução e avaliação
        var excecao = Assertions.assertThrows(RuntimeException.class, () -> {
            service.cadastra(
                    new CriaUsuarioDTO(
                            login,
                            senha
                    )
            );
        });
        verify(repository, times(0)).save(Mockito.any());
        verify(repository, times(0)).findByLogin(Mockito.any());
    }

    @ParameterizedTest
    @MethodSource("requestValidandoCamposAtualiza")
    public void atualiza_camposInvalidos_naoSalvaNaBaseDeDados(String senha,
                                                               String role) {
        // preparação
        var repository = Mockito.mock(UsuarioRepository.class);

        Mockito.when(repository.save(Mockito.any()))
                .thenReturn(
                        new UsuarioEntity(
                                "testeLogin",
                                senha,
                                UsuarioRoleEnum.USER
                        )
                );

        Mockito.when(repository.findByLogin(Mockito.any()))
                .thenReturn(
                        new UsuarioEntity(
                                "testeLogin",
                                "testeSenha",
                                UsuarioRoleEnum.USER
                        )
                );

        var service = new UsuarioUseCaseImpl(repository);

        // execução e avaliação
        var excecao = Assertions.assertThrows(RuntimeException.class, () -> {
            service.atualiza(
                    "testeLogin",
                    new AtualizaUsuarioDTO(
                            senha,
                            UsuarioRoleEnum.valueOf(role)
                    )
                );
        });
        verify(repository, times(0)).findByLogin(Mockito.any());
        verify(repository, times(0)).save(Mockito.any());
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
        var repository = Mockito.mock(UsuarioRepository.class);

        var service = new UsuarioUseCaseImpl(repository);

        // execução e avaliação
        var excecao = Assertions.assertThrows(RuntimeException.class, () -> {
            service.deleta(
                    login.equals("-1000") ? null : login
            );
        });
        verify(repository, times(0)).findByLogin(Mockito.any());
        verify(repository, times(0)).deleteById(Mockito.any());
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
        var repository = Mockito.mock(UsuarioRepository.class);

        var service = new UsuarioUseCaseImpl(repository);

        // execução e avaliação
        var excecao = Assertions.assertThrows(RuntimeException.class, () -> {
            service.existe(
                    login.equals("-1000") ? null : login
            );
        });
        verify(repository, times(0)).findByLogin(Mockito.any());
        verify(repository, times(0)).deleteById(Mockito.any());
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

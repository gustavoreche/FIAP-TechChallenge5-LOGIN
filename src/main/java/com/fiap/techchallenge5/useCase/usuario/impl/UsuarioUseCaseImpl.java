package com.fiap.techchallenge5.useCase.usuario.impl;

import com.fiap.techchallenge5.domain.token.UsuarioRoleEnum;
import com.fiap.techchallenge5.domain.usuario.Login;
import com.fiap.techchallenge5.domain.usuario.Usuario;
import com.fiap.techchallenge5.infrastructure.usuario.controller.dto.AtualizaUsuarioDTO;
import com.fiap.techchallenge5.infrastructure.usuario.controller.dto.CriaUsuarioDTO;
import com.fiap.techchallenge5.infrastructure.usuario.model.UsuarioEntity;
import com.fiap.techchallenge5.infrastructure.usuario.repository.UsuarioRepository;
import com.fiap.techchallenge5.useCase.usuario.UsuarioUseCase;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UsuarioUseCaseImpl implements UsuarioUseCase, UserDetailsService {

    private final UsuarioRepository repository;

    public UsuarioUseCaseImpl(final UsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.repository.findByLogin(username);
    }

    @Override
    public boolean cadastra(final CriaUsuarioDTO dadosUsuario) {
        final var usuario = new Usuario(
                dadosUsuario.login(),
                dadosUsuario.senha(),
                UsuarioRoleEnum.USER
        );

        final var usuarioNaBase = this.repository.findByLogin(usuario.login());
        if(Objects.isNull(usuarioNaBase)) {
            final var senhaEncriptada = new BCryptPasswordEncoder()
                    .encode(usuario.senha());

            final var usuarioEntity = UsuarioEntity.builder()
                    .login(usuario.login())
                    .senha(senhaEncriptada)
                    .role(usuario.role())
                    .build();

            this.repository.save(usuarioEntity);
            return true;
        }
        System.out.println("Usuário já cadastrado");
        return false;

    }

    @Override
    public boolean atualiza(final String login,
                            final AtualizaUsuarioDTO dadosUsuario) {
        final var loginObjeto = new Login(login);
        final var usuario = new Usuario(
                loginObjeto.id(),
                dadosUsuario.senha(),
                dadosUsuario.role()
        );

        final var usuarioNaBase = this.repository.findByLogin(login);
        if(Objects.isNull(usuarioNaBase)) {
            System.out.println("Usuario não está cadastrado");
            return false;
        }

        final var senhaEncriptada = new BCryptPasswordEncoder()
                .encode(usuario.senha());

        final var usuarioEntity = UsuarioEntity.builder()
                .login(usuario.login())
                .senha(senhaEncriptada)
                .role(usuario.role())
                .build();

        this.repository.save(usuarioEntity);
        return true;

    }

    @Override
    public boolean deleta(final String login) {
        final var loginObjeto = new Login(login);

        final var usuarioNaBase = this.repository.findByLogin(loginObjeto.id());
        if(Objects.isNull(usuarioNaBase)) {
            System.out.println("Usuário não está cadastrado");
            return false;
        }
        this.repository.deleteById(loginObjeto.id());
        return true;

    }

    @Override
    public boolean existe(final String login) {
        final var loginObjeto = new Login(login);

        final var usuarioNaBase = this.repository.findByLogin(loginObjeto.id());
        if(Objects.isNull(usuarioNaBase)) {
            System.out.println("Usuário não está cadastrado");
            return false;
        }

        return true;

    }

}

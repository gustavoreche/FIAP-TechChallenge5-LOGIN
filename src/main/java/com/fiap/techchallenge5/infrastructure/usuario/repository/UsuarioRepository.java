package com.fiap.techchallenge5.infrastructure.usuario.repository;

import com.fiap.techchallenge5.infrastructure.usuario.model.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, String> {

    UserDetails findByLogin(String login);

}

package com.fiap.techchallenge5.infrastructure.usuario.controller;

import com.fiap.techchallenge5.infrastructure.usuario.controller.dto.AtualizaUsuarioDTO;
import com.fiap.techchallenge5.infrastructure.usuario.controller.dto.CriaUsuarioDTO;
import com.fiap.techchallenge5.useCase.usuario.UsuarioUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.fiap.techchallenge5.infrastructure.usuario.controller.UsuarioController.URL_USUARIO;

@Tag(
		name = "Usuários",
		description = "Serviço para realizar o gerenciamento de usuários no sistema"
)
@RestController
@RequestMapping(URL_USUARIO)
public class UsuarioController {

	public static final String URL_USUARIO = "/usuario";
	public static final String URL_USUARIO_COM_LOGIN = URL_USUARIO + "/{login}";

	private final UsuarioUseCase service;

	public UsuarioController(final UsuarioUseCase service) {
		this.service = service;
	}

	@Operation(
			summary = "Serviço para cadastrar um usuário"
	)
	@PostMapping
	public ResponseEntity<Void> cadastra(@RequestBody @Valid final CriaUsuarioDTO dadosUsuario) {
		final var cadastrou = this.service.cadastra(dadosUsuario);
		if(cadastrou) {
			return ResponseEntity
					.status(HttpStatus.CREATED)
					.build();
		}
		return ResponseEntity
				.status(HttpStatus.CONFLICT)
				.build();
	}

	@Operation(
			summary = "Serviço para atualizar um usuário"
	)
	@PutMapping("/{login}")
	public ResponseEntity<Void> atualiza(@PathVariable("login") final String login,
										 @RequestBody @Valid final AtualizaUsuarioDTO dadosUsuario) {
		final var atualizou = this.service.atualiza(login, dadosUsuario);
		if(atualizou) {
			return ResponseEntity
					.status(HttpStatus.OK)
					.build();
		}
		return ResponseEntity
				.status(HttpStatus.NO_CONTENT)
				.build();
	}

	@Operation(
			summary = "Serviço para deletar um usuário"
	)
	@DeleteMapping("/{login}")
	public ResponseEntity<Void> deleta(@PathVariable("login") final String login) {
		final var deletou = this.service.deleta(login);
		if(deletou) {
			return ResponseEntity
					.status(HttpStatus.OK)
					.build();
		}
		return ResponseEntity
				.status(HttpStatus.NO_CONTENT)
				.build();
	}

	@Operation(
			summary = "Serviço para buscar um usuário"
	)
	@GetMapping("/{login}")
	public ResponseEntity<Boolean> busca(@PathVariable("login") final String login) {
		final var cliente = this.service.existe(login);
		if(cliente) {
			return ResponseEntity
					.status(HttpStatus.OK)
					.body(Boolean.TRUE);
		}
		return ResponseEntity
				.status(HttpStatus.NO_CONTENT)
				.build();
	}

}

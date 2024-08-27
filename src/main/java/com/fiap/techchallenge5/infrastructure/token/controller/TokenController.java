package com.fiap.techchallenge5.infrastructure.token.controller;

import com.fiap.techchallenge5.infrastructure.token.controller.dto.LoginDTO;
import com.fiap.techchallenge5.infrastructure.token.controller.dto.TokenDTO;
import com.fiap.techchallenge5.useCase.token.AuthUseCase;
import com.fiap.techchallenge5.useCase.token.TokenUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.fiap.techchallenge5.infrastructure.token.controller.TokenController.URL_AUTH;

@Tag(
		name = "Token",
		description = "Serviço para realizar o o gerenciamento de TOKENS dos usuários"
)
@RestController
@RequestMapping(URL_AUTH)
public class TokenController {

	public static final String URL_AUTH = "/auth";
	public static final String URL_AUTH_COM_LOGIN = URL_AUTH.concat("/login");

	private final AuthUseCase serviceAuth;
	private final TokenUseCase serviceToken;

	public TokenController(final AuthUseCase serviceAuth,
						   final TokenUseCase serviceToken) {
		this.serviceAuth = serviceAuth;
		this.serviceToken = serviceToken;
	}

	@Operation(
			summary = "Serviço para realizar o LOGIN do usuário, ou seja, " +
					"informando o LOGIN e SENHA, será retornando um TOKEN"
	)
	@PostMapping("/login")
	public ResponseEntity<TokenDTO> geraToken(@RequestBody @Valid final LoginDTO login) {
		final var usuarioAutenticado = this.serviceAuth.pegaUsuarioAutenticado(login);
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(new TokenDTO(this.serviceToken.geraToken(usuarioAutenticado)));
	}

}

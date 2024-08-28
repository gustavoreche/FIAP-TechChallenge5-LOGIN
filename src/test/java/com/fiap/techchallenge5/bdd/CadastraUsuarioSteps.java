package com.fiap.techchallenge5.bdd;

import com.fiap.techchallenge5.infrastructure.usuario.controller.dto.CriaUsuarioDTO;
import com.fiap.techchallenge5.integrados.JwtUtil;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.fiap.techchallenge5.infrastructure.usuario.controller.UsuarioController.URL_USUARIO;
import static io.restassured.RestAssured.given;


public class CadastraUsuarioSteps {

    private Response response;
    private CriaUsuarioDTO request;
    private final String login = pegaLogin();
    private final String token = JwtUtil.geraJwt();

    private String pegaLogin() {
        final var currentTime = System.currentTimeMillis();
        final var timeString = Long.toString(currentTime);
        return timeString.substring(1, 12);
    }

    @Dado("que tenho dados validos de um usuario")
    public void tenhoDadosValidosDeUmUsuario() {
        this.request = new CriaUsuarioDTO(
                this.login,
                "testeSenha"
        );
    }

    @Dado("que tenho dados validos de um usuario que ja esta cadastrado")
    public void tenhoDadosValidosDeUmUsuarioQueJaEstaCadastrado() {
        this.request = new CriaUsuarioDTO(
                this.login,
                "testeSenha"
        );

        this.cadastroEsseUsuario();
    }

    @Quando("cadastro esse usuario")
    public void cadastroEsseUsuario() {
        RestAssured.baseURI = "http://localhost:8080";
        this.response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + token)
                .body(this.request)
                .when()
                .post(URL_USUARIO);
    }

    @Entao("recebo uma resposta que o usuario foi cadastrado com sucesso")
    public void receboUmaRespostaQueOUsuarioFoiCadastradoComSucesso() {
        this.response
                .prettyPeek()
                .then()
                .statusCode(HttpStatus.CREATED.value())
        ;
    }

    @Entao("recebo uma resposta que o usuario ja esta cadastrado")
    public void receboUmaRespostaQueOUsuarioJaEstaCadastrado() {
        this.response
                .prettyPeek()
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
        ;
    }

}

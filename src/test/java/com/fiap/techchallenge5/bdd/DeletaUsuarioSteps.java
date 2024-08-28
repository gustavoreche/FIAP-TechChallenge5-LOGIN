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
import static com.fiap.techchallenge5.infrastructure.usuario.controller.UsuarioController.URL_USUARIO_COM_LOGIN;
import static io.restassured.RestAssured.given;


public class DeletaUsuarioSteps {

    private Response response;
    private String login;
    private final String token = JwtUtil.geraJwt();

    private String pegaLogin() {
        final var currentTime = System.currentTimeMillis();
        final var timeString = Long.toString(currentTime);
        return timeString.substring(1, 12);
    }

    @Dado("que informo um usuario que ja esta cadastrado")
    public void queInformoUmUsuarioQueJaEstaCadastrado() {
        this.login = this.pegaLogin();
        final var request = new CriaUsuarioDTO(
                this.login,
                "testeSenha"
        );

        RestAssured.baseURI = "http://localhost:8080";
        this.response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post(URL_USUARIO);
    }

    @Dado("que informo um usuario nao cadastrado")
    public void queInformoUmUsuarioNaoCadastrado() {
        this.login = this.pegaLogin();
    }


    @Quando("deleto esse usuario")
    public void deletoEsseUsuario() {
        RestAssured.baseURI = "http://localhost:8080";
        this.response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete(URL_USUARIO_COM_LOGIN.replace("{login}", this.login));
    }

    @Entao("recebo uma resposta que o usuario foi deletado com sucesso")
    public void receboUmaRespostaQueOUsuarioFoiDeletadoComSucesso() {
        this.response
                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
        ;
    }

    @Entao("recebo uma resposta que o usuario nao foi cadastrado")
    public void receboUmaRespostaQueOUsuarioNaoFoiCadastrado() {
        this.response
                .prettyPeek()
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
        ;
    }

}

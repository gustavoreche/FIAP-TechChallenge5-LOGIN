package com.fiap.techchallenge5.bdd;

import com.fiap.techchallenge5.infrastructure.token.controller.dto.LoginDTO;
import com.fiap.techchallenge5.infrastructure.usuario.controller.dto.CriaUsuarioDTO;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.fiap.techchallenge5.infrastructure.token.controller.TokenController.URL_AUTH_COM_LOGIN;
import static com.fiap.techchallenge5.infrastructure.usuario.controller.UsuarioController.URL_USUARIO;
import static io.restassured.RestAssured.given;


public class GeraTokenSteps {

    private Response response;
    private LoginDTO request;
    private String login;

    private String pegaLogin() {
        final var currentTime = System.currentTimeMillis();
        final var timeString = Long.toString(currentTime);
        return timeString.substring(1, 12);
    }

    @Dado("que informo dados validos de um usuario")
    public void queInformoDadosValidosDeUmUsuario() {
        this.login = this.pegaLogin();
        final var request = new CriaUsuarioDTO(
                this.login,
                "testeSenha"
        );

        RestAssured.baseURI = "http://localhost:8080";
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post(URL_USUARIO);

        this.request = new LoginDTO(
                this.login,
                "testeSenha"
        );
    }

    @Dado("que informo dados invalidos de um usuario")
    public void queInformoDadosInvalidosDeUmUsuario() {
        this.login = this.pegaLogin();
        this.request = new LoginDTO(
                "aaaaaa",
                "aaaaaa"
        );
    }


    @Quando("gero o token desse usuario")
    public void geroOTokenDesseUsuario() {
        RestAssured.baseURI = "http://localhost:8080";
        this.response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(this.request)
                .when()
                .post(URL_AUTH_COM_LOGIN);
    }

    @Entao("recebo uma resposta que o token foi gerado com sucesso")
    public void receboUmaRespostaQueOTokenFoiGeradoComSucesso() {
        this.response
                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
        ;
    }

    @Entao("recebo uma resposta que o token não foi gerado")
    public void receboUmaRespostaQueOTokenNaoFoiGerado() {
        this.response
                .prettyPeek()
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value())
        ;
    }

}

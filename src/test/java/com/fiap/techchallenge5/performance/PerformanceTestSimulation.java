package com.fiap.techchallenge5.performance;

import com.fiap.techchallenge5.integrados.JwtUtil;
import io.gatling.javaapi.core.ActionBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class PerformanceTestSimulation extends Simulation {

    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080");

    private final String token = JwtUtil.geraJwt();

    ActionBuilder cadastraUsuarioRequest = http("cadastra usuário")
            .post("/usuario")
            .header("Content-Type", "application/json")
            .body(StringBody("""
                              {
                                "login": "${login}",
                                "senha": "senha de teste"
                              }
                    """))
            .check(status().is(201));

    ActionBuilder atualizaUsuarioRequest = http("atualiza usuário")
            .put("/usuario/${login}")
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + token)
            .body(StringBody("""
                              {
                                "senha": "senha de teste",
                                "role": "ADMIN"
                              }
                    """))
            .check(status().is(200));

    ActionBuilder deletaUsuarioRequest = http("deleta usuário")
            .delete("/usuario/${login}")
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + token)
            .check(status().is(200));

    ActionBuilder buscaUsuarioRequest = http("busca usuário")
            .get("/usuario/${login}")
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + token)
            .check(status().is(200));

    ActionBuilder geraTokenRequest = http("gera token")
            .post("/auth/login")
            .header("Content-Type", "application/json")
            .body(StringBody("""
                              {
                                "login": "gustavo",
                                "senha": "senhaForte"
                              }
                    """))
            .check(status().is(200));

    ScenarioBuilder cenarioCadastraUsuario = scenario("Cadastra usuário")
            .exec(session -> {
                long login = System.currentTimeMillis();
                return session.set("login", String.valueOf(login));
            })
            .exec(cadastraUsuarioRequest);

    ScenarioBuilder cenarioAtualizaUsuario = scenario("Atualiza usuario")
            .exec(session -> {
                long login = System.currentTimeMillis() + 123456789L;
                return session.set("login", String.valueOf(login));
            })
            .exec(cadastraUsuarioRequest)
            .exec(atualizaUsuarioRequest);

    ScenarioBuilder cenarioDeletaUsuario = scenario("Deleta usuário")
            .exec(session -> {
                long login = System.currentTimeMillis() + 333333333L;
                return session.set("login", String.valueOf(login));
            })
            .exec(cadastraUsuarioRequest)
            .exec(deletaUsuarioRequest);

    ScenarioBuilder cenarioBuscaUsuario = scenario("Busca usuário")
            .exec(session -> {
                long login = System.currentTimeMillis() + 777777777L;
                return session.set("login", String.valueOf(login));
            })
            .exec(cadastraUsuarioRequest)
            .exec(buscaUsuarioRequest);

    ScenarioBuilder cenarioGeraToken = scenario("Gera token")
            .exec(geraTokenRequest);


    {
        setUp(
                cenarioCadastraUsuario.injectOpen(
                        rampUsersPerSec(1)
                                .to(10)
                                .during(Duration.ofSeconds(10)),
                        constantUsersPerSec(10)
                                .during(Duration.ofSeconds(20)),
                        rampUsersPerSec(10)
                                .to(1)
                                .during(Duration.ofSeconds(10))),
                cenarioAtualizaUsuario.injectOpen(
                        rampUsersPerSec(1)
                                .to(10)
                                .during(Duration.ofSeconds(10)),
                        constantUsersPerSec(10)
                                .during(Duration.ofSeconds(20)),
                        rampUsersPerSec(10)
                                .to(1)
                                .during(Duration.ofSeconds(10))),
                cenarioDeletaUsuario.injectOpen(
                        rampUsersPerSec(1)
                                .to(10)
                                .during(Duration.ofSeconds(10)),
                        constantUsersPerSec(10)
                                .during(Duration.ofSeconds(20)),
                        rampUsersPerSec(10)
                                .to(1)
                                .during(Duration.ofSeconds(10))),
                cenarioBuscaUsuario.injectOpen(
                        rampUsersPerSec(1)
                                .to(10)
                                .during(Duration.ofSeconds(10)),
                        constantUsersPerSec(10)
                                .during(Duration.ofSeconds(20)),
                        rampUsersPerSec(10)
                                .to(1)
                                .during(Duration.ofSeconds(10))),
                cenarioGeraToken.injectOpen(
                        rampUsersPerSec(1)
                                .to(10)
                                .during(Duration.ofSeconds(10)),
                        constantUsersPerSec(5)
                                .during(Duration.ofSeconds(20)),
                        rampUsersPerSec(10)
                                .to(1)
                                .during(Duration.ofSeconds(10)))
        )
                .protocols(httpProtocol)
                .assertions(
                        global().responseTime().max().lt(600),
                        global().failedRequests().count().is(0L));
    }
}
package com.fiap.techchallenge5.integrados;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.techchallenge5.domain.token.UsuarioRoleEnum;
import com.fiap.techchallenge5.infrastructure.token.controller.dto.LoginDTO;
import com.fiap.techchallenge5.infrastructure.token.controller.dto.TokenDTO;
import com.fiap.techchallenge5.infrastructure.usuario.controller.dto.AtualizaUsuarioDTO;
import com.fiap.techchallenge5.infrastructure.usuario.controller.dto.CriaUsuarioDTO;
import com.fiap.techchallenge5.infrastructure.usuario.model.UsuarioEntity;
import com.fiap.techchallenge5.infrastructure.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.stream.Stream;

import static com.fiap.techchallenge5.infrastructure.token.controller.TokenController.URL_AUTH_COM_LOGIN;
import static com.fiap.techchallenge5.infrastructure.usuario.controller.UsuarioController.URL_USUARIO;
import static com.fiap.techchallenge5.infrastructure.usuario.controller.UsuarioController.URL_USUARIO_COM_LOGIN;


@AutoConfigureMockMvc
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TokenControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    UsuarioRepository repository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void inicializaLimpezaDoDatabase() {
        this.repository.deleteAll();

        final var senhaEncriptada = new BCryptPasswordEncoder()
                .encode("testeSenha");

        this.repository.save(UsuarioEntity.builder()
                .login("gustavo")
                .senha(senhaEncriptada)
                .role(UsuarioRoleEnum.ADMIN)
                .build());
    }

    @AfterAll
    void finalizaLimpezaDoDatabase() {
        this.repository.deleteAll();
    }

    @Test
    public void geraToken_deveRetornar200() throws Exception {
        var mockData = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS);
        var dataMock = LocalDateTime.of(2023, 11, 19, 14, 00, 00);
        mockData.when(LocalDateTime::now)
                .thenReturn(dataMock);

        var request = new LoginDTO(
                "gustavo",
                "testeSenha"
        );
        var objectMapper = this.objectMapper
                .writer()
                .withDefaultPrettyPrinter();
        var jsonRequest = objectMapper.writeValueAsString(request);

        var response = this.mockMvc
                .perform(MockMvcRequestBuilders.post(URL_AUTH_COM_LOGIN)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isOk()
                )
                .andReturn();

        var responseAppString = response.getResponse().getContentAsString();
        var responseApp = this.objectMapper
                .readValue(responseAppString, new TypeReference<TokenDTO>() {});

        Assertions.assertEquals(JwtUtil.geraJwt(), responseApp.token());

        mockData.close();
    }

    @Test
    public void geraToken_deveRetornar200_semUsuarioIdentificado() throws Exception {
        var mockData = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS);
        var dataMock = LocalDateTime.of(2023, 11, 19, 14, 00, 00);
        mockData.when(LocalDateTime::now)
                .thenReturn(dataMock);

        var request = new LoginDTO(
                "teste",
                "testeSenha"
        );
        var objectMapper = this.objectMapper
                .writer()
                .withDefaultPrettyPrinter();
        var jsonRequest = objectMapper.writeValueAsString(request);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(URL_AUTH_COM_LOGIN)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isForbidden()
                );

        mockData.close();
    }

    @ParameterizedTest
    @MethodSource("requestValidandoCampos")
    public void geraToken_camposInvalidos_naoGeraToken(String login,
                                                       String senha) throws Exception {
        var request = new LoginDTO(
                login,
                senha
        );
        var objectMapper = this.objectMapper
                .writer()
                .withDefaultPrettyPrinter();
        var jsonRequest = objectMapper.writeValueAsString(request);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(URL_AUTH_COM_LOGIN)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isBadRequest()
                );
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

}

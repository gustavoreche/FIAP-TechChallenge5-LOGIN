package com.fiap.techchallenge5.integrados;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.techchallenge5.domain.token.UsuarioRoleEnum;
import com.fiap.techchallenge5.infrastructure.usuario.controller.dto.AtualizaUsuarioDTO;
import com.fiap.techchallenge5.infrastructure.usuario.controller.dto.CriaUsuarioDTO;
import com.fiap.techchallenge5.infrastructure.usuario.model.UsuarioEntity;
import com.fiap.techchallenge5.infrastructure.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.stream.Stream;

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
    private ObjectMapper objectMapper;

    private final String token = JwtUtil.geraJwt();

    @BeforeEach
    void inicializaLimpezaDoDatabase() {
        this.repository.deleteAll();

        this.repository.save(UsuarioEntity.builder()
                .login("gustavo")
                .senha("testeSenha")
                .role(UsuarioRoleEnum.ADMIN)
                .build());
    }

    @AfterAll
    void finalizaLimpezaDoDatabase() {
        this.repository.deleteAll();
    }

    @Test
    public void cadastra_deveRetornar201_salvaNaBaseDeDados() throws Exception {

        var request = new CriaUsuarioDTO(
                "testeLogin",
                "testeSenha"
        );
        var objectMapper = this.objectMapper
                .writer()
                .withDefaultPrettyPrinter();
        var jsonRequest = objectMapper.writeValueAsString(request);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(URL_USUARIO)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isCreated()
                );

        this.repository.deleteById("gustavo");
        var usuario = this.repository.findAll().get(0);

        boolean testeSenha = new BCryptPasswordEncoder().matches("testeSenha", usuario.getSenha());

        Assertions.assertEquals("testeLogin", usuario.getLogin());
        Assertions.assertTrue(testeSenha);
        Assertions.assertEquals(UsuarioRoleEnum.USER, usuario.getRole());
    }

    @Test
    public void cadastra_deveRetornar409_naoSalvaNaBaseDeDados() throws Exception {

        this.repository.save(UsuarioEntity.builder()
                .login("testeLogin")
                .senha("testeSenha")
                .role(UsuarioRoleEnum.USER)
                .build());

        var request = new CriaUsuarioDTO(
                "testeLogin",
                "testeSenha"
        );
        var objectMapper = this.objectMapper
                .writer()
                .withDefaultPrettyPrinter();
        var jsonRequest = objectMapper.writeValueAsString(request);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(URL_USUARIO)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isConflict()
                );

        this.repository.deleteById("gustavo");
        var usuario = this.repository.findAll().get(0);

        Assertions.assertEquals("testeLogin", usuario.getLogin());
        Assertions.assertEquals("testeSenha", usuario.getSenha());
        Assertions.assertEquals(UsuarioRoleEnum.USER, usuario.getRole());
    }

    @Test
    public void atualiza_deveRetornar200_salvaNaBaseDeDados() throws Exception {

        this.repository.save(UsuarioEntity.builder()
                .login("testeLogin")
                .senha("testeSenha")
                .role(UsuarioRoleEnum.USER)
                .build());

        var request = new AtualizaUsuarioDTO(
                "testeSenha",
                UsuarioRoleEnum.ADMIN
        );
        var objectMapper = this.objectMapper
                .writer()
                .withDefaultPrettyPrinter();
        var jsonRequest = objectMapper.writeValueAsString(request);

        this.mockMvc
                .perform(MockMvcRequestBuilders.put(URL_USUARIO_COM_LOGIN.replace("{login}", "testeLogin"))
                        .header("Authorization", "Bearer " + this.token)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isOk()
                );

        this.repository.deleteById("gustavo");
        var usuario = this.repository.findAll().get(0);

        boolean testeSenha = new BCryptPasswordEncoder().matches("testeSenha", usuario.getSenha());

        Assertions.assertEquals("testeLogin", usuario.getLogin());
        Assertions.assertTrue(testeSenha);
        Assertions.assertEquals(UsuarioRoleEnum.ADMIN, usuario.getRole());
    }

    @Test
    public void atualiza_deveRetornar204_naoSalvaNaBaseDeDados() throws Exception {

        var request = new AtualizaUsuarioDTO(
                "testeSenha",
                UsuarioRoleEnum.ADMIN
        );
        var objectMapper = this.objectMapper
                .writer()
                .withDefaultPrettyPrinter();
        var jsonRequest = objectMapper.writeValueAsString(request);

        this.mockMvc
                .perform(MockMvcRequestBuilders.put(URL_USUARIO_COM_LOGIN.replace("{login}", "testeLogin"))
                        .header("Authorization", "Bearer " + this.token)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isNoContent()
                );

        this.repository.deleteById("gustavo");
        Assertions.assertEquals(0, this.repository.findAll().size());
    }

    @Test
    public void atualiza_deveRetornar401_semToken() throws Exception {

        var request = new AtualizaUsuarioDTO(
                "testeSenha",
                UsuarioRoleEnum.ADMIN
        );
        var objectMapper = this.objectMapper
                .writer()
                .withDefaultPrettyPrinter();
        var jsonRequest = objectMapper.writeValueAsString(request);

        this.mockMvc
                .perform(MockMvcRequestBuilders.put(URL_USUARIO_COM_LOGIN.replace("{login}", "testeLogin"))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isUnauthorized()
                );

        this.repository.deleteById("gustavo");
        Assertions.assertEquals(0, this.repository.findAll().size());
    }

    @Test
    public void atualiza_deveRetornar401_tokenInvalido() throws Exception {

        var request = new AtualizaUsuarioDTO(
                "testeSenha",
                UsuarioRoleEnum.ADMIN
        );
        var objectMapper = this.objectMapper
                .writer()
                .withDefaultPrettyPrinter();
        var jsonRequest = objectMapper.writeValueAsString(request);

        this.mockMvc
                .perform(MockMvcRequestBuilders.put(URL_USUARIO_COM_LOGIN.replace("{login}", "testeLogin"))
                        .header("Authorization", "Bearer TESTE")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isUnauthorized()
                );

        this.repository.deleteById("gustavo");
        Assertions.assertEquals(0, this.repository.findAll().size());
    }

    @Test
    public void atualiza_deveRetornar401_tokenExpirado() throws Exception {

        var request = new AtualizaUsuarioDTO(
                "testeSenha",
                UsuarioRoleEnum.ADMIN
        );
        var objectMapper = this.objectMapper
                .writer()
                .withDefaultPrettyPrinter();
        var jsonRequest = objectMapper.writeValueAsString(request);

        this.mockMvc
                .perform(MockMvcRequestBuilders.put(URL_USUARIO_COM_LOGIN.replace("{login}", "testeLogin"))
                        .header("Authorization", "Bearer " + JwtUtil.geraJwt(LocalDateTime.now()
                                .minusHours(3)
                                .toInstant(ZoneOffset.of("-03:00"))))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isUnauthorized()
                );

        this.repository.deleteById("gustavo");
        Assertions.assertEquals(0, this.repository.findAll().size());
    }

    @Test
    public void atualiza_deveRetornar403_tokenSemRoleAdmin() throws Exception {

        this.repository.save(UsuarioEntity.builder()
                .login("usuarioUser")
                .senha("testeSenha")
                .role(UsuarioRoleEnum.USER)
                .build());

        var request = new AtualizaUsuarioDTO(
                "testeSenha",
                UsuarioRoleEnum.ADMIN
        );
        var objectMapper = this.objectMapper
                .writer()
                .withDefaultPrettyPrinter();
        var jsonRequest = objectMapper.writeValueAsString(request);

        this.mockMvc
                .perform(MockMvcRequestBuilders.put(URL_USUARIO_COM_LOGIN.replace("{login}", "testeLogin"))
                        .header("Authorization", "Bearer " + JwtUtil.geraJwt("usuarioUser"))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isForbidden()
                );

        this.repository.deleteById("gustavo");
        this.repository.deleteById("usuarioUser");
        Assertions.assertEquals(0, this.repository.findAll().size());
    }

    @Test
    public void deleta_deveRetornar200_deletaNaBaseDeDados() throws Exception {

        this.repository.save(UsuarioEntity.builder()
                .login("testeLogin")
                .senha("testeSenha")
                .role(UsuarioRoleEnum.USER)
                .build());

        this.mockMvc
                .perform(MockMvcRequestBuilders.delete(URL_USUARIO_COM_LOGIN.replace("{login}", "testeLogin"))
                        .header("Authorization", "Bearer " + this.token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isOk()
                );

        this.repository.deleteById("gustavo");
        Assertions.assertEquals(0, this.repository.findAll().size());
    }

    @Test
    public void deleta_deveRetornar204_naoDeletaNaBaseDeDados() throws Exception {

        this.mockMvc
                .perform(MockMvcRequestBuilders.delete(URL_USUARIO_COM_LOGIN.replace("{login}", "loginTeste"))
                        .header("Authorization", "Bearer " + this.token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isNoContent()
                );

        this.repository.deleteById("gustavo");
        Assertions.assertEquals(0, this.repository.findAll().size());
    }

    @Test
    public void deleta_deveRetornar201_semToken() throws Exception {

        this.mockMvc
                .perform(MockMvcRequestBuilders.delete(URL_USUARIO_COM_LOGIN.replace("{login}", "loginTeste"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isUnauthorized()
                );

        this.repository.deleteById("gustavo");
        Assertions.assertEquals(0, this.repository.findAll().size());
    }

    @Test
    public void deleta_deveRetornar201_tokenInvalido() throws Exception {

        this.mockMvc
                .perform(MockMvcRequestBuilders.delete(URL_USUARIO_COM_LOGIN.replace("{login}", "loginTeste"))
                        .header("Authorization", "Bearer TESTE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isUnauthorized()
                );

        this.repository.deleteById("gustavo");
        Assertions.assertEquals(0, this.repository.findAll().size());
    }

    @Test
    public void deleta_deveRetornar201_tokenExpirado() throws Exception {

        this.mockMvc
                .perform(MockMvcRequestBuilders.delete(URL_USUARIO_COM_LOGIN.replace("{login}", "loginTeste"))
                        .header("Authorization", "Bearer " + JwtUtil.geraJwt(LocalDateTime.now()
                                .minusHours(3)
                                .toInstant(ZoneOffset.of("-03:00"))))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isUnauthorized()
                );

        this.repository.deleteById("gustavo");
        Assertions.assertEquals(0, this.repository.findAll().size());
    }

    @Test
    public void deleta_deveRetornar403_tokenSemRoleAdmin() throws Exception {
        this.repository.save(UsuarioEntity.builder()
                .login("usuarioUser")
                .senha("testeSenha")
                .role(UsuarioRoleEnum.USER)
                .build());

        this.mockMvc
                .perform(MockMvcRequestBuilders.delete(URL_USUARIO_COM_LOGIN.replace("{login}", "loginTeste"))
                        .header("Authorization", "Bearer " + JwtUtil.geraJwt("usuarioUser"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isForbidden()
                );

        this.repository.deleteById("gustavo");
        this.repository.deleteById("usuarioUser");
        Assertions.assertEquals(0, this.repository.findAll().size());
    }

    @Test
    public void existe_deveRetornar200_buscaNaBaseDeDados() throws Exception {

        this.repository.save(UsuarioEntity.builder()
                .login("testeLogin")
                .senha("testeSenha")
                .role(UsuarioRoleEnum.USER)
                .build());

        var response = this.mockMvc
                .perform(MockMvcRequestBuilders.get(URL_USUARIO_COM_LOGIN.replace("{login}", "testeLogin"))
                        .header("Authorization", "Bearer " + this.token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isOk()
                )
                .andReturn();
        var responseAppString = response.getResponse().getContentAsString();
        var responseApp = this.objectMapper
                .readValue(responseAppString, new TypeReference<Boolean>() {});

        this.repository.deleteById("gustavo");
        Assertions.assertTrue(responseApp);
    }

    @Test
    public void existe_deveRetornar204_naoEcontraNaBaseDeDados() throws Exception {

        this.mockMvc
                .perform(MockMvcRequestBuilders.get(URL_USUARIO_COM_LOGIN.replace("{login}", "testeLogin"))
                        .header("Authorization", "Bearer " + this.token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isNoContent()
                )
                .andReturn();

        this.repository.deleteById("gustavo");
        Assertions.assertEquals(0, this.repository.findAll().size());
    }

    @Test
    public void existe_deveRetornar401_semToken() throws Exception {

        this.mockMvc
                .perform(MockMvcRequestBuilders.get(URL_USUARIO_COM_LOGIN.replace("{login}", "testeLogin"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isUnauthorized()
                );

        this.repository.deleteById("gustavo");
        Assertions.assertEquals(0, this.repository.findAll().size());
    }

    @Test
    public void existe_deveRetornar401_tokenInvalido() throws Exception {

        this.mockMvc
                .perform(MockMvcRequestBuilders.get(URL_USUARIO_COM_LOGIN.replace("{login}", "testeLogin"))
                        .header("Authorization", "Bearer TESTE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isUnauthorized()
                );

        this.repository.deleteById("gustavo");
        Assertions.assertEquals(0, this.repository.findAll().size());
    }

    @Test
    public void existe_deveRetornar401_tokenExpirado() throws Exception {

        this.mockMvc
                .perform(MockMvcRequestBuilders.get(URL_USUARIO_COM_LOGIN.replace("{login}", "testeLogin"))
                        .header("Authorization", "Bearer " + JwtUtil.geraJwt(LocalDateTime.now()
                                .minusHours(3)
                                .toInstant(ZoneOffset.of("-03:00"))))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isUnauthorized()
                );

        this.repository.deleteById("gustavo");
        Assertions.assertEquals(0, this.repository.findAll().size());
    }

    @Test
    public void existe_deveRetornar204_tokenSemRoleAdmin() throws Exception {
        this.repository.save(UsuarioEntity.builder()
                .login("usuarioUser")
                .senha("testeSenha")
                .role(UsuarioRoleEnum.USER)
                .build());

        this.mockMvc
                .perform(MockMvcRequestBuilders.get(URL_USUARIO_COM_LOGIN.replace("{login}", "testeLogin"))
                        .header("Authorization", "Bearer " + JwtUtil.geraJwt("usuarioUser"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isNoContent()
                );

        this.repository.deleteById("gustavo");
        this.repository.deleteById("usuarioUser");
        Assertions.assertEquals(0, this.repository.findAll().size());
    }

    @ParameterizedTest
    @MethodSource("requestValidandoCampos")
    public void cadastra_camposInvalidos_naoBuscaNaBaseDeDados(String login,
                                                               String senha) throws Exception {
        var request = new CriaUsuarioDTO(
                login,
                senha
        );
        var objectMapper = this.objectMapper
                .writer()
                .withDefaultPrettyPrinter();
        var jsonRequest = objectMapper.writeValueAsString(request);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(URL_USUARIO)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isBadRequest()
                );
    }

    @ParameterizedTest
    @MethodSource("requestValidandoCamposAtualiza")
    public void atualiza_camposInvalidos_naoBuscaNaBaseDeDados(String senha,
                                                               String role) throws Exception {
        var request = new AtualizaUsuarioDTO(
                senha,
                Objects.isNull(role) ? null : UsuarioRoleEnum.valueOf(role)
        );
        var objectMapper = this.objectMapper
                .writer()
                .withDefaultPrettyPrinter();
        var jsonRequest = objectMapper.writeValueAsString(request);

        this.mockMvc
                .perform(MockMvcRequestBuilders.put(URL_USUARIO_COM_LOGIN.replace("{login}", "testeLogin"))
                        .header("Authorization", "Bearer " + this.token)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isBadRequest()
                );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            " ",
            "ab",
            "aaaaaaaaaabbbbbbbbbbaaaaaaaaaabbbbbbbbbbaaaaaaaaaad"
    })
    public void deleta_camposInvalidos_naoDeletaNaBaseDeDados(String login) throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.delete(URL_USUARIO_COM_LOGIN.replace("{login}", login))
                        .header("Authorization", "Bearer " + this.token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isBadRequest()
                );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            " ",
            "ab",
            "aaaaaaaaaabbbbbbbbbbaaaaaaaaaabbbbbbbbbbaaaaaaaaaad"
    })
    public void busca_camposInvalidos_naoBuscaNaBaseDeDados(String login) throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get(URL_USUARIO_COM_LOGIN.replace("{login}", login))
                        .header("Authorization", "Bearer " + this.token)
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

    private static Stream<Arguments> requestValidandoCamposAtualiza() {
        return Stream.of(
                Arguments.of(null, UsuarioRoleEnum.ADMIN.name()),
                Arguments.of("", UsuarioRoleEnum.ADMIN.name()),
                Arguments.of(" ", UsuarioRoleEnum.ADMIN.name()),
                Arguments.of("ab", UsuarioRoleEnum.ADMIN.name()),
                Arguments.of("aaaaaaaaaabbbbbbbbbbaaaaaaaaaabbbbbbbbbbaaaaaaaaaad", UsuarioRoleEnum.ADMIN.name()),
                Arguments.of("testeLogin", null)
        );
    }

}

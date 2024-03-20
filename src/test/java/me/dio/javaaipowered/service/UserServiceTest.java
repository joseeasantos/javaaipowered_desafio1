package me.dio.javaaipowered.service;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mockk.impl.annotations.InjectMockKs;
import io.mockk.impl.annotations.MockK;
import io.mockk.junit5.MockKExtension;
import me.dio.controller.UserController;
import me.dio.domain.model.Account;
import me.dio.domain.model.Card;
import me.dio.domain.model.User;
import me.dio.service.UserService;
import me.dio.service.impl.UserServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;
/*********************************************************
 * testes de integração
 * powered by: José E. A. Santos
 * date: 20/03/2024
 *********************************************************/
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class UserServiceTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserService userRepository;
    @Autowired
    ObjectMapper objectMapper;

    private static String url = "/users";

    @BeforeEach
    public void beforeEach() throws JsonProcessingException {
    }
    @AfterEach
    public void afterEach(){
    }


    User buildUser(){
        User user = new User();
        user.setName("João Paulo");
        user.setAccount(new Account());
        user.getAccount().setAgency("123456");
        user.getAccount().setBalance(BigDecimal.valueOf(50000));
        user.getAccount().setLimit(BigDecimal.valueOf(1000000));
        user.getAccount().setNumber(UUID.randomUUID().toString());
        user.setCard(new Card());
        user.getCard().setLimit(BigDecimal.valueOf(200000));
        user.getCard().setNumber(UUID.randomUUID().toString());
        return user;
    }

    @Test
    @Order(1)
    @DisplayName("Deve impedir cadastro de usuario sem cartão")
    void deveImpedirCadastroDeUsuarioSemCartao() throws Exception {
        User user = buildUser();
        user.setCard(null);
        mockMvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(2)
    @DisplayName("Deve impedir cadastro de usuario sem conta")
    void deveImpedirCadastroDeUsuarioSemConta() throws Exception {
        User user = buildUser();
        user.setAccount(null);
        mockMvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andDo(MockMvcResultHandlers.print());
    }
    @Test
    @Order(3)
    @DisplayName("Deve salvar usuario")
    void deveSalvarUsuario() throws Exception {
        User user = buildUser();
        mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    @Order(4)
    @DisplayName("Deve atualizar dados do usuario")
    void deveAtualizarUsuario() throws Exception {
        Optional<List<User>> userList = Optional.ofNullable(userRepository.findAll());

        userList.ifPresent(lista ->{
            User user = lista.stream().findFirst().orElse(null);
            if (user != null){
                user.setName("João Paulo II");
                try {
                    mockMvc.perform(
                                    MockMvcRequestBuilders.post(url)
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(objectMapper.writeValueAsString(user)))
                            .andExpect(MockMvcResultMatchers.status().isCreated())
                            .andDo(MockMvcResultHandlers.print());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    @Order(5)
    @DisplayName("Deve listar os usuários")
    void deveListarUsuarios() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get(url))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

    }
}

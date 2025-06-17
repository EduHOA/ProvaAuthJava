package com.edu.atividade;

import com.edu.atividade.dto.UserLoginDto;
import com.edu.atividade.dto.UserRegistrationDto;
import com.edu.atividade.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class AuthControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testLoginSuccess() throws Exception {
        // Arrange - Criar um usuário para teste
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("testuser");
        registrationDto.setEmail("test@example.com");
        registrationDto.setPassword("password123");
        
        userService.registerUser(registrationDto);

        // Arrange - Dados de login
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsername("testuser");
        loginDto.setPassword("password123");

        // Act & Assert - Testar o endpoint de login
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }

    @Test
    void testLoginFailureEmptyFields() throws Exception {
        // Arrange - Dados de login vazios
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsername("");
        loginDto.setPassword("");

        // Act & Assert - Testar login com campos vazios
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterSuccess() throws Exception {
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("newuser");
        registrationDto.setEmail("newuser@example.com");
        registrationDto.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }

    @Test
    void testRegisterUsernameAlreadyExists() throws Exception {
        // Primeiro cadastro
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("duplicateuser");
        registrationDto.setEmail("uniqueemail@example.com");
        registrationDto.setPassword("password123");
        userService.registerUser(registrationDto);

        // Segundo cadastro com mesmo username
        UserRegistrationDto duplicateDto = new UserRegistrationDto();
        duplicateDto.setUsername("duplicateuser");
        duplicateDto.setEmail("anotheremail@example.com");
        duplicateDto.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void testRegisterEmailAlreadyExists() throws Exception {
        // Primeiro cadastro
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("uniqueuser");
        registrationDto.setEmail("duplicateemail@example.com");
        registrationDto.setPassword("password123");
        userService.registerUser(registrationDto);

        // Segundo cadastro com mesmo email
        UserRegistrationDto duplicateDto = new UserRegistrationDto();
        duplicateDto.setUsername("anotheruser");
        duplicateDto.setEmail("duplicateemail@example.com");
        duplicateDto.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void testRegisterWithEmptyFields() throws Exception {
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("");
        registrationDto.setEmail("");
        registrationDto.setPassword("");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isBadRequest());
    }
} 
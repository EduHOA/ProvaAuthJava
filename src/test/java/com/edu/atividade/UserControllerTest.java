package com.edu.atividade;

import com.edu.atividade.dto.UserLoginDto;
import com.edu.atividade.dto.UserRegistrationDto;
import com.edu.atividade.model.User;
import com.edu.atividade.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class UserControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private MockMvc mockMvc;

    private String userToken;
    private String adminToken;
    private Long userId;
    private Long adminId;
    private String userUsername;
    private String adminUsername;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        userRepository.deleteAll();

        // Gera sufixo único
        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        userUsername = "user1_" + unique;
        String userEmail = "user1_" + unique + "@email.com";
        adminUsername = "admin1_" + unique;
        String adminEmail = "admin1_" + unique + "@email.com";

        // Cria usuário comum
        UserRegistrationDto userDto = new UserRegistrationDto();
        userDto.setUsername(userUsername);
        userDto.setEmail(userEmail);
        userDto.setPassword("password123");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)));

        // Cria admin
        UserRegistrationDto adminDto = new UserRegistrationDto();
        adminDto.setUsername(adminUsername);
        adminDto.setEmail(adminEmail);
        adminDto.setPassword("adminpass");
        mockMvc.perform(post("/api/auth/register/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminDto)));

        // Login usuário
        UserLoginDto loginUser = new UserLoginDto();
        loginUser.setUsername(userUsername);
        loginUser.setPassword("password123");
        String userResponse = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginUser)))
                .andReturn().getResponse().getContentAsString();
        userToken = objectMapper.readTree(userResponse).get("token").asText();

        // Login admin
        UserLoginDto loginAdmin = new UserLoginDto();
        loginAdmin.setUsername(adminUsername);
        loginAdmin.setPassword("adminpass");
        String adminResponse = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginAdmin)))
                .andReturn().getResponse().getContentAsString();
        adminToken = objectMapper.readTree(adminResponse).get("token").asText();

        // Recupera IDs
        Optional<User> userOpt = userRepository.findByUsername(userUsername);
        Optional<User> adminOpt = userRepository.findByUsername(adminUsername);
        userId = userOpt.map(User::getId).orElse(null);
        adminId = adminOpt.map(User::getId).orElse(null);
    }

    @Test
    void testGetUserProfile() throws Exception {
        // Busca usuário persistido para depuração
        Optional<User> userOpt = userRepository.findByUsername(userUsername);
        System.out.println("[DEBUG] Username persistido: " + (userOpt.isPresent() ? userOpt.get().getUsername() : "NÃO ENCONTRADO"));
        System.out.println("[DEBUG] Token usado: " + userToken);

        mockMvc.perform(get("/api/users/profile")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(userUsername))
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.role").exists())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void testUpdateUserProfile() throws Exception {
        String updateUnique = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        UserRegistrationDto updateDto = new UserRegistrationDto();
        updateDto.setUsername("user1updated_" + updateUnique);
        updateDto.setEmail("user1updated_" + updateUnique + "@email.com");
        updateDto.setPassword("newpassword");

        mockMvc.perform(put("/api/users/profile")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(updateDto.getUsername()));
    }

    @Test
    void testGetAllUsersAsAdmin() throws Exception {
        mockMvc.perform(get("/api/users")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").exists());
    }

    @Test
    void testGetUserByIdAsAdmin() throws Exception {
        mockMvc.perform(get("/api/users/" + userId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").exists());
    }

    @Test
    void testGetUserByIdAsSelf() throws Exception {
        mockMvc.perform(get("/api/users/" + userId)
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").exists());
    }

    @Test
    void testUpdateUserByIdAsAdmin() throws Exception {
        String updateUnique = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        UserRegistrationDto updateDto = new UserRegistrationDto();
        updateDto.setUsername("user1adminedit_" + updateUnique);
        updateDto.setEmail("user1adminedit_" + updateUnique + "@email.com");
        updateDto.setPassword("admineditpass");

        mockMvc.perform(put("/api/users/" + userId)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(updateDto.getUsername()));
    }

    @Test
    void testDeleteUserByIdAsAdmin() throws Exception {
        mockMvc.perform(delete("/api/users/" + userId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
        assertThat(userRepository.findById(userId)).isEmpty();
    }
} 
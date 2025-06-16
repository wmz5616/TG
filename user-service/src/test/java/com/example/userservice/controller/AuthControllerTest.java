package com.example.userservice.controller;

import com.example.userservice.dto.AuthenticationRequest;
import com.example.userservice.dto.RegistrationRequest;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("newUser");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        assertTrue(userRepository.findByUsername("newUser").isPresent());
    }

    @Test
    void testRegisterUser_UsernameAlreadyTaken() throws Exception {
        userService.registerNewUser(new RegistrationRequest("existingUser", "password", "email"));

        RegistrationRequest request = new RegistrationRequest("existingUser", "newPass", "newEmail");

        // 【【测试修正】】期望得到 409 Conflict 状态码
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void testLogin_SuccessAndAccessProtectedResource() throws Exception {
        User user = userService.registerNewUser(new RegistrationRequest("loginUser", "password123", "email"));

        AuthenticationRequest loginRequest = new AuthenticationRequest();
        loginRequest.setUsername("loginUser");
        loginRequest.setPassword("password123");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String jwt = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("jwt").asText();

        // 【【测试修正】】在请求头中手动添加 X-Authenticated-Username，模拟网关行为
        mockMvc.perform(get("/api/users/" + user.getId())
                        .header("Authorization", "Bearer " + jwt)
                        .header("X-Authenticated-Username", "loginUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("loginUser"));
    }

    @Test
    void testGetUserById_AccessingAnotherUser_Forbidden() throws Exception {
        User userA = userService.registerNewUser(new RegistrationRequest("userA", "passA", "a@a.com"));
        User userB = userService.registerNewUser(new RegistrationRequest("userB", "passB", "b@b.com"));

        AuthenticationRequest authReqA = new AuthenticationRequest();
        authReqA.setUsername("userA");
        authReqA.setPassword("passA");

        MvcResult loginResultA = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authReqA)))
                .andReturn();
        String jwtA = objectMapper.readTree(loginResultA.getResponse().getContentAsString()).get("jwt").asText();

        // 【【测试修正】】使用 userA 的身份去获取 userB 的信息，也要加上模拟的请求头
        mockMvc.perform(get("/api/users/" + userB.getId())
                        .header("Authorization", "Bearer " + jwtA)
                        .header("X-Authenticated-Username", "userA")) // 当前登录的是 userA
                .andExpect(status().isForbidden()); // 期望被禁止访问
    }

    // (testLogin_BadCredentials 这个测试不用修改)
}

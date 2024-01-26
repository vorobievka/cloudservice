package com.example.cloudservice.service;

import com.example.cloudservice.model.AuthToken;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static com.example.cloudservice.exceptions.MessageConstant.LOGIN;
import static com.example.cloudservice.exceptions.MessageConstant.LOGIN_NOT_VALID_PASSWORD;
import static com.example.cloudservice.service.PrepareInfoForTest.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class UserServiceTest {


    @MockBean
    private AuthToken authToken;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void loginValidTest() throws Exception {
        when(authToken.getAuthToken()).thenReturn(TOKEN);
        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\": \"" + TEST + "\", \"password\": \"" + TEST + "\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.auth-token").value(TOKEN));
    }

    @Test
    public void loginNonValidTest() throws Exception {
        when(authToken.getAuthToken()).thenReturn(TOKEN);
        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\": \"" + TEST + "\", \"password\": \"" + NON_VALID_TEST + "\"}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(MockMvcResultMatchers.content().string(LOGIN_NOT_VALID_PASSWORD));
    }
}
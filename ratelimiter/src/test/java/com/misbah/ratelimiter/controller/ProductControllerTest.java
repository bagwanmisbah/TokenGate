
package com.misbah.ratelimiter.controller;

import com.misbah.ratelimiter.service.JwtService;
import com.misbah.ratelimiter.service.RateLimiterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {


    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private RateLimiterService rateLimiterService;


    @MockBean
    private UserDetailsService userDetailsService;


    @Autowired
    private JwtService jwtService;

    private String testToken;

    @BeforeEach
    void setUp() {
        UserDetails userDetails = new User("testuser", "password", new ArrayList<>());


        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);

        testToken = jwtService.generateToken(userDetails);
    }

    @Test
    void getProductById_WhenAllowed_ShouldReturnOk() throws Exception {

        when(rateLimiterService.isAllowedFixedWindow(any(), anyInt(), anyInt())).thenReturn(true);

        mockMvc.perform(get("/api/v1/products/1")
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk());
    }

    @Test
    void getProductById_WhenRateLimited_ShouldReturnTooManyRequests() throws Exception {

        when(rateLimiterService.isAllowedFixedWindow(any(), anyInt(), anyInt())).thenReturn(false);

        mockMvc.perform(get("/api/v1/products/1")
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isTooManyRequests());
    }
}

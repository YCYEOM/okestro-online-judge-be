package com.okestro.okestroonlinejudge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.okestro.okestroonlinejudge.domain.Role;
import com.okestro.okestroonlinejudge.domain.UserEntity;
import com.okestro.okestroonlinejudge.dto.request.SignUpRequest;
import com.okestro.okestroonlinejudge.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 인증 API 테스트.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "Password123!";
    private static final String TEST_USERNAME = "테스트유저";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입 성공")
    void signUp_Success() throws Exception {
        SignUpRequest request = new SignUpRequest();
        request.setUserName("홍길동");
        request.setEmail("hong@example.com");
        request.setPassword("Password123!");
        request.setGroupName("오케스트로");

        mockMvc.perform(post("/u/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.email").value("hong@example.com"))
                .andExpect(jsonPath("$.data.userName").value("홍길동"));
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signUp_Fail_DuplicateEmail() throws Exception {
        // 기존 사용자 생성
        createTestUser();

        SignUpRequest request = new SignUpRequest();
        request.setUserName("다른유저");
        request.setEmail(TEST_EMAIL); // 중복 이메일
        request.setPassword("Password123!");

        mockMvc.perform(post("/u/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 성공")
    void login_Success() throws Exception {
        // 사용자 생성
        createTestUser();

        mockMvc.perform(post("/oauth2/token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("grant_type", "password")
                        .param("username", TEST_EMAIL)
                        .param("password", TEST_PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.refresh_token").exists())
                .andExpect(jsonPath("$.token_type").value("Bearer"))
                .andExpect(jsonPath("$.expires_in").exists());
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void login_Fail_WrongPassword() throws Exception {
        createTestUser();

        mockMvc.perform(post("/oauth2/token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("grant_type", "password")
                        .param("username", TEST_EMAIL)
                        .param("password", "wrongpassword"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("토큰 갱신 성공")
    void refreshToken_Success() throws Exception {
        createTestUser();

        // 먼저 로그인해서 refresh token 획득
        MvcResult loginResult = mockMvc.perform(post("/oauth2/token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("grant_type", "password")
                        .param("username", TEST_EMAIL)
                        .param("password", TEST_PASSWORD))
                .andExpect(status().isOk())
                .andReturn();

        String response = loginResult.getResponse().getContentAsString();
        String refreshToken = objectMapper.readTree(response).get("refresh_token").asText();

        // refresh token으로 새 토큰 발급
        mockMvc.perform(post("/oauth2/token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("grant_type", "refresh_token")
                        .param("refresh_token", refreshToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.refresh_token").exists());
    }

    @Test
    @DisplayName("닉네임 중복 확인 - 사용 가능")
    void checkNickname_Available() throws Exception {
        mockMvc.perform(get("/auth/check-nickname")
                        .param("nickname", "새로운닉네임"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.available").value(true));
    }

    @Test
    @DisplayName("닉네임 중복 확인 - 사용 불가")
    void checkNickname_NotAvailable() throws Exception {
        createTestUser();

        mockMvc.perform(get("/auth/check-nickname")
                        .param("nickname", TEST_USERNAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.available").value(false));
    }

    @Test
    @DisplayName("이메일 중복 확인")
    void checkEmail() throws Exception {
        mockMvc.perform(get("/u/auth/check-email")
                        .param("email", "new@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.available").value(true));
    }

    @Test
    @DisplayName("현재 사용자 정보 조회 - 인증 필요")
    void getCurrentUser_Unauthorized() throws Exception {
        // 인증 없이 접근 시 401 또는 userDetails가 null이어서 401 반환
        mockMvc.perform(get("/u/users/me"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    // 인증 없이 접근하면 401 또는 컨트롤러에서 처리한 401
                    assert status == 401 || status == 403 : "Expected 401 or 403, but got " + status;
                });
    }

    @Test
    @DisplayName("현재 사용자 정보 조회 - 성공")
    void getCurrentUser_Success() throws Exception {
        createTestUser();

        // 로그인해서 토큰 획득
        MvcResult loginResult = mockMvc.perform(post("/oauth2/token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("grant_type", "password")
                        .param("username", TEST_EMAIL)
                        .param("password", TEST_PASSWORD))
                .andExpect(status().isOk())
                .andReturn();

        String response = loginResult.getResponse().getContentAsString();
        String accessToken = objectMapper.readTree(response).get("access_token").asText();

        // 토큰으로 사용자 정보 조회
        mockMvc.perform(get("/u/users/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.data.userName").value(TEST_USERNAME));
    }

    @Test
    @DisplayName("이메일 인증 코드 발송")
    void sendVerificationCode() throws Exception {
        mockMvc.perform(post("/u/auth/send-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"test@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.sent").value(true));
    }

    @Test
    @DisplayName("이메일 인증 코드 검증 - 성공")
    void verifyEmailCode_Success() throws Exception {
        // 먼저 인증 코드 발송 (저장소에 코드 저장)
        mockMvc.perform(post("/u/auth/send-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"verify@example.com\"}"))
                .andExpect(status().isOk());

        // 개발 모드에서는 123456 코드로 검증
        mockMvc.perform(post("/u/auth/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"verify@example.com\", \"code\": \"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.verified").value(true));
    }

    @Test
    @DisplayName("이메일 인증 코드 검증 - 실패")
    void verifyEmailCode_Fail() throws Exception {
        mockMvc.perform(post("/u/auth/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"test@example.com\", \"code\": \"000000\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.verified").value(false));
    }

    @Test
    @DisplayName("약관 조회")
    void getTerm() throws Exception {
        mockMvc.perform(get("/auth/term")
                        .param("type", "service"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.type").value("service"));
    }

    private void createTestUser() {
        UserEntity user = new UserEntity(
                TEST_USERNAME,
                passwordEncoder.encode(TEST_PASSWORD),
                TEST_EMAIL,
                Role.USER,
                null,
                null
        );
        userRepository.save(user);
    }
}

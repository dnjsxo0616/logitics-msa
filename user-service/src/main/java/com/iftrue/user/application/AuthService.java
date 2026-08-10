package com.iftrue.user.application;

import com.iftrue.user.domain.User;
import com.iftrue.user.domain.UserRepository;
import com.iftrue.user.global.exception.BusinessException;
import com.iftrue.user.global.exception.ErrorCode;
import com.iftrue.user.global.security.JwtUtil;
import com.iftrue.user.infrastructure.redis.RefreshTokenRepository;
import com.iftrue.user.presentation.request.LoginRequest;
import com.iftrue.user.presentation.response.LoginResponse;
import com.iftrue.user.presentation.response.RefreshTokenResponse;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private final RefreshTokenRepository refreshTokenRepository;

    public LoginResponse login(LoginRequest request) {

        // 1. 사용자 조회
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.INVALID_CREDENTIALS)
                );

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(
                request.password(),
                user.getPassword()
        )) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        // 3. 사용자 상태 확인
        if (!user.isApproved()) {
            throw new BusinessException(ErrorCode.USER_NOT_APPROVED);
        }

        // 4. JWT 발급
        String accessToken = jwtUtil.createAccessToken(
                user.getId(),
                user.getRole(),
                user.getHubId(),
                user.getCompanyId()

        );
        String refreshToken = jwtUtil.createRefreshToken(
                user.getId()
        );
        refreshTokenRepository.save(
                user.getId(),
                refreshToken
        );
//         5. 응답
        return new LoginResponse(accessToken,refreshToken);

    }

    @Transactional
    public RefreshTokenResponse refresh(String refreshToken) {

        // 1. Refresh Token JWT 검증 + userId 추출
        UUID userId;

        try {
            userId = jwtUtil.getUserIdFromRefreshToken(refreshToken);
        } catch (JwtException | IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        // 2. Redis에 저장된 Refresh Token 조회
        String savedRefreshToken =
                refreshTokenRepository.findByUserId(userId);

        if (savedRefreshToken == null) {
            throw new BusinessException(
                    ErrorCode.REFRESH_TOKEN_NOT_FOUND
            );
        }

        // 3. Redis의 Refresh Token과 비교
        if (!savedRefreshToken.equals(refreshToken)) {
            throw new BusinessException(
                    ErrorCode.INVALID_TOKEN
            );
        }

        // 4. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.USER_NOT_FOUND)
                );

        // 5. 새로운 Access Token 발급
        String accessToken = jwtUtil.createAccessToken(
                user.getId(),
                user.getRole(),
                user.getHubId(),
                user.getCompanyId()
        );

        return new RefreshTokenResponse(accessToken);
    }

    public void logout(UUID userId) {

        refreshTokenRepository.deleteByUserId(userId);
    }


}

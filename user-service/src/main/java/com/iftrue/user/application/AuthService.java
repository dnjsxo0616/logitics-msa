package com.iftrue.user.application;

import com.iftrue.user.domain.User;
import com.iftrue.user.domain.UserRepository;
import com.iftrue.user.global.exception.BusinessException;
import com.iftrue.user.global.exception.ErrorCode;
import com.iftrue.user.global.security.JwtUtil;
import com.iftrue.user.presentation.request.LoginRequest;
import com.iftrue.user.presentation.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

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
        String refreshToken = jwtUtil.createRefreshToken(user.getId());

//         5. 응답
        return new LoginResponse(accessToken,refreshToken);

    }
}

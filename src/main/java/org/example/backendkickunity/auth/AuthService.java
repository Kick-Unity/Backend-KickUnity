package org.example.backendkickunity.auth;

import lombok.extern.slf4j.Slf4j;
import org.example.backendkickunity.auth.exception.AuthExceptionType;
import org.example.backendkickunity.global.util.JWTUtil;
import org.example.backendkickunity.auth.exception.AuthException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {

    private final JWTUtil jwtUtil;

    public AuthService(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // Authorization 헤더에서 토큰을 추출하는 메서드
    public String extractTokenFromAuthorizationHeader(String authorizationHeader) {
        log.debug("Authorization 헤더: {}", authorizationHeader);  // Authorization 헤더 로그 출력

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.warn("유효하지 않은 Authorization 헤더: {}", authorizationHeader);  // 유효하지 않은 헤더 경고 로그
            throw new AuthException(AuthExceptionType.INVALID_AUTHORIZATION_HEADER);
        }

        // Bearer 토큰에서 실제 토큰 추출
        String token = authorizationHeader.substring(7);  // "Bearer " 이후 부분만 추출
        log.debug("추출된 토큰: {}", token);

        return token;
    }

    // 토큰에서 이메일을 추출하고 유효성 검사하는 메서드
    public String extractEmailFromToken(String token) {
        try {
            // 토큰 만료 여부 확인
            if (jwtUtil.isExpired(token)) {
                throw new AuthException(AuthExceptionType.TOKEN_EXPIRED);
            }

            // 토큰에서 이메일 추출
            return jwtUtil.getUsername(token);

        } catch (Exception e) {
            log.error("토큰에서 이메일을 추출하는 데 실패했습니다: {}", e.getMessage());
            throw new AuthException(AuthExceptionType.UNAUTHORIZED_ACCESS);
        }
    }

    // 최종적으로 Authorization 헤더에서 이메일을 추출하는 메서드
    public String extractEmailFromAuthorizationHeader(String authorizationHeader) {
        String token = extractTokenFromAuthorizationHeader(authorizationHeader);
        return extractEmailFromToken(token);
    }
}

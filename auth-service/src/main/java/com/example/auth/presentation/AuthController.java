package com.example.auth.presentation;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.auth.application.AuthService;
import com.example.auth.application.dto.response.GetAuthorizedUriResponse;
import com.example.auth.application.dto.response.OAuthLoginResponse;
import com.example.auth.application.dto.response.RegisterResponse;
import com.example.auth.jwt.TokenInfo;
import com.example.auth.jwt.TokenInfoResponse;
import com.example.auth.presentation.dto.request.OAuthLoginRequest;
import com.example.auth.presentation.dto.request.RegisterRequest;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "인증")
@RestController
@RequiredArgsConstructor
@RequestMapping("/")
@Slf4j
public class AuthController {
    private final AuthService authService;

    // TODO 삭제해야 하는 로직입니다.
    //  Auth Code를 확인하기 위해 임의로 설정한 URL입니다.
    @GetMapping("/login/oauth2/code/google")
    public String showAuthCode(String code) {
        return code;
    }

    @GetMapping("/oauth/{provider}/authorized-uri")
    public ResponseEntity<GetAuthorizedUriResponse> getAuthorizedUri(@Parameter(description = "OAuth 인증 방식 중 "
        + "어떤 방식을 사용할지 선택합니다", example = "google") @PathVariable String provider) {
        return ResponseEntity.ok(authService.getAuthorizedUri(provider));
    }

    @PostMapping("/oauth/{provider}/login")
    public ResponseEntity<OAuthLoginResponse> oauthLogin(@Parameter(description = "OAuth 인증 방식 중 "
        + "어떤 방식을 사용할지 선택합니다", example = "google") @PathVariable String provider,
                                                         @Valid @RequestBody OAuthLoginRequest request,
                                                         HttpServletResponse httpServletResponse) {
        OAuthLoginResponse response = authService.login(provider, request.getAuthCode());
        addCookieUsingRefreshToken(httpServletResponse, response.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/auth/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request,
                                                     HttpServletResponse httpServletResponse) {
        log.debug("[AuthController] Register 로직 실행");
        RegisterResponse response = authService.register(request);
        addCookieUsingRefreshToken(httpServletResponse, response.getRefreshToken());
        return ResponseEntity.created(URI.create("/members/" + response.getId()))
            .body(response);
    }

    @PostMapping("/auth/refresh-token")
    public ResponseEntity<TokenInfo> refreshToken(@CookieValue(name = "refresh") Cookie refreshCookie,
                                                  HttpServletResponse httpServletResponse) {
        String refreshToken = refreshCookie.getValue();
        TokenInfo response = authService.refreshToken(refreshToken);
        addCookieUsingRefreshToken(httpServletResponse, response.getRefreshToken());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/auth/parse")
    public ResponseEntity<TokenInfoResponse> parseToken(@RequestParam String token) {
        return ResponseEntity.ok().body(authService.parse(token));
    }

    private void addCookieUsingRefreshToken(HttpServletResponse httpServletResponse, String refreshToken) {
        if (refreshToken == null) {
            return;
        }
        String cookieValue =
            String.format("refresh=%s; Max-Age=%s; Path=/; HttpOnly; SameSite=Strict", refreshToken, 60 * 60 * 24 * 30);
        httpServletResponse.setHeader("Set-Cookie", cookieValue);
    }
}

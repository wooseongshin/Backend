package com.example.demo.application.dto.response;

import com.example.demo.domain.OAuthMember;
import com.example.demo.jwt.TokenInfo;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuthLoginResponse {
    private String accessToken;
    private String refreshToken;
    private String grantType;
    private String profileUrl;
    private String email;
    private boolean old;

    @Builder
    private OAuthLoginResponse(String accessToken, String refreshToken, String grantType,
                               String profileUrl, String email, boolean old) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.grantType = grantType;
        this.profileUrl = profileUrl;
        this.email = email;
        this.old = old;
    }

    public static OAuthLoginResponse create(OAuthMember oAuthMember, TokenInfo tokenInfo) {
        return OAuthLoginResponse.builder()
            .accessToken(tokenInfo.getAccessToken())
            .refreshToken(tokenInfo.getRefreshToken())
            .grantType(tokenInfo.getGrantType())
            .profileUrl(oAuthMember.getProfileUrl())
            .email(oAuthMember.getEmail())
            .old(oAuthMember.isOld())
            .build();
    }
}

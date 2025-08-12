package com.example.board.security.auth.oauth.provider;

import java.util.Map;

public class KakaoOAuth2UserInfo implements OAuth2UserInfo{
    private final Map<String,Object> attributes;

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getEmail() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        return kakaoAccount != null ? kakaoAccount.get("email").toString() : null;
    }
}

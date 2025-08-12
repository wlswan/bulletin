package com.example.board.security.auth.oauth;

import com.example.board.security.User;
import com.example.board.security.UserRepository;
import com.example.board.security.auth.PrincipalDetails;
import com.example.board.security.auth.Role;
import com.example.board.security.auth.oauth.provider.GoogleOAuth2UserInfo;
import com.example.board.security.auth.oauth.provider.KakaoOAuth2UserInfo;
import com.example.board.security.auth.oauth.provider.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest,OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        //공급자 정보 kakao, google, naver 등등
        System.out.println("loaduser 호출 ");
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo userInfo;
        if("kakao".equals(registrationId)){
            userInfo = new KakaoOAuth2UserInfo(oAuth2User.getAttributes());
        } else if ("google".equals(registrationId)) {
            userInfo = new GoogleOAuth2UserInfo(oAuth2User.getAttributes());
        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 공급자입니다.");
        }

        User user = userRepository.findByEmail(userInfo.getEmail()).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(userInfo.getEmail());
            newUser.setProvider(registrationId);
            newUser.setProviderId(userInfo.getProviderId());
            newUser.setRole(Role.ROLE_USER);
            return userRepository.save(newUser);
        });
        return new PrincipalDetails(user, oAuth2User.getAttributes());
    }
}

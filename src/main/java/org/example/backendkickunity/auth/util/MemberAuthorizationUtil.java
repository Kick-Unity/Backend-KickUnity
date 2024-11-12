package org.example.backendkickunity.auth.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

public class MemberAuthorizationUtil {

    public static String getLoginMemberEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            return user.getUsername();  // 이메일이 사용자 이름(username)으로 설정되어 있다고 가정
        }

        return null;  // 인증되지 않은 경우
    }
}

//import org.example.backendkickunity.member.dto.CustomUserDetails;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//public class MemberAuthorizationUtil {
//
//    private MemberAuthorizationUtil() {
//        throw new AssertionError();
//    }
//    public static String getLoginMemberEmail() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//
//        return userDetails.getUsername();
//    }
//}
package org.example.backendkickunity.controller;

import lombok.RequiredArgsConstructor;
import org.example.backendkickunity.service.MemberService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController { //서비스와 닿아있는 layer

    private final MemberService memberService;


//    @PostMapping("/join")
//    public String join(@RequestBody JoinRequest joinRequest) {
//        String memberId = joinRequest.getMemberId();
//        String name = joinRequest.getName();
//        String password = joinRequest.getPassword();
//
//        return memberId;
//    }

}
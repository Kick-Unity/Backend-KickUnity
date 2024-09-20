package org.example.backedkickunity.controller;

import lombok.RequiredArgsConstructor;
import org.example.backedkickunity.controller.dto.JoinRequest;
import org.example.backedkickunity.service.MemberService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController { //서비스와 닿아있는 layer

    private final MemberService memberService;


    @PostMapping("/join")
    public String join(@RequestBody JoinRequest joinRequest) {
        String memberId = joinRequest.getMemberId();
        String name = joinRequest.getName();
        String password = joinRequest.getPassword();
        String phone = joinRequest.getPhone();

        String result = memberService.join(joinRequest);

        if("success".equalsIgnoreCase(result)){
            return "success";
        }else{
            return "fail";
        }
    }

}

package org.example.backendkickunity;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ResponseBody
public class MainApiController {

    @GetMapping
    public String mainP() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        return "Main Controller : " + name;
    }

}

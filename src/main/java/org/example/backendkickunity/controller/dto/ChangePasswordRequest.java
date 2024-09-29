package org.example.backendkickunity.controller.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ChangePasswordRequest {

    @NotNull
    private String oldPassword;

    @NotNull
    private String newPassword;
}

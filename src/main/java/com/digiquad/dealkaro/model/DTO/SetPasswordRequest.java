package com.digiquad.dealkaro.model.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SetPasswordRequest {
    @NotNull
    private String newPassword;
}

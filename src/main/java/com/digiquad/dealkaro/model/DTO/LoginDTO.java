package com.digiquad.dealkaro.model.DTO;

import lombok.*;

import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    private String inputKey;
    private String password;
}
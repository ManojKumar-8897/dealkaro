package com.digiquad.dealkaro.model.DTO;



import com.digiquad.dealkaro.entity.RefreshToken;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenInfo {
    private String token;
    private Instant expiry;
}

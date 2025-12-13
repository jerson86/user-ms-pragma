package com.pragma.powerup.application.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class TokenResponse {
    private String jwt;
    private final String tokenType = "Bearer";
}

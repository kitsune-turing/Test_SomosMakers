package com.kitsune.BanckLoad.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDTO {

    private String token;
    @Builder.Default
    private String type = "Bearer";
    private String username;
    private String email;
    private String fullName;
    private Set<String> roles;
}

package com.minhduc.smartrestaurant.domain.response.email;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResEmailResetPassword {
    private String endpoint;
    private String token;
    private long expiresInMinutes;
}

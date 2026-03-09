package com.minhduc.smartrestaurant.domain.response;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResCategoryDTO {
    private long id;
    private String name;
    private Instant createdAt;
    private String createdBy;
}

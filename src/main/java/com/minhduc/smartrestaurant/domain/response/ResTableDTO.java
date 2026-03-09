package com.minhduc.smartrestaurant.domain.response;

import java.time.Instant;

import com.minhduc.smartrestaurant.util.constant.TableEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResTableDTO {
    private long id;
    private String name;
    private String qrToken;
    private TableEnum occupied;
    private Instant createdAt;
    private String createdBy;
}

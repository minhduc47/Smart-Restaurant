package com.minhduc.smartrestaurant.domain.dto;

import java.time.Instant;

import com.minhduc.smartrestaurant.util.constant.GenderEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResUpdateUserDTO {
    private long id;
    private String name;
    private GenderEnum gender;
    private String address;
    private int age;
    private Instant updatedAt;
}
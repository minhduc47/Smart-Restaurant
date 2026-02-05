package com.minhduc.smartrestaurant.domain.dto;

import java.time.Instant;

import com.minhduc.smartrestaurant.util.constant.GenderEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResUserDTO {
    private long id;
    private String name;
    private String email;
    private GenderEnum gender;
    private String address;
    private int age;
    private Instant updatedAt;
    private Instant createdAt;
}

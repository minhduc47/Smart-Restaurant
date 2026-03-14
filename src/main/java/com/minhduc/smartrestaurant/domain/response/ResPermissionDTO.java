package com.minhduc.smartrestaurant.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResPermissionDTO {
    private long id;
    private String name;
    private String apiPath;
    private String method;
    private String module;
}
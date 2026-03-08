package com.minhduc.smartrestaurant.domain.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DishResponseDTO {
    private long id;
    private String name;
    private String description;
    private long price;
    private String image;
    private boolean active;
    private Long categoryId;
    private String categoryName;
}

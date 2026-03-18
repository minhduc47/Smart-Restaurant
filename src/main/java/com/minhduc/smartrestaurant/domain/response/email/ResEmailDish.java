package com.minhduc.smartrestaurant.domain.response.email;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResEmailDish {
    private String dishName;
    private long price;
    private String image;
    private String categoryName;
}

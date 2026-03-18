package com.minhduc.smartrestaurant.domain.response;

import java.time.Instant;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResSubscriberDTO {

    private long id;
    private String email;
    private String name;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
    private List<String> categoryNames;
}

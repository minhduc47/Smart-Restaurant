package com.minhduc.smartrestaurant.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultPaginationDTO {
    private Meta meta;
    private Object result;

    @Getter
    @Setter
    public static class Meta {
        private int page; // Trang hiện tại
        private int pageSize; // Số lượng bản ghi
        private int pages; // Tổng số trang
        private long total; // Tổng số phần tử
    }
}
package com.minhduc.smartrestaurant.util.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ApiMessage {
    String value();
    // @interface: Dùng để biến class -> annotation
    // @Target(ElementType.METHOD): Phạm vi hoạt động dùng cho Method
    // @Retention(RetentionPolicy.RUNTIME): Hoạt động trong quá trình chạy dự án

}
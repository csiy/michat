package com.chester.michat.utils.json;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 64 位整型 ID
 *
 * <p>
 * JSON.parse 对超过 2^53 的整型值会有精度损失，因此对此类的 ID 类型采用 16 进制字符串表示
 * </p>
 *
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = Int64IdSerializer.class)
public @interface Int64Id {

}

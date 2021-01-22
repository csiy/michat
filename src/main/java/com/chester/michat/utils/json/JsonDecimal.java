package com.chester.michat.utils.json;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.RoundingMode;

/**
 * JSON 数字注解，用于指定输出 JSON 的数字的精度
 *
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = JsonDecimalSerializer.class)
public @interface JsonDecimal {

	/**
	 * 小数位数
	 *
	 * @return
	 */
	int scale() default 0;

	/**
	 * 小数截取模式
	 *
	 * @return
	 */
	RoundingMode round() default RoundingMode.HALF_UP;

}

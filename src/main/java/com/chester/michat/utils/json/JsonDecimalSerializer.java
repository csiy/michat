package com.chester.michat.utils.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * JSON 数字序列化
 *
 */
public class JsonDecimalSerializer extends JsonSerializer<Number> implements ContextualSerializer {

	private static final ConcurrentMap<String, JsonDecimalSerializer> cache = new ConcurrentHashMap<>();

	private static JsonDecimalSerializer getInstance(final int scale, final RoundingMode round) {
		String key = scale + round.toString();
		return cache.computeIfAbsent(key, k -> {
			return new JsonDecimalSerializer(scale, round);
		});
	}

	final int scale;
	final RoundingMode round;

	public JsonDecimalSerializer() {
		this(0, RoundingMode.HALF_UP);
	}

	public JsonDecimalSerializer(int scale, RoundingMode round) {
		this.scale = scale;
		this.round = round;
	}

	@Override
	public void serialize(Number value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		if (value == null) {
			gen.writeNull();
		} else {
			gen.writeNumber(new BigDecimal(value.doubleValue()).setScale(scale, round));
		}
	}

	@Override
	public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property)
			throws JsonMappingException {
		if (property == null) {
			return this;
		}
		JsonDecimal anno = property.getAnnotation(JsonDecimal.class);
		if (anno == null) {
			anno = property.getContextAnnotation(JsonDecimal.class);
		}
		if (anno == null) {
			return this;
		}
		return getInstance(anno.scale(), anno.round());
	}

}

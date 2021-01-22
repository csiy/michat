package com.chester.michat.utils.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.*;

/**
 * JSON 帮助类
 *
 */
public class JSON {
	private static final ObjectMapper OM;

	static {
		ObjectMapper om = new ObjectMapper(new JsonFactory());
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		om.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		om.setTimeZone(TimeZone.getDefault());
		OM = om;
	}

	/**
	 * 讲 Java 对象转为 JSON 字符串
	 *
	 * @param obj
	 * @return
	 */
	public static String stringify(Object obj) {
		try {
			return OM.writeValueAsString(obj);
		} catch (Exception e) {
			throw new JSONException("JSON stringify error", e);
		}
	}

	/**
	 * 讲 Java 对象转为格式美观 JSON 字符串
	 *
	 * @param obj
	 * @return
	 */
	public static String prettify(Object obj) {
		try {
			return OM.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
		} catch (Exception e) {
			throw new JSONException("JSON prettify error", e);
		}
	}

	/**
	 * 讲 Java 对象转为 JSON 字节数组
	 *
	 * @param obj
	 * @return
	 */
	public static byte[] serialize(Object obj) {
		try {
			return OM.writeValueAsBytes(obj);
		} catch (Exception e) {
			throw new JSONException("JSON serialize error", e);
		}
	}

	/**
	 * 解析 JSON 为制定类型的对象
	 *
	 * @param json
	 * @param type
	 * @return
	 */
	public static <T> T parse(String json, JavaType type) {
		try {
			return OM.readValue(json, type);
		} catch (Exception e) {
			throw new JSONException("JSON parse error", e);
		}
	}

	/**
	 * 解析 JSON 为制定类型的对象
	 *
	 * @param input
	 * @param type
	 * @return
	 */
	public static <T> T parse(InputStream input, JavaType type) {
		try {
			return OM.readValue(input, type);
		} catch (Exception e) {
			throw new JSONException("JSON parse error", e);
		}
	}

	/**
	 * 解析 JSON 为制定类型的对象
	 *
	 * @param bytes
	 * @param type
	 * @return
	 */
	public static <T> T parse(byte[] bytes, JavaType type) {
		try {
			return OM.readValue(bytes, type);
		} catch (Exception e) {
			throw new JSONException("JSON parse error", e);
		}
	}

	/**
	 * 解析 JSON 为制定类型的对象
	 *
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static <T> T parse(String json, Class<T> clazz) {
		try {
			return OM.readValue(json, clazz);
		} catch (Exception e) {
			throw new JSONException("JSON parse error", e);
		}
	}

	/**
	 * 解析 JSON 为制定类型的对象
	 *
	 * @param input
	 * @param clazz
	 * @return
	 */
	public static <T> T parse(InputStream input, Class<T> clazz) {
		try {
			return OM.readValue(input, clazz);
		} catch (Exception e) {
			throw new JSONException("JSON parse error", e);
		}
	}

	/**
	 * 解析 JSON 为制定类型的对象
	 *
	 * @param bytes
	 * @param clazz
	 * @return
	 */
	public static <T> T parse(byte[] bytes, Class<T> clazz) {
		try {
			return OM.readValue(bytes, clazz);
		} catch (Exception e) {
			throw new JSONException("JSON parse error", e);
		}
	}

	/**
	 * JSON to List
	 *
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static <T> List<T> parseList(String json, Class<T> clazz) {
		return parse(json, OM.getTypeFactory().constructCollectionType(ArrayList.class, clazz));
	}

	/**
	 * JSON to List
	 *
	 * @param input
	 * @param clazz
	 * @return
	 */
	public static <T> List<T> parseList(InputStream input, Class<T> clazz) {
		return parse(input, OM.getTypeFactory().constructCollectionType(ArrayList.class, clazz));
	}

	/**
	 * JSON to Map
	 *
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static <V> Map<String, V> parseMap(String json, Class<V> clazz) {
		return parse(json, OM.getTypeFactory().constructMapType(LinkedHashMap.class, String.class, clazz));
	}

	/**
	 * JSON to Map
	 *
	 * @param input
	 * @param clazz
	 * @return
	 */
	public static <V> Map<String, V> parseMap(InputStream input, Class<V> clazz) {
		return parse(input, OM.getTypeFactory().constructMapType(LinkedHashMap.class, String.class, clazz));
	}

	/**
	 * 读取 JSON 树节点
	 *
	 * @param json
	 * @return
	 */
	public static JsonNode readTree(String json) {
		try {
			return OM.readTree(json);
		} catch (Exception e) {
			throw new JSONException("read json tree error", e);
		}
	}

	/**
	 * 读取 JSON 树节点
	 *
	 * @param input
	 * @return
	 */
	public static JsonNode readTree(InputStream input) {
		try {
			return OM.readTree(input);
		} catch (Exception e) {
			throw new JSONException("read json tree error", e);
		}
	}

	/**
	 * 转换对象为目标类型
	 *
	 * @param source
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <V> V convert(Object source, Class<V> clazz) {
		if (source == null || clazz.isInstance(source)) {
			return (V) source;
		}
		return OM.convertValue(source, clazz);
	}

	/**
	 * 转换对象为目标类型
	 *
	 * @param source
	 * @param type
	 * @return
	 */
	public static <V> V convert(Object source, JavaType type) {
		if (source == null) {
			return (V) null;
		}
		return OM.convertValue(source, type);
	}

	/**
	 * 将源对象转换为指定类型的列表
	 *
	 * @param source
	 * @param clazz
	 * @return
	 */
	public static <T> List<T> convertList(Object source, Class<T> clazz) {
		return convert(source, OM.getTypeFactory().constructCollectionType(ArrayList.class, clazz));
	}

	/**
	 * 将源对象转换为 Map
	 *
	 * @param source
	 * @param clazz
	 * @return
	 */
	public static <V> Map<String, V> convertMap(Object source, Class<V> clazz) {
		return convert(source, OM.getTypeFactory().constructMapType(LinkedHashMap.class, String.class, clazz));
	}

	/**
	 * 将对象以 JSON 格式输出
	 *
	 * @param source
	 * @param out
	 */
	public static void write(Object source, OutputStream out) {
		try {
			OM.writeValue(out, source);
		} catch (Exception e) {
			throw new JSONException("JSON write error", e);
		}
	}

	/**
	 * 将对象以 JSON 格式输出
	 *
	 * @param source
	 * @param writer
	 */
	public static void write(Object source, Writer writer) {
		try {
			OM.writeValue(writer, source);
		} catch (Exception e) {
			throw new JSONException("JSON write error", e);
		}
	}

	/**
	 * 获取 TypeFactory
	 *
	 * @return
	 */
	public static TypeFactory getTypeFactory() {
		return OM.getTypeFactory();
	}

	/**
	 * 注册模块
	 *
	 * @param module
	 */
	public static void registerModule(Module module) {
		OM.registerModule(module);
	}

	private JSON() {
		// util class
	}
}

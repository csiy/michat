package com.chester.michat.utils.json;

/**
 * JSON 异常
 *
 */
public class JSONException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 737983260732512719L;

	public JSONException(String message, Throwable cause) {
		super(message, cause);
	}

	public JSONException(String message) {
		super(message);
	}

}

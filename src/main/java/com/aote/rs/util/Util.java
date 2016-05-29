package com.aote.rs.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Util {
	public static String encode(String error) {
		try {
			return (new BASE64Encoder()).encodeBuffer(error.getBytes("UTF-8"));
		} catch (Exception e) {
			return "";
		}
	}
	public static String decode(String error) {
		try {
			return new String((new BASE64Decoder()).decodeBuffer(error), "UTF-8");
		} catch (Exception e) {
			return "";
		}
	}
}

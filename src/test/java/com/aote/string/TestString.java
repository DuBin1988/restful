package com.aote.string;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.WebApplicationException;

import junit.framework.TestCase;

public class TestString extends TestCase {
	public void testOne() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		try {
			Date date = sdf.parse("15:50:00");
		} catch (ParseException e) {
			throw new WebApplicationException(e);
		}
	}
}

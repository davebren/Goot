package com.project.gutenberg.com.project.gutenberg.util;

import java.util.regex.Pattern;

public class String_Checker {
	public static boolean check_alphanumeric(String s) {
	    Pattern p = Pattern.compile("^[A-Za-z0-9]+$");
		return p.matcher(s).matches();
	}
}

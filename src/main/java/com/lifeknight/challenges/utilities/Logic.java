package com.lifeknight.challenges.utilities;

public class Logic {

	public static boolean isWithinRange(int a, int b, int range) {
		return (b > a - range && b < a + range) || (a > b - range && a < b + range);
	}
}

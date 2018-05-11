package com.onpositive.ai.playground.util;

import java.lang.reflect.Array;

public class ArrayUtil {

	public static boolean[] flatten (boolean[][] array) {
		int xLength = array[0].length;
		boolean[] result = new boolean[array.length * xLength];
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < xLength; j++) {
				result[i * xLength + j] = array[i][j];
			}
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T[] flatten (T[][] array) {
		int xLength = array[0].length;
		T[] result = (T[]) Array.newInstance(array.getClass().getComponentType().getComponentType(), array.length * xLength); 
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < xLength; j++) {
				result[i * xLength + j] = array[i][j];
			}
		}
		return result;
	}
	
}

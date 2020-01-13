package br.com.stockinfo.tsql.util;

import java.lang.reflect.Field;

import br.com.stockinfo.tsql.annotations.Column;

public class TSQLUtil {

	public static boolean verifyMapped(Class<?> clazz, String fieldName, String dataSource) {
		try {
			Field field = clazz.getDeclaredField(fieldName);
			Column column = Util.getColumnAnnotation(field, dataSource);
			return column != null;
		} catch (NoSuchFieldException | SecurityException e) {
			return false;
		}
		
	}
	
}

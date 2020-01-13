package br.com.stockinfo.tsql.query;

import br.com.stockinfo.tsql.util.Util;

public class Delete<T> extends Whereable<T, Delete<T>> {

	public Delete(Class<T> clazz) {
		super(clazz);
	}

	public static <T> Delete<T> from(Class<T> clazz){
		return new Delete<>(clazz);
	}

	public String toSql(String dataSource){
		return "DELETE FROM " + Util.getTable(clazz, dataSource) + whereSql(dataSource);
	}

}
package br.com.stockinfo.tsql.struct.orderby;

import java.lang.reflect.Field;

public abstract class OrderBy {
	private Field field;
	private boolean asc;

	OrderBy(Field field, boolean asc) {
		this.field = field;
		this.asc = asc;
	}

	public Field getField() {
		return field;
	}

	public boolean isAsc() {
		return asc;
	}

}

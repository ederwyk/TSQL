package br.com.stockinfo.tsql.struct.orderby;

import java.lang.reflect.Field;

public class OrderByField extends OrderBy{
	public OrderByField(Field field, boolean asc) {
		super(field, asc);
	}
}

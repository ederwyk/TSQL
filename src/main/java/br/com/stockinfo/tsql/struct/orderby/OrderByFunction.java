package br.com.stockinfo.tsql.struct.orderby;

import br.com.stockinfo.tsql.struct.projection.SQLFunction;

import java.lang.reflect.Field;

public class OrderByFunction extends OrderBy {
	private SQLFunction function;
	public OrderByFunction(Field field, SQLFunction function, boolean asc) {
		super(field, asc);
		this.function = function;
	}

	public SQLFunction getFunc() {
		return function;
	}

}

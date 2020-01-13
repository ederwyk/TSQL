package br.com.stockinfo.tsql.struct.projection;

import java.lang.reflect.Field;

/**
 * Classe para satisfazer projeções que também sejam uma função, como por exemplo SUM
 */
public class FunctionProjection extends Projection {

	private SQLFunction function;
	public FunctionProjection(SQLFunction function, Field field) {
		super(field);
		this.function = function;
	}

	public SQLFunction getFunction() {
		return function;
	}
}

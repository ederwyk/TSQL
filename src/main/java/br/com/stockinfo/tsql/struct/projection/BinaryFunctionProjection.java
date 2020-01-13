package br.com.stockinfo.tsql.struct.projection;

import java.lang.reflect.Field;

/**
 * Classe que, assim como a function projection, encapsula uma função, só que com dois operadores, exemplo COALESCE
 */
public class BinaryFunctionProjection<T> extends FunctionProjection{
	private T second;
	public BinaryFunctionProjection(SQLFunction function, Field field, T second) {
		super(function, field);
		this.second = second;
	}

	public T getSecond() {
		return second;
	}
}

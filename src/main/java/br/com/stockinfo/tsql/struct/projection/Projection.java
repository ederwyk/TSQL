package br.com.stockinfo.tsql.struct.projection;

import java.lang.reflect.Field;

/**
 * Classe para servir como base de projeções
 */
public abstract class Projection {

	private Field field;
	Projection(Field field) {
		this.field = field;
	}

	public Field getField() {
		return field;
	}
}

package br.com.stockinfo.tsql.fields.tuple;

import java.lang.reflect.Field;

/**
 * Classe auxiliar para encapsular valores e qual campo o valor está relacionado em updates e inserts
 * @param <T> Classe relacionada
 */
public class FieldValue<T> {
	public final T value;
	public final Field field;

	public FieldValue(Field field, T value) {
		this.value = value;
		this.field = field;
	}
}

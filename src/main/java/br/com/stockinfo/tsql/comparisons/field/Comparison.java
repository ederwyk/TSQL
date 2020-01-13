package br.com.stockinfo.tsql.comparisons.field;

import br.com.stockinfo.tsql.interfaces.Modifiyer;
import br.com.stockinfo.tsql.query.Whereable;
import br.com.stockinfo.tsql.Operator;
import br.com.stockinfo.tsql.fields.two.Operator2;
import br.com.stockinfo.tsql.mocking.Recorder;
import br.com.stockinfo.tsql.mocking.RecordingObject;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Function;

/**
 * Classes de comparison são auxiliares para encapsular as cláusulas where. Esta classe é a classe pai das comparações
 * que tem as comparações genéricas para todos os tipos, as classes filhas tem comparações de tipos especificos, como data
 * number e string
 * @param <T> Entidade
 * @param <V> Valor
 * @param <U> Whereable relacionado
 */
public class Comparison<T, V, U extends Whereable<T,U>> {

	protected final Field field;
	protected final U whereable;
	protected final List<Operator2> where;
	protected final Recorder<T> recorder;
	protected final Modifiyer leftModifier;

	public Comparison(Field field, U whereable, List<Operator2> where){
		this(field, whereable, where, null);
	}

	public Comparison(Field field, U whereable, List<Operator2> where, Modifiyer leftModifier){
		this.field = field;
		this.whereable = whereable;
		this.where = where;
		this.recorder = RecordingObject.create(whereable.getClazz());
		this.leftModifier = leftModifier;
	}

	public U eq(V value) {
		where.add(new Operator2(field, value, Operator.eq, leftModifier));
		return whereable;
	}

	public U ne(V value) {
		where.add(new Operator2(field, value, Operator.ne, leftModifier));
		return whereable;
	}

	public U eq(Function<T,V> function) {
		where.add(new Operator2(field, getField(function), Operator.eq, leftModifier));
		return whereable;
	}

	public U ne(Function<T,V> function) {
		where.add(new Operator2(field, getField(function), Operator.ne, leftModifier));
		return whereable;
	}

	public U isNull() {
		where.add(new Operator2(field, "NULL", Operator.is, leftModifier));
		return whereable;
	}

	public U notNull() {
		where.add(new Operator2(field, "NOT NULL", Operator.is, leftModifier));
		return whereable;
	}

	protected Field getField(Function<T,V> function){
		return recorder.getField(function, whereable.getClazz());
	}
}

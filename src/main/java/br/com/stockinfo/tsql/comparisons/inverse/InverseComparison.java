package br.com.stockinfo.tsql.comparisons.inverse;

import br.com.stockinfo.tsql.query.Whereable;
import br.com.stockinfo.tsql.Operator;
import br.com.stockinfo.tsql.fields.two.Operator2;
import br.com.stockinfo.tsql.mocking.Recorder;
import br.com.stockinfo.tsql.mocking.RecordingObject;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Function;

/**
 * Comparação inversa nada mais é que uma comparação que começa com um valor e termina com um atributo da classe
 * @param <T> Entidade
 * @param <V> Valor
 * @param <U> Whereable relacionado
 */
public class InverseComparison<T, V, U extends Whereable<T,U>> {

	protected final V value;
	protected final U whereable;
	protected final List<Operator2> where;
	protected final Recorder<T> recorder;


	public InverseComparison(V value, U whereable, List<Operator2> where){
		this.value = value;
		this.whereable = whereable;
		this.where = where;
		this.recorder = RecordingObject.create(whereable.getClazz());
	}

	public <R> U eq(Function<T,V> function) {
		where.add(new Operator2(value , getField(function), Operator.eq));
		return whereable;
	}

	public U ne(Function<T,V> function) {
		where.add(new Operator2(value, getField(function), Operator.ne));
		return whereable;
	}

	protected Field getField(Function<T,V> function){
		return recorder.getField(function, whereable.getClazz());
	}

}

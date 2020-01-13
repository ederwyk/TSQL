package br.com.stockinfo.tsql.comparisons.field;

import br.com.stockinfo.tsql.Operator;
import br.com.stockinfo.tsql.fields.two.Operator2;
import br.com.stockinfo.tsql.query.Whereable;

import java.lang.reflect.Field;
import java.util.List;

public class NumberComparison<T, U extends Whereable<T,U>> extends Comparison<T, Number, U> {

	public NumberComparison(Field field, U select, List<Operator2> where) {
		super(field, select, where);
	}

	public U gt(Number value) {
		where.add(new Operator2(field, value, Operator.gt));
		return whereable;
	}


	public U lt(Number value) {
		where.add(new Operator2(field, value, Operator.lt));
		return whereable;
	}


	public U ge(Number value) {
		where.add(new Operator2(field, value, Operator.ge));
		return whereable;
	}


	public U le(Number value) {
		where.add(new Operator2(field, value, Operator.le));
		return whereable;
	}
}
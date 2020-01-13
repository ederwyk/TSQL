package br.com.stockinfo.tsql.comparisons.inverse;

import br.com.stockinfo.tsql.fields.two.Operator2;
import br.com.stockinfo.tsql.interfaces.NumberFunction;
import br.com.stockinfo.tsql.query.Whereable;
import br.com.stockinfo.tsql.Operator;

import java.util.List;

public class InverseNumberComparison<T, U extends Whereable<T,U>> extends InverseComparison<T, Number, U>{

	public InverseNumberComparison(Number value, U whereable, List<Operator2> where) {
		super(value, whereable, where);
	}

	public U lt(NumberFunction<T> value) {
		where.add(new Operator2(value, getField(value), Operator.lt));
		return whereable;
	}


	public U ge(NumberFunction<T> value) {
		where.add(new Operator2(value, getField(value), Operator.ge));
		return whereable;
	}


	public U le(NumberFunction<T> value) {
		where.add(new Operator2(value, getField(value), Operator.le));
		return whereable;
	}
}

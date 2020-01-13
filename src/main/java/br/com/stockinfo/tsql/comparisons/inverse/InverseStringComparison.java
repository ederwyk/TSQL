package br.com.stockinfo.tsql.comparisons.inverse;

import br.com.stockinfo.tsql.query.Whereable;
import br.com.stockinfo.tsql.Operator;
import br.com.stockinfo.tsql.fields.two.Operator2;
import br.com.stockinfo.tsql.interfaces.StringFunction;

import java.util.List;

public class InverseStringComparison<T, U extends Whereable<T,U>> extends InverseComparison<T, String, U>{

	public InverseStringComparison(String value, U whereable, List<Operator2> where) {
		super(value, whereable, where);
	}

	public U like(StringFunction<T> function) {
		where.add(new Operator2(value, getField(function), Operator.like));
		return whereable;
	}
}

package br.com.stockinfo.tsql.comparisons.field;

import br.com.stockinfo.tsql.fields.two.Operator2;
import br.com.stockinfo.tsql.interfaces.Modifiyer;
import br.com.stockinfo.tsql.query.Whereable;
import br.com.stockinfo.tsql.Operator;

import java.lang.reflect.Field;
import java.util.List;

public class StringComparison<T, U extends Whereable<T,U>> extends Comparison<T, String, U> {

	public StringComparison(Field field, U select, List<Operator2> where) {
		super(field, select, where);
	}

	public StringComparison(Field field, U select, List<Operator2> where, Modifiyer modifiyer) {
		super(field, select, where, modifiyer);
	}

	public U like(java.lang.String value) {
		where.add(new Operator2(field, value, Operator.like));
		return whereable;
	}
}
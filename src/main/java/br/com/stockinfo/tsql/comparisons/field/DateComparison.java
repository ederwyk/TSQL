package br.com.stockinfo.tsql.comparisons.field;

import br.com.stockinfo.tsql.Operator;
import br.com.stockinfo.tsql.fields.ternary.Operator3;
import br.com.stockinfo.tsql.fields.two.Operator2;
import br.com.stockinfo.tsql.interfaces.DateFunction;
import br.com.stockinfo.tsql.query.Whereable;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

public class DateComparison<T, U extends Whereable<T,U>> extends Comparison<T, Date, U> {

	public DateComparison(Field field, U whereable, List<Operator2> where) {
		super(field, whereable, where);
	}

	public U between(Date dateA, Date dateB){
		where.add(new Operator3(field, dateA, dateB, Operator.between, Operator.and));
		return whereable;
	}

	public U between(DateFunction<T> function, Date date){
		where.add(new Operator3(field, getField(function), date, Operator.between, Operator.and));
		return whereable;
	}
	public U between(Date date, DateFunction<T> function){
		where.add(new Operator3(field, date, getField(function), Operator.between, Operator.and));
		return whereable;
	}
	public U between(DateFunction<T> functionA, DateFunction<T> functionB){
		where.add(new Operator3(field, getField(functionA),getField(functionB), Operator.between, Operator.and));
		return whereable;
	}

	public U gt(Date value) {
		where.add(new Operator2(field, value, Operator.gt));
		return whereable;
	}

	public U lt(Date value) {
		where.add(new Operator2(field, value, Operator.lt));
		return whereable;
	}

	public U ge(Date value) {
		where.add(new Operator2(field, value, Operator.ge));
		return whereable;
	}

	public U le(Date value) {
		where.add(new Operator2(field, value, Operator.le));
		return whereable;
	}

	public U gt(DateFunction<T> function) {
		where.add(new Operator2(field, getField(function), Operator.gt));
		return whereable;
	}

	public U lt(DateFunction<T> function) {
		where.add(new Operator2(field, getField(function), Operator.lt));
		return whereable;
	}

	public U ge(DateFunction<T> function) {
		where.add(new Operator2(field, getField(function), Operator.ge));
		return whereable;
	}

	public U le(DateFunction<T> func) {
		where.add(new Operator2(field, getField(func), Operator.le));
		return whereable;
	}
}

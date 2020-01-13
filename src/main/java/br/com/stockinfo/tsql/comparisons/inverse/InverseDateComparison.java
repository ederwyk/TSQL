package br.com.stockinfo.tsql.comparisons.inverse;

import br.com.stockinfo.tsql.Operator;
import br.com.stockinfo.tsql.fields.ternary.Operator3;
import br.com.stockinfo.tsql.fields.two.Operator2;
import br.com.stockinfo.tsql.query.Whereable;
import br.com.stockinfo.tsql.interfaces.DateFunction;

import java.util.Date;
import java.util.List;

public class InverseDateComparison<T, U extends Whereable<T,U>> extends InverseComparison<T, Date, U>{

	public InverseDateComparison(Date value, U whereable, List<Operator2> where){
		super(value, whereable, where);
	}

	public U between(Date date, DateFunction<T> dateFunction){
		where.add(new Operator3(value, date, getField(dateFunction), Operator.between, Operator.and));
		return whereable;
	}

	public U between(DateFunction<T> dateFunction, Date date){
		where.add(new Operator3(value, getField(dateFunction), date, Operator.between, Operator.and));
		return whereable;
	}

	public U between(DateFunction<T> dateFunctionA, DateFunction<T> dateFunctionB){
		where.add(new Operator3(value, getField(dateFunctionA), getField(dateFunctionB), Operator.between, Operator.and));
		return whereable;
	}

	public U gt(DateFunction<T> function) {
		where.add(new Operator2(value, getField(function), Operator.gt));
		return whereable;
	}

	public U lt(DateFunction<T> function) {
		where.add(new Operator2(value, getField(function), Operator.lt));
		return whereable;
	}

	public U ge(DateFunction<T> function) {
		where.add(new Operator2(value, getField(function), Operator.ge));
		return whereable;
	}

	public U le(DateFunction<T> function) {
		where.add(new Operator2(value, getField(function), Operator.le));
		return whereable;
	}
}

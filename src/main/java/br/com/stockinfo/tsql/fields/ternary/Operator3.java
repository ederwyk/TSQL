package br.com.stockinfo.tsql.fields.ternary;

import br.com.stockinfo.tsql.fields.two.Operator2;
import br.com.stockinfo.tsql.Operator;
import br.com.stockinfo.tsql.interfaces.Modifiyer;

/**
 * Operador de where com 3 entradas, como por exemplo o between: where 1 between 2 and 3
 */
public class Operator3 extends Operator2 {

	public final Operator secondOperator;
	public final Object rightest;
	public final Modifiyer rightestModifier;

	public Operator3(Object left, Object right, Object rightest, Operator operatorA, Operator oparatorB) {
		this(left, right, rightest, operatorA, oparatorB, null, null, null);
	}

	public Operator3(Object left, Object right, Object rightest, Operator operatorA, Operator operatorB, Modifiyer leftModifier, Modifiyer rightModifier, Modifiyer rightestModifier) {
		super(left, right, operatorA, leftModifier, rightModifier);
		this.secondOperator = operatorB;
		this.rightest = rightest;
		this.rightestModifier = rightestModifier;
	}

	public Object getRightest(){
		return rightest;
	}

	public Operator getSecondOp(){
		return secondOperator;
	}


}

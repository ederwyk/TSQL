package br.com.stockinfo.tsql.fields.two;

import br.com.stockinfo.tsql.Operator;
import br.com.stockinfo.tsql.interfaces.Modifiyer;

/**
 * Operator de duas entradas, um where normal, como por exemplo where 1 = 2
 */
public class Operator2 {

	public final Object left;
	public final Modifiyer leftModifiyer;
	public final Object right;
	public final Operator operator;
	public final Modifiyer rightModifier;

	public Operator2(Object left, Object right, Operator operator, Modifiyer leftModifiyer, Modifiyer rightModifier){
		this.left = left;
		this.right = right;
		this.operator = operator;
		this.leftModifiyer = leftModifiyer;
		this.rightModifier = rightModifier;
	}

	public Operator2(Object left, Object right, Operator operator, Modifiyer leftModifiyer){
		this(left, right, operator, leftModifiyer, null);
	}

	public Operator2(Object left, Object right, Operator operator){
		this(left, right, operator, null, null);
	}

	public Object getLeft() {
		return left;
	}

	public Object getRight(){
		return right;
	}

	public Operator getOperator(){
		return operator;
	}
}

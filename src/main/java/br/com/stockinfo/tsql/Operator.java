package br.com.stockinfo.tsql;

public enum Operator {
	gt, eq, ne, ge, lt, le, like, is, between, and, or;

	@Override
	public String toString() {
		switch (this){
			case eq:
				return "=";
			case ge:
				return ">=";
			case gt:
				return ">";
			case is:
				return "IS";
			case le:
				return "<=";
			case lt:
				return "<";
			case ne:
				return "!=";
			case like:
				return "LIKE";
			case between:
				return "BETWEEN";
			case and:
				return "AND";
			case or:
				return "OR";
			default:
				return null;
		}
	}
}

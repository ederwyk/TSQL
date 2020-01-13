package br.com.stockinfo.tsql.query;

import br.com.stockinfo.tsql.comparisons.field.DateComparison;
import br.com.stockinfo.tsql.comparisons.field.NumberComparison;
import br.com.stockinfo.tsql.comparisons.field.StringComparison;
import br.com.stockinfo.tsql.comparisons.inverse.InverseDateComparison;
import br.com.stockinfo.tsql.comparisons.inverse.InverseNumberComparison;
import br.com.stockinfo.tsql.comparisons.inverse.InverseStringComparison;
import br.com.stockinfo.tsql.fields.ternary.Operator3;
import br.com.stockinfo.tsql.fields.two.Operator2;
import br.com.stockinfo.tsql.interfaces.*;
import br.com.stockinfo.tsql.util.Util;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Whereable<T,U extends Whereable<T,U>> extends Query<T> {

	protected List<Operator2> where = new ArrayList<>();
	protected HashMap<Integer, Brackets> brackets = new HashMap<>();
	protected HashMap<Integer, Boolean> isAnd = new HashMap<>();

	public Whereable(Class<T> clazz) {
		super(clazz);
	}

	public Whereable(Class<T> clazz, Whereable<?, ?> whereable) {
		super(clazz);
		this.where = whereable.where;
		this.brackets = whereable.brackets;
		this.isAnd = whereable.isAnd;
	}

	String whereSql(String dataSource) {
		verifyFirstOr();
		verifyBracketCount();
		String where = this.where.stream().map(w-> {
			int index = this.where.indexOf(w);

			String sql = checkStartingBrackets(index);
			if (w instanceof Operator3) {
				Operator3 operator3 = (Operator3) w;
				sql += Util.getCoiso(operator3.getLeft(), dataSource, operator3.leftModifiyer) + " " + operator3.getOperator() + " " + Util.getCoiso(operator3.getRight(),dataSource, operator3.rightModifier) + " " + operator3.getSecondOp() + " " + Util.getCoiso(operator3.getRightest(), dataSource, operator3.rightestModifier);
			}
			else
				sql += Util.getCoiso(w.getLeft(), dataSource, w.leftModifiyer) + " " + w.getOperator() + " " + Util.getCoiso(w.getRight(), dataSource);

			sql += checkEndingBrackets(index);

			if (index == this.where.size() - 1)
				return sql;

			if (isAnd.get(index + 1))
				return sql + " AND ";
			else
				return sql + " OR ";

		}).collect(Collectors.joining());
		return where.isEmpty() ? "" : " WHERE " + where;
	}

	private void verifyFirstOr() {
		if (where.size() > 0 && !isAnd.get(0))
			throw new RuntimeException("Erro, SQL começa com OR");
	}

	private void verifyBracketCount(){
		int offset = 0;

		for (Map.Entry<Integer,Brackets> entry : brackets.entrySet()){
			if (entry.getValue().open){
				offset+= entry.getValue().quantity;
			}
			if (!entry.getValue().open){
				offset-= entry.getValue().quantity;
			}
			if (offset < 0)
				throw new RuntimeException("Erro, verifique open() e close() da consulta");
		}
		if (offset != 0)
			throw new RuntimeException("Erro, verifique open() e close() da consulta");
	}

	private void addBrackets(boolean open){
		Integer index = where.size();
		if (open)
			if (brackets.containsKey(index)){
				if (!brackets.get(index).open)
					throw new RuntimeException("Erro, tentando adicionar ( logo após )");
				brackets.get(index).quantity++;
			} else
				brackets.put(index, new Brackets(true));
		else{
			if (brackets.containsKey(index)){
				if (brackets.get(index).open)
					throw new RuntimeException("Erro, tentando adicionar ) logo após (");

				brackets.get(index).quantity++;
			} else
				brackets.put(index,new Brackets(false) );
		}
	}

	private String checkStartingBrackets(Integer index){
		if (brackets.containsKey(index)) {
			if (brackets.get(index).open){
				return StringUtils.repeat("(",brackets.get(index).quantity);
			}
		}
		return "";
	}

	private String checkEndingBrackets(Integer index){
		if (brackets.containsKey(index+1)){
			if (!brackets.get(index+1).open)
				return StringUtils.repeat(")",brackets.get(index+1).quantity);
		}
		return "";
	}

	public U open() {
		addBrackets(true);
		return (U)this;
	}

	public U close() {
		addBrackets(false);
		return (U)this;
	}

	public StringComparison<T, U> or(StringFunction<T> getter) {
		isAnd.put(where.size() , false);
		return new StringComparison<>(recorder.getField(getter, clazz), (U) this, where);
	}

	public DateComparison<T, U> or(DateFunction<T> getter) {
		isAnd.put(where.size() , false);
		return new DateComparison<>(recorder.getField(getter, clazz),(U)this,where);
	}

	public NumberComparison<T, U> or(NumberFunction<T> getter) {
		isAnd.put(where.size() , false);
		return new NumberComparison<>(recorder.getField(getter, clazz),(U)this,where);
	}

	public StringComparison<T, U> where(StringFunction<T> getter) {
		isAnd.put(where.size(), true);
		return new StringComparison<>(recorder.getField(getter, clazz), (U) this, where);
	}

	public StringComparison<T, U> where(StringFunction<T> getter, Modifiyer modifiyer) {
		isAnd.put(where.size(), true);
		return new StringComparison<>(recorder.getField(getter, clazz), (U) this, where, modifiyer);
	}

	public InverseDateComparison<T, U> where(Date date) {
		isAnd.put(where.size(), true);
		return new InverseDateComparison<>(date, (U)this, where);
	}

	public DateComparison<T, U> where(DateFunction<T> getter) {
		isAnd.put(where.size(), true);
		return new DateComparison<>(recorder.getField(getter, clazz),(U)this,where);
	}

	public NumberComparison<T, U> where(NumberFunction<T> getter) {
		isAnd.put(where.size(), true);
		return new NumberComparison<>(recorder.getField(getter, clazz),(U)this,where);
	}

	public InverseStringComparison<T, U> where(String batata) {
		isAnd.put(where.size(), true);
		return new InverseStringComparison<>(batata, (U)this,where);
	}

	public InverseNumberComparison<T, U> where(Number ADWAD){
		isAnd.put(where.size(), true);
		return new InverseNumberComparison<>(ADWAD, (U)this,where);
	}

}

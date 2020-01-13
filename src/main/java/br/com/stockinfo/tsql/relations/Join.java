package br.com.stockinfo.tsql.relations;

import br.com.stockinfo.tsql.interfaces.JoinType;
import br.com.stockinfo.tsql.query.Select;
import br.com.stockinfo.tsql.mocking.Recorder;
import br.com.stockinfo.tsql.mocking.RecordingObject;
import br.com.stockinfo.tsql.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Join<T,U> {

	private final Select<T> select;
	private final Class<T> a;
	private final Class<U> b;

	private final Recorder<T> aRecorder;
	private final Recorder<U> bRecorder;

	private final JoinType joinType;

	private List<On> ons = new ArrayList<>();

	public Join(Class<T> a, Class<U> b, Select<T> select, JoinType joinType) {
		this.select = select;
		this.a = a;
		this.b = b;
		this.joinType = joinType;
		aRecorder = RecordingObject.create(a);
		bRecorder = RecordingObject.create(b);
	}

	private <X,W> void addOn(Function<T,X> aFunc, Function<U,W> bFunc) {
		aFunc.apply(aRecorder.getObject());
		bFunc.apply(bRecorder.getObject());

		On on = new On();
		on.a = Util.getField(aRecorder.getCurrentPropertyName(), a);
		on.b = Util.getField(bRecorder.getCurrentPropertyName(), b);
		ons.add(on);
	}

	public Class<T> getA() {
		return a;
	}

	public <X,W> Select<U> on(Function<T,X> aFunc, Function<U,W> bFunc){
		addOn(aFunc,bFunc );
		return select.using(b);
	}
	public <X,W> Join<T,U> onAnd(Function<T,X> aFunc, Function<U,W> bFunc){
		addOn(aFunc,bFunc );
		return this;
	}

	public JoinType getJoinType(){
		return joinType;
	}

	public List<On> getOns(){
		return ons;
	}

	public Class<U> getB() {
		return b;
	}
}

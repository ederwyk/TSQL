package br.com.stockinfo.tsql.interfaces;

import br.com.stockinfo.tsql.mocking.Recorder;
import br.com.stockinfo.tsql.mocking.RecordingObject;

public abstract class Query<T> {

	protected final Class<T> clazz;
	protected final Recorder<T> recorder;

	public Query(Class<T> clazz) {
		this.clazz = clazz;
		this.recorder =  RecordingObject.create(clazz);
	}

	public Class<T> getClazz(){
		return clazz;
	}
}

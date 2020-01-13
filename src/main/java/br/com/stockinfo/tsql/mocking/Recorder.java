package br.com.stockinfo.tsql.mocking;


import br.com.stockinfo.tsql.util.Util;

import java.lang.reflect.Field;
import java.util.function.Function;

public class Recorder<T> {

    private T t;
    private RecordingObject recorder;

    public Recorder(T t, RecordingObject recorder) {
        this.t = t;
        this.recorder = recorder;
    }

    public String getCurrentPropertyName() {
        return recorder.getCurrentPropertyName();
    }

    public T getObject() {
        return t;
    }

	public <R> Field getField(Function<T, R> getter, Class<T> clazz) {
        getter.apply(t);
        return Util.getField(getCurrentPropertyName(), clazz);
	}

}
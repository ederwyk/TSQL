package br.com.stockinfo.tsql.mocking;


import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import br.com.stockinfo.tsql.util.Configurations;

import java.lang.reflect.Method;

public class RecordingObject implements MethodInterceptor {

    private String currentPropertyName = "";
    private Recorder<?> currentMock = null;

    @SuppressWarnings("unchecked")
    public static <T> Recorder<T> create(Class<T> clazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        final RecordingObject recordingObject = new RecordingObject();

        enhancer.setCallback(recordingObject);
        return new Recorder<T>((T) enhancer.create(), recordingObject);
    }

    public Object intercept(Object o, Method method, Object[] os, MethodProxy methodProxy) throws Throwable {
        if (method.getName().equals("getCurrentPropertyName")) {
            return getCurrentPropertyName();
        }
        currentPropertyName = Configurations.conventions.toFieldName(method.getName());
        return DefaultValues.getDefault(method.getReturnType());
    }

    public String getCurrentPropertyName() {
        return currentPropertyName + (currentMock == null ? "" : ("." + currentMock.getCurrentPropertyName()));
    }
}
package br.com.stockinfo.tsql.query;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import br.com.stockinfo.tsql.fields.tuple.FieldValue;
import br.com.stockinfo.tsql.util.Configurations;
import br.com.stockinfo.tsql.util.Util;

public class Update<T> extends Whereable<T, Update<T>> {

	private final T object;
	private List<FieldValue> fieldValues = new ArrayList<>();
	private boolean allowNull = false;
	private boolean allowEmpty = true;

	public Update(T object) {
		super((Class<T>) object.getClass());
		this.object = object;
	}

	public static <T> Update<T> entity(T clazz){
		return new Update<>(clazz);
	}

	public Update<T> allowNull(){
		allowNull = true;
		return this;
	}

	public Update<T> rejectEmpty(){
		allowEmpty = false;
		return this;
	}

	public <U> Update<T> set(Function<T,U> function){
		function.apply(recorder.getObject());
		String fieldName = recorder.getCurrentPropertyName();

		U value = function.apply(object);
		Field field = Util.getField(fieldName, clazz);
		fieldValues.add(new FieldValue<>(field, value));
		return this;
	}

	public String toSql(String dataSource){
		return "UPDATE " + Util.getTable(clazz,dataSource ) + " SET " + columnsSql(dataSource) + whereSql(dataSource);
	}

	private String columnsSql(String dataSource) {
		if (fieldValues.size() == 0){
			fieldValues = Util.getAllSetableFields(object, allowNull, allowEmpty, dataSource);
		}

		return fieldValues.stream().map(fieldValue-> Util.getField(fieldValue.field, dataSource) +" = " + Configurations.conventions.getText(fieldValue.value)).collect(Collectors.joining(", "));
	}
}
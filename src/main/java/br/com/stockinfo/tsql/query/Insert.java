package br.com.stockinfo.tsql.query;

import br.com.stockinfo.tsql.fields.tuple.FieldValue;
import br.com.stockinfo.tsql.interfaces.Query;
import br.com.stockinfo.tsql.util.Configurations;
import br.com.stockinfo.tsql.util.Util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Insert<T> extends Query<T> {

	private final T object;
	private List<FieldValue> fieldValues = new ArrayList<>();
	private boolean allowNull = false;
	private boolean allowEmpty = true;

	public Insert(T object) {
		super((Class<T>) object.getClass());
		this.object = object;
	}

	public static <T> Insert<T> entity(T clazz){
		return new Insert<>(clazz);
	}

	public Insert<T> allowNull(){
		allowNull = true;
		return this;
	}

	public Insert<T> rejectEmpty(){
		allowEmpty = true;
		return this;
	}

	public <U> Insert<T> set(Function<T,U> function){
		function.apply(recorder.getObject());
		String fieldName = recorder.getCurrentPropertyName();

		U value = function.apply(object);
		Field field = Util.getField(fieldName, clazz);
		fieldValues.add(new FieldValue<>(field, value));
		return this;
	}

	public String toSql(String dataSource){
		return "INSERT INTO " + Util.getTable(clazz, dataSource) + " " +  columnsSql(dataSource) + " " + valuesSql(dataSource);
	}

	private String valuesSql(String dataSource) {
		return "VALUES (" + fieldValues.stream().map(fieldValue->Configurations.conventions.getText(fieldValue.value)).collect(Collectors.joining(", ")) + ")";
	}

	private String columnsSql(String dataSource) {
		if (fieldValues.size() == 0){
			fieldValues = Util.getAllSetableFields(object, allowNull, allowEmpty, dataSource);
		}

		return "(" + fieldValues.stream().map(fieldValue-> Util.getField(fieldValue.field, dataSource)).collect(Collectors.joining(", ")) + ")";
	}

}

package br.com.stockinfo.tsql.util;

import br.com.stockinfo.core.util.Formatacao;
import com.google.common.collect.ImmutableMap;
import br.com.stockinfo.tsql.results.JdbcSetter;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * Basicamente um Dialect do Filemaker pro TSQL
 */
public class Conventions {

    private final Map<Class<?>, String> dbTypes = ImmutableMap.<Class<?>,String>builder()
        .put(String.class, "text")
        .put(Integer.class, "integer")
        .put(Float.class, "double")
        .put(Double.class, "double")
        .put(Long.class, "bigint")
        .put(Character.class, "integer")
        .put(Byte.class, "integer")
        .put(int.class, "integer")
        .put(float.class, "double")
        .put(double.class, "double")
        .put(long.class, "bigint")
        .put(char.class, "integer")
        .put(byte.class, "integer")
        .build();

    private final Map<Class<?>, JDBCType> jdbcTypes = ImmutableMap.<Class<?>,JDBCType>builder()
        .put(String.class, JDBCType.VARCHAR)
        .put(Integer.class, JDBCType.INTEGER)
        .put(Float.class, JDBCType.FLOAT)
        .put(Double.class, JDBCType.DOUBLE)
        .put(Long.class, JDBCType.BIGINT)
        .put(Character.class, JDBCType.CHAR)
        .put(Byte.class, JDBCType.TINYINT)
        .put(int.class, JDBCType.INTEGER)
        .put(float.class, JDBCType.FLOAT)
        .put(double.class, JDBCType.DOUBLE)
        .put(long.class, JDBCType.BIGINT)
        .put(char.class, JDBCType.CHAR)
        .put(byte.class, JDBCType.TINYINT)
        .build();

	public <T> String getText(T value) {
		if (value instanceof Timestamp)
			return getText((Timestamp) value);
		if (value instanceof String)
			return getText((String) value);
		if (value instanceof Number)
			return getText((Number) value);
		if (value instanceof Date)
			return getText((Date) value);
		if (value == null)
			return "NULL";

		throw new RuntimeException("Erro, tipo inesperado");
	}

    public String getText(Number value){
        return value.toString();
    }

    public String getText(String value){
		if (value.equalsIgnoreCase("NULL") || value.equalsIgnoreCase("NOT NULL"))
			return value;

		value = value.replaceAll("'", "''");
		value = value.trim();
        return value.isEmpty() ? "NULL" : "'" + value +"'";
    }

    private String getText(Date value){
		return "DATE'"+ Formatacao.formatarDataBD11(value)+"'";
	}

	private String getText(Timestamp value){
		return "TIMESTAMP'"+ Formatacao.formatarDataHoraBD11(value)+"'";
	}


    private final Map<Class<?>, JdbcSetter> jdbcSetters = ImmutableMap.<Class<?>,JdbcSetter>builder()
        .put(String.class, (JdbcSetter<String>)PreparedStatement::setString)
        .put(Integer.class, (JdbcSetter<Integer>)PreparedStatement::setInt)
        .put(Float.class, (JdbcSetter<Float>)PreparedStatement::setFloat)
        .put(Double.class, (JdbcSetter<Double>) PreparedStatement::setDouble)
        .put(Long.class, (JdbcSetter<Long>)PreparedStatement::setLong)
        .put(Character.class, (JdbcSetter<Integer>)PreparedStatement::setInt)
        .put(Byte.class, (JdbcSetter<Integer>)PreparedStatement::setInt)
        .put(int.class, (JdbcSetter<Integer>)PreparedStatement::setInt)
        .put(float.class, (JdbcSetter<Float>)PreparedStatement::setFloat)
        .put(double.class, (JdbcSetter<Double>)PreparedStatement::setDouble)
        .put(long.class, (JdbcSetter<Long>)PreparedStatement::setLong)
        .put(char.class, (JdbcSetter<Integer>)PreparedStatement::setInt)
        .put(byte.class, (JdbcSetter<Integer>)PreparedStatement::setInt)
        .build();



    public String toDbName(String name) {
        return uncapitalize(toSnakeCase(banishGetterSetters(name)));
    }

    public String toFieldName(String methodName){
		return uncapitalize(banishGetterSetters(methodName));
	}

    public String toDbName(Class<?>... clazzs) {
        return Arrays.stream(clazzs)
            .map(Class::getSimpleName)
            .map(this::toDbName)
            .reduce((a,b) -> a + "_" + b)
            .orElse("");
    }

    public String toJoinTableName(Class<?> clazz, String name) {
        return toDbName(clazz.getSimpleName()) + "_" + toDbName(name);
    }

    private String banishGetterSetters(String name) {
        return name.replaceAll("^(get|set)", "");
    }

    public String uncapitalize(String s) {
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    public String toSnakeCase(String s) {
        return s.replaceAll("([a-z]|[1-9])([1-9]|[A-Z])","$1_$2").toLowerCase();
    }

    public <T> String toDbType(Class<T> clazz) {
        return dbTypes.getOrDefault(clazz, "text");
    }

    public <T> JdbcSetter<T> getSetter(Class<T> clazz) {
        return jdbcSetters.get(clazz);
    }
}

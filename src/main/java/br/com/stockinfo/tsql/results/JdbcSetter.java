package br.com.stockinfo.tsql.results;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface JdbcSetter<T> {
	public void apply(PreparedStatement stmt, int index, T t) throws SQLException;
}
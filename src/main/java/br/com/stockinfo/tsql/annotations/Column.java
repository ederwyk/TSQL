package br.com.stockinfo.tsql.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Columns.class)
public @interface Column {
	String name() default "";
	boolean snake() default false;
	String[] dataSource();
	boolean identity() default false;
	boolean readOnly() default false;
}

package br.com.stockinfo.tsql.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Tables.class)
public @interface Table {
	String name() default "";
	String[] dataSource();
}

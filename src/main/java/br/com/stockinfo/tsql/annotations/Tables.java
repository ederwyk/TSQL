package br.com.stockinfo.tsql.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface Tables {
	Table[] value();
}

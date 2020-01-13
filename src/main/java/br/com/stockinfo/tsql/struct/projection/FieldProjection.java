package br.com.stockinfo.tsql.struct.projection;

import java.lang.reflect.Field;

public class FieldProjection extends Projection {
	public FieldProjection(Field field){
		super(field);
	}
}

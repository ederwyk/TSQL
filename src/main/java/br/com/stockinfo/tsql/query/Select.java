package br.com.stockinfo.tsql.query;

import br.com.stockinfo.tsql.Operator;
import br.com.stockinfo.tsql.annotations.Column;
import br.com.stockinfo.tsql.annotations.Columns;
import br.com.stockinfo.tsql.fields.ternary.Operator3;
import br.com.stockinfo.tsql.fields.two.Operator2;
import br.com.stockinfo.tsql.interfaces.Brackets;
import br.com.stockinfo.tsql.interfaces.JoinType;
import br.com.stockinfo.tsql.relations.On;
import br.com.stockinfo.tsql.struct.projection.*;
import br.com.stockinfo.tsql.relations.Join;
import br.com.stockinfo.tsql.struct.orderby.OrderBy;
import br.com.stockinfo.tsql.struct.orderby.OrderByField;
import br.com.stockinfo.tsql.struct.orderby.OrderByFunction;
import br.com.stockinfo.tsql.util.Util;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static br.com.stockinfo.core.JsonHelper.*;

public class Select<T> extends Whereable<T, Select<T>> {

	private List<Projection> projections = new ArrayList<>();
	private List<Join<?,?>> joins = new ArrayList<>();
	private List<OrderBy> orderBy = new ArrayList<>();
	private List<FieldProjection> groupBy = new ArrayList<>();

	private Select (Class<T> cls){
		super(cls);
	}

	/**
	 * Iniciailiza o select a partir de um outro select, por exemplo ao usar o método using
	 */
	private <U> Select(Class<T> clazz, Select<U> select) {
		super(clazz, select);

		this.projections.addAll(select.projections);
		this.joins.addAll(select.joins);
		this.orderBy.addAll(select.orderBy);
		this.groupBy.addAll(select.groupBy);
	}

	public static <T> Select<T> from(Class<T> clazz){
		return new Select<>(clazz);
	}

	public <U> Join<T,U> join(Class<U> cls){
		return j(cls, JoinType.INNER);
	}

	public <U> Join<T,U> leftJoin(Class<U> clazz){
		return j(clazz, JoinType.LEFT);
	}

	public <U> Join<T,U> rightJoin(Class<U> clazz){
		return j(clazz, JoinType.RIGHT);
	}

	public <U> Join<T,U> outerJoin(Class<U> clazz){
		return j(clazz, JoinType.OUTER);
	}

	private  <U> Join<T,U> j(Class<U> clazz, JoinType joinType){
		Join<T,U> join = new Join<>(this.clazz, clazz, this, joinType);
		joins.add(join);
		return join;
	}

	public <U> Select<U> using(Class<U> clazz){
		return new Select<>(clazz, this);
	}

	public <U> Select<T> coalesce(Function<T, U> function, U value){
		function.apply(recorder.getObject());
		projections.add(new BinaryFunctionProjection<>(SQLFunction.COALESCE, Util.getField(recorder.getCurrentPropertyName(), clazz), value));
		return this;
	}

	public <U> Select<T> project(Function<T,U> function){
		function.apply(recorder.getObject());
		projections.add(new FieldProjection(Util.getField(recorder.getCurrentPropertyName(), clazz)));
		return this;
	}

	public <U> Select<T> project(Function<T,U> function, SQLFunction sqlFunction){
		function.apply(recorder.getObject());
		projections.add(new FunctionProjection(sqlFunction, Util.getField(recorder.getCurrentPropertyName(), clazz)));
		return this;
	}

	public <U> Select<T> orderBy(Function<T,U> function){
		function.apply(recorder.getObject());
		orderBy.add(new OrderByField(Util.getField(recorder.getCurrentPropertyName(), clazz), true));
		return this;
	}

	public <U> Select<T> orderByDesc(Function<T,U> function){
		function.apply(recorder.getObject());
		orderBy.add(new OrderByField(Util.getField(recorder.getCurrentPropertyName(), clazz), false));
		return this;
	}

	public <U> Select<T> orderBy(Function<T,U> function, SQLFunction sqlFunction){
		function.apply(recorder.getObject());
		orderBy.add(new OrderByFunction(Util.getField(recorder.getCurrentPropertyName(), clazz), sqlFunction, true));
		return this;
	}

	public <U> Select<T> orderByDesc(Function<T,U> function, SQLFunction sqlFunction){
		function.apply(recorder.getObject());
		orderBy.add(new OrderByFunction(Util.getField(recorder.getCurrentPropertyName(), clazz), sqlFunction, false));
		return this;
	}

	public <U> Select<T> groupBy(Function<T,U> function){
		function.apply(recorder.getObject());
		groupBy.add(new FieldProjection(Util.getField(recorder.getCurrentPropertyName(), clazz)));
		return this;
	}

	private JsonObject fieldStuff(Field field){
		JsonObject left = new JsonObject();
		left.addProperty("type", "field");
		JsonObject fieldValue = new JsonObject();
		fieldValue.addProperty("entity", field.getDeclaringClass().getSimpleName());
		fieldValue.addProperty("field", field.getName());
		left.add("value", fieldValue);
		return left;
	}

	private JsonObject valueStuff(Object object){
		JsonObject left = new JsonObject();
		left.addProperty("type", "value");
		JsonObject valueValue = new JsonObject();
		valueValue.addProperty("type", object.getClass().getSimpleName());
		valueValue.addProperty("value", getString(object));
		left.add("value", valueValue);
		return left;
	}

	private JsonObject fieldEvaluation(Object object){
		JsonObject jsonObject;
		if (object instanceof Field)
		{
			jsonObject = fieldStuff((Field) object);
		}
		else{
			jsonObject = valueStuff(object);
		}
		return jsonObject;
	}

	private static Object getObject(String type, String source) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (type.equalsIgnoreCase("date"))
			return sdf.parse(source);
		if (type.equalsIgnoreCase("integer"))
			return Integer.parseInt(source);
		if (type.equalsIgnoreCase("bigDecimal"))
			return new BigDecimal(source);
		if (type.equalsIgnoreCase("double"))
			return Double.parseDouble(source);
	
		return source;
	}

	public static Select fromJson(JsonObject jsonObject, Class<?> clazz, String packageName) throws ParseException {
		return Select.fromJson(jsonObject, clazz, (className, fieldName) -> {
			try {
				Class<?> aClass = Class.forName(packageName + "." + className);
				return aClass.getDeclaredField(fieldName);
			} catch (ClassNotFoundException | NoSuchFieldException e) {
				e.printStackTrace();
				return null;
			}
		}, (className) -> {
			try {
				return Class.forName(packageName + "." + className);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		});
	}

	/** Criação da entidade a partir de um JSON */
	private static <U> Select<U> fromJson(JsonObject object, Class<U> clazz, BiFunction<String, String, Field> parseField, Function<String,Class> parseClass) throws ParseException {
		Select<U> select = new Select<>(clazz);

		for (JsonObject element : joList(object, "restrictions")){
			Object left = null;
			if (json(element, "left.type").equals("field")){
				left = parseField.apply(json(element, "left.value.entity"), json(element,"left.value.field"));
			}
			if (json(element, "left.type").equals("value")){
				left = getObject(json(element, "left.value.type"), json(element, "left.value.value"));
			}

			Object right = null;
			if (json(element, "right.type").equals("field")){
				right = parseField.apply(json(element, "right.value.entity"), json(element,"right.value.field"));
			}
			if (json(element, "right.type").equals("value")){
				right = getObject(json(element, "right.value.type"), json(element, "right.value.value"));
			}

			Object rightest = null;
			Operator operator;
			Operator secondOperator = null;
			if (jsonObject(element, "rightest") != null) {

				if (json(element, "rightest.type").equals("field")){
					rightest = parseField.apply(json(element, "rightest.value.entity"), json(element,"rightest.value.field"));
				}
				if (json(element, "rightest.type").equals("value")){
					rightest = getObject(json(element, "rightest.value.type"), json(element, "rightest.value.value"));
				}

				secondOperator = Operator.valueOf(json(element, "secondOperator"));
			}
			operator = Operator.valueOf(json(element, "operator"));

			Operator2 fullOperator;
			if (rightest == null){
				fullOperator = new Operator2(left,right,operator);
			}
			else{
				fullOperator = new Operator3(left,right,rightest,operator,secondOperator);
			}
			select.where.add(fullOperator);
		}

		for(JsonObject join : joList(object,"joins")){
			Class<?> a = parseClass.apply(json(join, "a"));
			Class<?> b = parseClass.apply(json(join, "b"));

			@SuppressWarnings("unchecked")
			Join innerJoins = new Join(a, b, select, JoinType.INNER);
			for (JsonObject on : joList(join,"ons")){
				On one = new On();
				one.a = parseField.apply(a.getSimpleName(),json(on,"a"));
				one.b = parseField.apply(b.getSimpleName(),json(on,"b"));
				innerJoins.getOns().add(one);
			}
			select.joins.add(innerJoins);
		}

		for(JsonObject projection : joList(object, "projections")){
			Projection p = null;
			if (json(projection, "func") != null){
				p = new FunctionProjection(SQLFunction.valueOf(json(projection, "func")), parseField.apply(json(projection, "entity"), json(projection, "field")));
			}
			else if (json(projection, "second") != null){
//				p = new BinaryFunctionProjection(parseField.apply(json(projection, "entity"), json(projection, "field"), j))
			}else{
				p = new FieldProjection(parseField.apply(json(projection, "entity"), json(projection, "field")));
			}
			select.projections.add(p);
		}

		for(JsonObject groupBy: joList(object, "groupBy")){
			FieldProjection group = new FieldProjection(parseField.apply(json(groupBy, "entity"), json(groupBy, "field")));
			select.groupBy.add(group);
		}

		for(JsonObject orderBy: joList(object, "orderBy")){
			OrderBy order;
			if(json(orderBy, "operator") != null){
				order = new OrderByFunction(parseField.apply(json(orderBy, "entity"), json(orderBy, "field")), SQLFunction.valueOf(json(orderBy, "operator")), !jsonBool(orderBy, "desc"));
			}else{
				order = new OrderByField(parseField.apply(json(orderBy, "entity"), json(orderBy, "field")), !jsonBool(orderBy, "desc"));
			}
			select.orderBy.add(order);
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Type empMapType = new TypeToken<Map<Integer, Brackets>>() {}.getType();
		Type booleanMap = new TypeToken<Map<Integer, Boolean>>() {}.getType();

		select.brackets = gson.fromJson(jsonObject(object,"brackets"), empMapType);
		select.isAnd = gson.fromJson(jsonObject(object,"isAnd"), booleanMap);

		return select;
	}

	/** Transformação em json a partir de um objeto Select */
	public String toJson(){
		JsonObject whereable = new JsonObject();
		JsonArray restrictions = new JsonArray();
		for (Operator2 fo : where) {
			JsonObject jo = new JsonObject();
			jo.addProperty("operator", fo.operator.name());

			if (fo instanceof Operator3){
				jo.add("left", fieldEvaluation(fo.left));
				jo.add("right", fieldEvaluation(fo.right));
				jo.add("rightest", fieldEvaluation(((Operator3) fo).rightest));
				jo.addProperty("secondOp", ((Operator3) fo).secondOperator.name());
			}
			else {
				jo.add("left", fieldEvaluation(fo.left));
				jo.add("right", fieldEvaluation(fo.right));
			}
			restrictions.add(jo);
		}
		whereable.add("restrictions",restrictions);

		JsonArray joins = new JsonArray();
		for (Join<?,?> join : this.joins){
			JsonObject a = new JsonObject();
			a.addProperty("a", join.getA().getSimpleName());
			a.addProperty("b", join.getB().getSimpleName());
			JsonArray array = new JsonArray();
			for (On on : join.getOns()){
				JsonObject one = new JsonObject();
				one.addProperty("a", on.a.getName());
				one.addProperty("b", on.b.getName());
				array.add(one);
			}
			a.add("ons", array);
			joins.add(a);
		}
		whereable.add("joins", joins);

		JsonArray projections = new JsonArray();
		for (Projection p : this.projections){
			JsonObject proj = new JsonObject();

			proj.addProperty("entity", p.getField().getDeclaringClass().getSimpleName());
			proj.addProperty("field", p.getField().getName());

			if (p instanceof FunctionProjection){
				proj.addProperty("func", ((FunctionProjection) p).getFunction().name());
			}

			if (p instanceof BinaryFunctionProjection){
				proj.addProperty("func", ((BinaryFunctionProjection<?>) p).getFunction().name());
				proj.addProperty("second", getString(((BinaryFunctionProjection<?>) p).getSecond()));
			}
			projections.add(proj);
		}
		whereable.add("projections", projections);

		JsonArray groupBy = new JsonArray();
		for (FieldProjection fieldProjection : this.groupBy){
			JsonObject jo = new JsonObject();
			jo.addProperty("entity", fieldProjection.getField().getDeclaringClass().getSimpleName());
			jo.addProperty("field", fieldProjection.getField().getName());
			groupBy.add(jo);
		}
		whereable.add("groupBy", groupBy);

		JsonArray orderBy = new JsonArray();
		for (OrderBy orders : this.orderBy){
			JsonObject jo = new JsonObject();
			jo.addProperty("entity", orders.getField().getDeclaringClass().getSimpleName());
			jo.addProperty("field", orders.getField().getName());
			if (orders instanceof OrderByFunction){
				jo.addProperty("operator", ((OrderByFunction) orders).getFunc().name());
			}
			jo.addProperty("desc", !orders.isAsc());

			orderBy.add(jo);
		}
		whereable.add("orderBy", orderBy);



		Gson gson = new GsonBuilder().setPrettyPrinting().create();


		whereable.add("brackets", gson.toJsonTree(this.brackets));
		whereable.add("isAnd", gson.toJsonTree(this.isAnd));

		return gson.toJson(whereable);
	}

	private String getString(Object object){
		if (object instanceof Date) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			return sdf.format((Date) object);
		}
		else {
			return String.valueOf(object);
		}
	}

	public String toSql(){
		return toSql("");
	}

	public String toSql(String dataSource){
		return "SELECT " + projectionsSql(dataSource) + " FROM " + fromSql(dataSource) + whereSql(dataSource) + groupBySql(dataSource) + orderBySql(dataSource);
	}

	private String groupBySql(String dataSource) {
		String groupBy = this.groupBy.stream().map(g-> Util.getAlias(g.getField(), dataSource)).collect(Collectors.joining(", "));
		return this.groupBy.isEmpty() ? "" : " GROUP BY " + groupBy;
	}

	private String orderBySql(String dataSource) {
		String orderBy = this.orderBy.stream().map(order->{
			Field field = order.getField();
			if (order instanceof OrderByFunction)
				return ((OrderByFunction) order).getFunc() + "(" + Util.getAlias(field, dataSource)+ ")" + (order.isAsc() ? "" : " DESC");
			return Util.getAlias(field, dataSource) + (order.isAsc() ? "" : " DESC");
		}).collect(Collectors.joining(", "));

		return this.orderBy.isEmpty() ? "" : " ORDER BY " + orderBy;
	}

	private String projectionsSql(String dataSource) {
		String projections = this.projections.stream().map(project->{
			Field field = project.getField();
			if (project instanceof FunctionProjection){
				return ((FunctionProjection) project).getFunction() + "("+ Util.getAlias(field, dataSource) + ") as " + Util.getFullFieldName(field);
			}
			return Util.getAlias(field, dataSource) +" as " + Util.getFullFieldName(field);
		}).collect(Collectors.joining(", "));

		return projections.isEmpty() ? getAllProjectionsSql(dataSource) : projections;
	}

	private String getAllProjectionsSql(String dataSource) {
		Set<Field> fieldsA = new LinkedHashSet<>(Arrays.asList(clazz.getDeclaredFields()));
		for (Join<?, ?> join : joins) {
			fieldsA.addAll(Arrays.asList(join.getB().getDeclaredFields()));
			fieldsA.addAll(Arrays.asList(join.getA().getDeclaredFields()));
		}

		List<Field> fieldsB = fieldsA.stream().filter(field-> Util.getColumnAnnotation(field, dataSource) != null).collect(Collectors.toList());
		return fieldsB.stream().map(field-> Util.getAlias(field, dataSource) +" as " + Util.getFullFieldName(field)).collect(Collectors.joining(", "));
	}

	private String fromSql(String dataSource) {
		String mainQuery = Util.getTable(clazz, dataSource);
		if (joins.size() > 0) //Pega primeiro whereable do encadeamento de Joins
			mainQuery = Util.getTable(joins.get(0).getA(), dataSource);

		return mainQuery + (joins.size() > 0 ? " " : "") +
			this.joins.stream().map(join -> join.getJoinType().toString() + " JOIN " + Util.getTable(join.getB(), dataSource) + " ON " + join.getOns().stream().map(on-> Util.getAlias(on.a,dataSource ) + " = "+ Util.getAlias(on.b,dataSource ) ).collect(Collectors.joining(" AND "))).collect(Collectors.joining(" "));
	}


}

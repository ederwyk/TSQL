package br.com.stockinfo.tsql.util;

import br.com.stockinfo.tsql.annotations.Column;
import br.com.stockinfo.tsql.annotations.Columns;
import br.com.stockinfo.tsql.annotations.Table;
import br.com.stockinfo.tsql.annotations.Tables;
import br.com.stockinfo.tsql.fields.tuple.FieldValue;
import br.com.stockinfo.tsql.interfaces.Modifiyer;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Classe com métodos auxiliares pro TSQL
 */
public class Util {

	public static final Logger log = LoggerFactory.getLogger(Util.class);

	/** Retorna o campo de uma classe a partir do nome */
	public static Field getField(String fieldName, Class<?> clazz){
		for (Field field : clazz.getDeclaredFields()){
			if (field.getName().equalsIgnoreCase(fieldName))
				return field;
		}
		return null;
	}

	/** Retorna o nome do campo no banco de dados do plano especificado (dataSource) */
	public static String getField(Field field, String dataSource){
		dataSource = getDataSurce(dataSource);
		
		Column column = getColumnAnnotation(field,dataSource);
		if (column == null){
			throw new RuntimeException("Campo "+field.getName()+" não possui mapeamento mas está sendo chamado mesmo assim.");
		}
		return column.name().isEmpty() ? column.snake() ? Configurations.conventions.toDbName(field.getName()) : field.getName() : column.name();
	}

	/** Retorna o nome da tabela de uma classe a partir do nome do plano */
	public static String getTable(Class<?> clazz, String dataSource){
		dataSource = getDataSurce(dataSource);
		
		Table table = getTableAnnotation(clazz, dataSource);
		return table.name().isEmpty() ? clazz.getSimpleName() : table.name();
	}

	/** Retorna alias do campo especificado, que é basicamente o nome do campo appendado do nome da tabela  */
	public static String getAlias(Field field, String dataSource){
		dataSource = getDataSurce(dataSource);
		
		return getTable(field.getDeclaringClass(), dataSource)+ "." + getField(field,dataSource);
	}

	/** Retorna coiso, que é o texto de uma váriavel dentro da classe Operator */
	public static String getCoiso(Object left, String dataSource) {
		dataSource = getDataSurce(dataSource);
		
		if (left instanceof Field)
			return getAlias((Field) left, dataSource);
		return Configurations.conventions.getText(left);
	}

	/** Retorna coiso, que é o texto de uma váriavel dentro da classe Operator */
	public static String getCoiso(Object left, String dataSource, Modifiyer modifiyer) {
		dataSource = getDataSurce(dataSource);
		
		if (modifiyer != null)
			return modifiyer.toString() + "(" + getCoiso(left,dataSource) + ")";
		return getCoiso(left,dataSource);
	}

	/** Retorna todas as anotações Column de um campo a partir do plano */
	public static Column getColumnAnnotation(Field field, String dataSource){
		dataSource = getDataSurce(dataSource);
		final String ddss = dataSource;
		
		Columns[] columnsAnnotations = field.getAnnotationsByType(Columns.class);

		Column[] columns;
		if (columnsAnnotations.length == 0)
			columns = field.getAnnotationsByType(Column.class);
		else
			columns = columnsAnnotations[0].value();

		List<Column> columnsList = Arrays.stream(columns).filter(y-> containsCaseInsensitive(ddss, y.dataSource())).collect(Collectors.toList());

		if (columnsList.size() > 1)
			throw new RuntimeException("Mapeamento de campo " + field.getName() + " da classe " + field.getDeclaringClass().getSimpleName() + " ambiguo (" + dataSource +")");

		if (columnsList.size() == 0 && !dataSource.isEmpty())
			return getColumnAnnotation(field,"");

		if (columnsList.size() == 0)
			return null;

		return columnsList.get(0);
	}

	/** Retorna todas as anotações Table de uma classe a partir do plano */
	private static Table getTableAnnotation(Class<?> clazz, String dataSource){
		dataSource = getDataSurce(dataSource);
		final String ddataSourceName = dataSource;
		
		Tables[] tables = clazz.getAnnotationsByType(Tables.class);

		Table[] columns;
		if (tables.length == 0)
			columns = clazz.getAnnotationsByType(Table.class);
		else
			columns = tables[0].value();

		List<Table> columnsFiltered = Arrays.stream(columns).filter(y-> containsCaseInsensitive(ddataSourceName,y.dataSource())).collect(Collectors.toList());

		if (columnsFiltered.size() > 1)
			throw new RuntimeException("Mapeamento de entidade " + clazz.getSimpleName() + " ambiguo (" + dataSource +")");

		if (columnsFiltered.size() == 0 && !dataSource.isEmpty())
			return getTableAnnotation(clazz,"");

		if (columnsFiltered.size() == 0)
			throw new RuntimeException("Classe " + clazz.getSimpleName() + " não possui mapeamento mas está sendo chamada mesmo assim.");

		return columnsFiltered.get(0);
	}

	/**
	 * Retorna todos os campos que possuem anotação de Column a partir de um plano a partir de um objeto, fazendo checagem de null e empty
	 * @param object Objeto do qual será feito Insert/Update
	 * @param allowNull Se verdadeiro, também inclui campos nulos
	 * @param allowEmpty Se verdadeiro, também inlcui campos vazios
	 * @param dataSource Plano
	 */
	public static List<FieldValue> getAllSetableFields(Object object, Boolean allowNull, Boolean allowEmpty, String dataSource){
		dataSource = getDataSurce(dataSource);
		
		List<FieldValue> fieldValues = new ArrayList<>();
		for (Field field : object.getClass().getDeclaredFields()){
			Column column = Util.getColumnAnnotation(field,dataSource);
			if (column != null && !column.readOnly() && !column.identity()){
				field.setAccessible(true);
				Object value = null;
				try {
					value = field.get(object);
				} catch (IllegalAccessException e) {
					log.warn(e.getLocalizedMessage(), e);
				}
				if (allowNull || value != null){
					if (allowEmpty || isNotEmpty(value)){
						fieldValues.add(new FieldValue<>(field, value));
					}
				}
			}
		}
		return fieldValues;
	}

	private static boolean isNotEmpty(Object value) {
		if (value instanceof String){
			String s = ((String) value).trim();
			return !s.isEmpty();
		}
		return true;
	}

	private static boolean containsCaseInsensitive(String s, String[] l){
		for (String string : l){
			if (string.equalsIgnoreCase(s)){
				return true;
			}
		}
		return false;
	}

	private static boolean containsCaseInsensitive(String s, List<String> l){
		for (String string : l){
			if (string.equalsIgnoreCase(s)){
				return true;
			}
		}
		return false;
	}

	/** Retorna objeto que é utilizado em projeto data-source-builder */
	public static <T> HashMap validateEntityJson(Class<T> clazz, List<String> dbFields, String dataSource){
		dataSource = getDataSurce(dataSource);
		final String ddss = dataSource;
		
		HashMap<String, Object> jsonObject = new HashMap<>();

		dbFields = dbFields.stream().map(String::toLowerCase).collect(Collectors.toList());

		List<String> fieldsEntity = new ArrayList<>();
		List<String> unmmappedFields = new ArrayList<>();
		List<Field> unmappedRFields = new ArrayList<>();
		Arrays.stream(clazz.getDeclaredFields()).forEach(field->{
			try {
				fieldsEntity.add(Util.getField(field, ddss));
			}catch (Exception e){
				unmmappedFields.add(field.getName());
				unmappedRFields.add(field);
			}
		});

		Collections.sort(fieldsEntity);
		Collections.sort(unmmappedFields);

		List<String> camel = new ArrayList<>();
		for (String entityField : unmmappedFields){
			for (String dbField : dbFields){
				if (dbField.equalsIgnoreCase(entityField))
					camel.add(entityField);
			}
		}

		class Tuple{
			private String a;
			private String b;

			private Tuple(String a, String b){
				this.a = a;
				this.b = b;
			}

			public String getA() {
				return a;
			}

			public void setA(String a) {
				this.a = a;
			}

			public String getB() {
				return b;
			}

			public void setB(String b) {
				this.b = b;
			}
		}

		List<Tuple> correspondente = new ArrayList<>();
		for (Field field : unmappedRFields) {
			for (String dbField : dbFields){
				Columns[] columnss = field.getAnnotationsByType(Columns.class);
				Column[] columns;
				if (columnss.length == 0)
					columns = field.getAnnotationsByType(Column.class);
				else
					columns = columnss[0].value();

				List<Column> columnsList = Arrays.stream(columns).filter(column-> dbField.equalsIgnoreCase(column.name())).collect(Collectors.toList());
				for (Column column : columnsList){
					correspondente.add(new Tuple(field.getName(),column.name()));
				}
			}
		}

		List<String> snake = new ArrayList<>();
		for (String entityField : unmmappedFields){
			for (String dbField : dbFields){
				if (Configurations.conventions.toDbName(entityField).equalsIgnoreCase(dbField) && !Configurations.conventions.toDbName(entityField).equals(dbField))
					snake.add(entityField + " - " + dbField);
				if (Configurations.conventions.toDbName(entityField).equals(dbField))
					snake.add(entityField);
			}
		}
		snake = (List<String>) CollectionUtils.subtract(snake, camel);

		List<String> all = new ArrayList<>();
		all.addAll(camel);
		all.addAll(snake);
		all.addAll(correspondente.stream().map(o->o.a).collect(Collectors.toList()));
		Set<String> set = new HashSet<>();
		ArrayList<String> duplicateds = new ArrayList<>();
		for (String name : all){
			if (!set.add(name))
				duplicateds.add(name);
		}

		List<String> invalids = new ArrayList<>();
		for (String fieldEntity : fieldsEntity){
			if (!containsCaseInsensitive(fieldEntity, dbFields))
				invalids.add(fieldEntity);
		}

		jsonObject.put("unmappedFields", unmmappedFields);
		jsonObject.put("camel", camel);
		jsonObject.put("correspondente", correspondente);
		jsonObject.put("snake", snake);
		jsonObject.put("duplicateds", duplicateds);
		jsonObject.put("invalid", invalids);

		return jsonObject;
	}

	public static <T> String getFullFieldName(Field field) {
		return "\"" + field.getDeclaringClass().getSimpleName() + "." + field.getName() + "\"";
	}

	/** Retorna lista que é utilizada em projeto data-source-builder */
	public static List<String> getAllTableNames(Class<?> clazz) {
		Tables[] tables = clazz.getAnnotationsByType(Tables.class);

		Table[] columns;
		if (tables.length == 0)
			columns = clazz.getAnnotationsByType(Table.class);
		else
			columns = tables[0].value();

		return Arrays.stream(columns).map(Table::name).collect(Collectors.toList());
	}
	
	public static String getDataSurce(String dataSource) {
		return dataSource
				.replaceAll("HOMOLOGACAO", "")
				.replaceAll("HOMOLOGA", "")
				.replaceAll("DEV", "")
				.replaceAll("homologacao", "")
				.replaceAll("homologa", "")
				.replaceAll("dev", "");
	}
}

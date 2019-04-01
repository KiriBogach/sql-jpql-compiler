package transformer.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import elements.CampoFrom;
import elements.CampoSelect;
import elements.Condicion;
import elements.Having;
import elements.Join;
import elements.TipoJoin;
import javafx.util.Pair;
import transformer.api.GSP_API;
import utils.Utils;
import utils.introspection.ClaseJPA;
import utils.introspection.ClassIntrospection;

public class GSP_JPQLTransformer extends JPQLTransformerBase {

	private static final char CARACTER_AUTOINCREMENTAL = 'a';
	private static final String DIFERENCIADOR = "_";
	private String diferenciador = "";
	private char aliasAutoIncrementado;

	// Variables del FROM
	private TreeMap<String, ClaseJPA> mappingAliasClase;
	private TreeMap<String, String> mappingNombreBDAlias;
	private HashMap<String, ClaseJPA> mappingClasesNombreJPA;
	private LinkedList<String> fromEncontrados;

	// Variables de JOINS
	private LinkedList<String> joinEncontrados;

	// Variables del SELECT
	private LinkedList<String> selectEncontrados;

	// Variables del WHERE
	private LinkedList<String> whereEncontrados;

	// Variables del GROUP BY
	private LinkedList<String> groupByEncontrados;

	// Variables del HAVING
	private LinkedList<String> havingEncontrados;

	// Variables del ORDER BY
	private LinkedList<String> orderByEncontrados;

	public GSP_JPQLTransformer() {
		super();
		this.api = new GSP_API();

		this.aliasAutoIncrementado = CARACTER_AUTOINCREMENTAL;
		this.mappingAliasClase = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		this.mappingNombreBDAlias = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		this.mappingClasesNombreJPA = new HashMap<>();

		// Lista con todo lo incluido (separamos luego por ',')
		this.fromEncontrados = new LinkedList<>();
		this.joinEncontrados = new LinkedList<>();
		this.selectEncontrados = new LinkedList<>();
		this.whereEncontrados = new LinkedList<>();
		this.groupByEncontrados = new LinkedList<>();
		this.havingEncontrados = new LinkedList<>();
		this.orderByEncontrados = new LinkedList<>();
	}

	public void reset() {
		this.diferenciador = "";
		this.aliasAutoIncrementado = CARACTER_AUTOINCREMENTAL;

		this.mappingAliasClase.clear();
		this.mappingNombreBDAlias.clear();
		this.mappingClasesNombreJPA.clear();
		this.fromEncontrados.clear();
		this.joinEncontrados.clear();
		this.selectEncontrados.clear();
		this.whereEncontrados.clear();
		this.groupByEncontrados.clear();
		this.havingEncontrados.clear();
		this.orderByEncontrados.clear();

		this.api.reset();
	}

	@Override
	public String transform(String sql) {
		this.reset();
		this.api.parse(sql);

		this.select = this.api.getSelect();
		this.from = this.api.getFrom();
		this.where = this.api.getWhere();
		this.groupBy = this.api.getGroupBy();
		this.orderBy = this.api.getOrderBy();

		String selectQuery = "SELECT ";
		String fromQuery = "FROM ";
		String whereQuery = "";
		String groupByQuery = "";
		String havingByQuery = "";
		String orderByQuery = "";

		// Miramos si usamos diferenciador o no
		this.checkUsoLimitador();

		// Mapeamos y construimos los datos
		this.buildFrom();
		this.buildJoin();
		this.buildSelect();
		this.buildWhere();
		this.buildGroupBy();
		this.buildOrderBy();

		// FROM
		fromQuery += String.join(", ", fromEncontrados) + " ";

		// JOIN
		if (!joinEncontrados.isEmpty()) {
			fromQuery += String.join("", joinEncontrados);
		}

		// SELECT
		selectQuery += String.join(", ", selectEncontrados) + " ";

		// WHERE
		if (!whereEncontrados.isEmpty()) {
			whereQuery += "WHERE " + String.join(" ", whereEncontrados) + " ";
		}

		// GROUP BY
		if (!groupByEncontrados.isEmpty()) {
			groupByQuery += "GROUP BY " + String.join(", ", groupByEncontrados) + " ";
		}

		// HAVING
		if (!havingEncontrados.isEmpty()) {
			havingByQuery += "HAVING " + String.join(" ", havingEncontrados) + " ";
		}

		// ORDER BY
		if (!orderByEncontrados.isEmpty()) {
			orderByQuery += "ORDER BY " + String.join(", ", orderByEncontrados) + " ";
		}

		return (selectQuery + fromQuery + whereQuery + groupByQuery + havingByQuery + orderByQuery).trim();
	}

	private void buildFrom() {
		for (CampoFrom campoFrom : this.from.getTables()) {
			String nombreCampo = campoFrom.getNombre(); // FROM (Table) a
			String aliasCampo = campoFrom.getAlias(); // FROM Table (a)

			ClaseJPA claseJPA = ClassIntrospection.getClaseJPA(nombreCampo);

			String fromString = claseJPA.getNombreJPA();
			String aliasUsado = "";

			if (aliasCampo == null || aliasCampo.equals("")) {
				// Si no tiene alias, se lo asignamos nosotros
				aliasUsado = aliasAutoIncrementado + this.diferenciador;
				fromString += " " + aliasUsado;
				this.aliasAutoIncrementado++;
			} else {
				// Si tiene alias, lo respetamos
				aliasUsado = aliasCampo;
				fromString += " " + aliasUsado;
			}

			this.mappingAliasClase.put(aliasUsado, claseJPA);
			this.mappingNombreBDAlias.put(nombreCampo, aliasUsado);
			this.mappingClasesNombreJPA.put(claseJPA.getNombreJPA(), claseJPA);
			this.fromEncontrados.add(fromString);
		}
	}

	public void buildJoin() {
		LinkedList<Join> joins = this.from.getJoins();

		for (Join join : joins) {
			String aliasCampo = join.getTable().getAlias();
			String nombreCampo = join.getTable().getNombre();

			ClaseJPA claseJPA = ClassIntrospection.getClaseJPA(nombreCampo);
			String aliasUsado = "";

			if (aliasCampo == null || aliasCampo.equals("")) {
				// Si no tiene alias, se lo asignamos nosotros
				aliasUsado = aliasAutoIncrementado + this.diferenciador;
				this.aliasAutoIncrementado++;
			} else {
				// Si tiene alias, lo respetamos
				aliasUsado = aliasCampo;
			}

			// Añadimos los joins al mapping
			this.mappingAliasClase.put(aliasUsado, claseJPA);
			this.mappingNombreBDAlias.put(nombreCampo, aliasUsado);
			this.mappingClasesNombreJPA.put(claseJPA.getNombreJPA(), claseJPA);

			// https://www.objectdb.com/java/jpa/query/jpql/from
			TipoJoin tipoJoin = join.getTipo();
			Set<Condicion> condiciones = join.getCondiciones();

			// Buscamos una relación y mapeo por '.'
			claseJPA.getAtributoWithJoinColumn(nombreCampo);

			// Buscamos la relación concreta no presente en los atributos
			// sino por objetos JPA
			Pair<String, Condicion> relacionObjetual = this.getRelationCondicition(condiciones);
			if (relacionObjetual != null) {
				// Falta ver las demás condiciones JOIN p.category c ON ...
				condiciones.remove(relacionObjetual.getValue());
				String onCondition = parseCondicionExpressions(condiciones, join.getCondicionRaw());
				// System.out.println(onCondition);
				// System.out.println(relacionObjetual.getValue().getRawConditionRE());
				onCondition = Utils.reemplazarYNormalizar(onCondition, relacionObjetual.getValue().getRawConditionRE());

				if (!onCondition.isEmpty()) {
					onCondition = "ON " + onCondition;
				}

				joinEncontrados.add(relacionObjetual.getKey() + " " + onCondition);
			} else {
				// Construimos la condición y la query
				String rawWhereString = join.getCondicionRaw();
				String treatedJoinString = parseCondicionExpressions(condiciones, rawWhereString);

				// Tenemos como enumerados: JOIN, LEFT, RIGHT ..., filtramos JOIN para no
				// volverlo a poner
				// Para los demás necesitamos la sintasis JOIN
				String tipoJoinString = tipoJoin.toString() + (tipoJoin.equals(TipoJoin.JOIN) ? "" : " JOIN");
				joinEncontrados.add(
						tipoJoinString + " " + claseJPA.getNombreJPA() + " " + aliasUsado + " ON " + treatedJoinString);
			}
		}
	}

	private void buildSelect() {
		Collection<CampoSelect> selectFields = this.select.getFields();
		for (CampoSelect campoSelect : selectFields) {

			String aliasCampo = campoSelect.getAlias(); // customerId as (identificador)
			//String nombreCampo = campoSelect.getNombre(); // (customerId)
			String tablaReferida = campoSelect.getTableReference(); // (c).customerId
			String nombreRaw = campoSelect.getRawNombre(); // c.(customerId)

			String selectFormado = "";

			if (nombreRaw.equals("*")) {
				if (tablaReferida == null || tablaReferida.isEmpty()) {
					// SELECT * FROM T1, T2 --> SELECT a, b FROM T1 a, T2 b
					// SELECT * FROM T1 --> SELECT a FROM T1 a
					selectFormado += String.join(", ", this.mappingAliasClase.keySet());
				} else {
					// SELECT a.* FROM T1 a, T2 b --> SELECT a FROM T1 a, T2 b
					if (this.mappingAliasClase.containsKey(tablaReferida)) {
						// SELECT c.* FROM Customers c
						selectFormado += tablaReferida;
					} else {
						// SELECT customers.* FROM Customers
						selectFormado += this.mappingNombreBDAlias.get(tablaReferida);
					}
				}

				this.selectEncontrados.add(selectFormado);
				continue;
			}

			// if (tablaReferida == null || tablaReferida.isEmpty()) {

			/*
			 * Hay que buscar en todas las tablas del FROM el campo (tiene que estar en
			 * alguno de los dos, sino habría colisión y la query estaría mal).
			 * 
			 * Recorremos, pues, todas las clases del FROM:
			 */

			if (nombreRaw.contains("(") && nombreRaw.contains(")")) {
				// Con '(' y ')', es decir, con una función
				// Por ahora eliminamos la función
				int comienzoParentesis = nombreRaw.indexOf("(");
				int finParentesis = nombreRaw.indexOf(")");
				nombreRaw = nombreRaw.substring(comienzoParentesis + 1, finParentesis);
				Pair<String, String> aliasNombreReal = getTableFromAtributo(nombreRaw);

				if (aliasNombreReal != null) {
					// Tenemos un campo de BD
					selectFormado += aliasNombreReal.getKey() + "." + aliasNombreReal.getValue();
				} else {
					// Es un literal
					selectFormado += nombreRaw;
				}
			} else {
				Pair<String, String> aliasNombreReal = getTableFromAtributo(nombreRaw);
				if (aliasNombreReal != null) {
					selectFormado = aliasNombreReal.getKey() + "." + aliasNombreReal.getValue();
				}

				if (aliasCampo != null && !aliasCampo.isEmpty()) {
					selectFormado += " as " + aliasCampo;
				}
			}

			this.selectEncontrados.add(this.parseCondition(nombreRaw));
		}
	}

	private void buildWhere() {
		String rawWhereString = this.where.getCondicionRaw();
		Set<Condicion> condiciones = this.where.getCondiciones();
		if (condiciones == null || condiciones.isEmpty()) {
			return;
		}

		String treatedWhereString = parseCondicionExpressions(condiciones, rawWhereString);
		this.whereEncontrados.add(treatedWhereString);
	}

	private void buildGroupBy() {
		Collection<CampoSelect> groupByCampos = this.groupBy.getFields();
		if (groupByCampos.isEmpty()) {
			return;
		}

		for (CampoSelect campo : groupByCampos) {
			String condicion = this.parseCondition(campo.getRawNombre());
			this.groupByEncontrados.add(condicion);
		}

		Having having = this.groupBy.getHaving();
		if (having == null) {
			return;
		}

		String rawWhereString = having.getCondicionRaw();
		Set<Condicion> condiciones = having.getCondiciones();
		if (condiciones == null || condiciones.isEmpty()) {
			return;
		}

		String treatedWhereString = parseCondicionExpressions(condiciones, rawWhereString);
		this.havingEncontrados.add(treatedWhereString);

	}

	private void buildOrderBy() {
		Collection<CampoSelect> orderByCampos = this.orderBy.getFields();
		if (orderByCampos.isEmpty()) {
			return;
		}

		for (CampoSelect campo : orderByCampos) {
			String condicion = this.parseCondition(campo.getRawNombre());
			String direccionOrden = campo.getAlias(); // DESC, ASC
			if (direccionOrden != null && !direccionOrden.isEmpty()) {
				condicion += " " + direccionOrden.toUpperCase();
			}

			this.orderByEncontrados.add(condicion);
		}
	}

	private Pair<String, Condicion> getRelationCondicition(Set<Condicion> condiciones) {
		for (Condicion condicion : condiciones) {
			String parteIzquierda = condicion.getIzquierda();
			String parteDerecha = condicion.getDerecha();
			String condicionParseadaIzquierda = this.parseCondition(parteIzquierda);
			String condicionParseadaDerecha = this.parseCondition(parteDerecha);
			String referenciaIzquierda = Utils.getOnlyFieldTable(condicionParseadaIzquierda);
			String referenciaDerecha = Utils.getOnlyFieldTable(condicionParseadaDerecha);
			String campoIzquierda = Utils.getRawColumnValue(condicionParseadaIzquierda);
			String campoDerecha = Utils.getRawColumnValue(condicionParseadaDerecha);

			if (campoIzquierda == null || campoIzquierda.isEmpty() || campoIzquierda.equalsIgnoreCase("null")) {
				ClaseJPA claseDerecha = this.mappingAliasClase.get(referenciaDerecha);
				if (claseDerecha.getAtributoID().equalsIgnoreCase(campoDerecha)) {
					// buscamos algo de la primera clase (que tiene null) de la segunda clase
					ClaseJPA claseIzquierda = this.mappingAliasClase.get(referenciaIzquierda);
					claseIzquierda.getAtributoWithJoinColumn(claseDerecha.getAtributoID());
					String joinResult = "JOIN " + referenciaIzquierda + "."
							+ claseIzquierda.getAtributoWithJoinColumn(claseDerecha.getAtributoID()) + " "
							+ referenciaDerecha;
					return new Pair<String, Condicion>(joinResult, condicion);
				}
			} else if (campoDerecha == null || campoDerecha.isEmpty() || campoDerecha.equalsIgnoreCase("null")) {
				ClaseJPA claseIzquierda = this.mappingAliasClase.get(referenciaIzquierda);
				if (claseIzquierda.getAtributoID().equalsIgnoreCase(campoIzquierda)) {
					// buscamos algo de la primera clase (que tiene null) de la segunda clase
					ClaseJPA claseDerecha = this.mappingAliasClase.get(referenciaDerecha);
					claseIzquierda.getAtributoWithJoinColumn(claseDerecha.getAtributoID());
					String joinResult = "JOIN " + referenciaDerecha + "."
							+ claseDerecha.getAtributoWithJoinColumn(claseIzquierda.getAtributoID()) + " "
							+ referenciaIzquierda;
					return new Pair<String, Condicion>(joinResult, condicion);
				}
			}
		}

		return null;
	}

	private Pair<String, String> getTableFromAtributo(String atributoBuscado) {
		String atributoNormalizado = getJPAFormat(atributoBuscado);
		for (Map.Entry<String, ClaseJPA> claseBuscada : this.mappingAliasClase.entrySet()) {
			String alias = claseBuscada.getKey();
			ClaseJPA clase = claseBuscada.getValue();
			String atributoEnClase = clase.getAttributeRealName(atributoNormalizado);
			if (atributoEnClase != null) {
				return new Pair<>(alias, atributoEnClase);
			}
		}

		return null;
	}

	private Pair<String, String> getTableFromAtributo(ClaseJPA claseJPA, String atributoBuscado) {
		String atributoNormalizado = getJPAFormat(atributoBuscado);
		String atributoEnClase = claseJPA.getAttributeRealName(atributoNormalizado);
		if (atributoEnClase != null) {
			return new Pair<>("", atributoEnClase);
		}
		return null;
	}

	private String parseCondition(String condicion) {
		String resultado = "";
		// System.out.println(condicion);
		if (condicion.contains(".")) {
			// Puede ser:
			// ... FROM TABLA a WHERE a.atr
			// o
			// ... FROM TABLA WHERE tabla.atr
			String referencia = Utils.getFieldTable(condicion);
			String rawColumn = Utils.getRawColumnValue(condicion);

			if (this.mappingNombreBDAlias.containsKey(referencia)) {
				// ... FROM TABLA WHERE tabla.atr
				String alias = this.mappingNombreBDAlias.get(referencia);
				String nombreCampo = this.mappingAliasClase.get(alias).getAttributeRealName(rawColumn);
				resultado += alias + "." + nombreCampo;
			} else {
				// ... FROM TABLA a WHERE a.atr

				// Esto puede venir en mayúsculas, minúsculas...etc
				// Como es case insensitive lo encontraremos, pero necesitamos el correcto
				ClaseJPA claseJPA = this.mappingAliasClase.get(referencia);
				referencia = this.getExactMappingAlias(referencia);

				Pair<String, String> aliasNombreReal = getTableFromAtributo(claseJPA, rawColumn);
				// System.out.println(aliasNombreReal);
				if (aliasNombreReal != null) {
					resultado += referencia + "." + aliasNombreReal.getValue();
				} else {
					resultado += referencia + ".null";
				}
				// System.out.println(resultado);
			}
		} else if (condicion.contains("(") && condicion.contains(")")) {
			// Con '(' y ')', es decir, con una función
			// Por ahora eliminamos la función
			int comienzoParentesis = condicion.indexOf("(");
			int finParentesis = condicion.indexOf(")");
			condicion = condicion.substring(comienzoParentesis + 1, finParentesis);
			Pair<String, String> aliasNombreReal = getTableFromAtributo(condicion);

			if (aliasNombreReal != null) {
				// Tenemos un campo de BD
				resultado += aliasNombreReal.getKey() + "." + aliasNombreReal.getValue();
			} else {
				// Es un literal
				resultado += condicion;
			}
		} else {
			// Sin .
			// Puede ser un campo BD
			// o
			// Un literal
			Pair<String, String> aliasNombreReal = getTableFromAtributo(condicion);

			if (aliasNombreReal != null) {
				// Tenemos un campo de BD
				resultado += aliasNombreReal.getKey() + "." + aliasNombreReal.getValue();
			} else {
				// Es un literal
				resultado += condicion;
			}
		}

		return resultado;
	}

	private String parseCondicionExpressions(Set<Condicion> condiciones, String rawExpression) {
		List<String> condionesToChange = new LinkedList<>();
		for (Condicion condicion : condiciones) {
			String parteIzquierda = condicion.getIzquierda();
			String parteDerecha = condicion.getDerecha();
			if (parteIzquierda != null && !parteIzquierda.isEmpty()) {
				// Para literales y LIKE ('valores')
				if (parteIzquierda.startsWith("'") || parteIzquierda.startsWith("(")) {
					condionesToChange.add(parteIzquierda);
				} else {
					condionesToChange.add(parteIzquierda.toUpperCase());
				}
			}
			if (parteDerecha != null && !parteDerecha.isEmpty()) {
				// Para literales y LIKE ('valores')
				if (parteDerecha.startsWith("'") || parteDerecha.startsWith("(")) {
					condionesToChange.add(parteDerecha);
				} else {
					condionesToChange.add(parteDerecha.toUpperCase());
				}
			}
		}

		// Ponemos primero los que tienen alias o referencia a su tabla
		condionesToChange.sort((s1, s2) -> s1.contains(".") && !s2.contains(".") ? -1 : 1);

		String treatedWhereString = rawExpression.toUpperCase();
		for (String condicion : condionesToChange) {
			if (condicion.startsWith("'") || condicion.startsWith("(")) {
				treatedWhereString = treatedWhereString.replace(condicion.toUpperCase(), condicion);
				continue;
			}
			String condicionParseada = this.parseCondition(condicion);
			if (condicion.equals(condicionParseada)) {
				continue;
			}

			treatedWhereString = treatedWhereString.replace(condicion, this.parseCondition(condicion));
		}

		return treatedWhereString;
	}

	private String getExactMappingAlias(String referencia) {
		for (String key : this.mappingAliasClase.keySet()) {
			if (key.equalsIgnoreCase(referencia)) {
				return key;
			}
		}
		return null;
	}

	private void checkUsoLimitador() {
		for (CampoFrom campoFrom : this.from.getTables()) {
			String alias = campoFrom.getAlias();
			if (alias.matches("[a-z]")) {
				this.diferenciador = DIFERENCIADOR;
				break;
			}
		}
	}

	private String getJPAFormat(String entrada) {
		String phrase = entrada.toLowerCase();
		while (phrase.contains("_")) {
			phrase = phrase.replaceFirst("_[a-z]",
					String.valueOf(Character.toUpperCase(phrase.charAt(phrase.indexOf("_") + 1))));
		}
		return phrase;
	}

}

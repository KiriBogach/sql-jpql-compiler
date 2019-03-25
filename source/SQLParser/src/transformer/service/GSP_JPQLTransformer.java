package transformer.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import elements.ArbolCondicion;
import elements.CampoFrom;
import elements.CampoSelect;
import elements.Condicion;
import transformer.api.GSP_API;
import utils.Utils;
import utils.introspection.ClassIntrospection;

public class GSP_JPQLTransformer extends JPQLTransformerBase {

	private static final char CARACTER_AUTOINCREMENTAL = 'a';
	private static final String DIFERENCIADOR = "_";
	private String diferenciador = "";
	private char aliasAutoIncrementado;

	// Variables del FROM
	private HashMap<String, Class<?>> mappingClases;
	private List<String> fromEncontrados;
	private HashMap<String, String> mappingAliasJPAName;

	// Variables del SELECT
	List<String> selectEncontrados;

	// Variables del WHERE
	List<String> whereEncontrados;

	public GSP_JPQLTransformer() {
		super();
		this.api = new GSP_API();

		this.aliasAutoIncrementado = CARACTER_AUTOINCREMENTAL;
		this.mappingClases = new HashMap<>(); // alias - claseJPA
		this.mappingAliasJPAName = new HashMap<>(); // alias - nombreClaseJPA

		// Lista con todo lo incluido (separamos luego por ',')
		this.fromEncontrados = new LinkedList<>();
		this.selectEncontrados = new LinkedList<>();
		this.whereEncontrados = new LinkedList<>();
	}

	@Override
	public String transform(String sql) {
		this.api.parse(sql);

		this.select = this.api.getSelect();
		this.from = this.api.getFrom();
		this.where = this.api.getWhere();

		String selectQuery = "SELECT ";
		String fromQuery = "FROM ";
		String whereQuery = "WHERE ";

		// Miramos si usamos diferenciador o no
		this.checkUsoLimitador();

		// Mapeamos y construimos los datos
		this.buildFrom();
		this.buildSelect();
		this.buildWhere();

		// Equivalente a implode de PHP
		selectQuery += String.join(", ", selectEncontrados) + " ";
		fromQuery += String.join(", ", fromEncontrados) + " ";
		whereQuery += String.join(" ", whereEncontrados) + " ";

		return (selectQuery + fromQuery + whereQuery).trim();
	}

	private void buildSelect() {
		Collection<CampoSelect> selectFields = this.select.getFields();
		for (CampoSelect campoSelect : selectFields) {
			// System.out.println("camposelect: " + campoSelect);

			String aliasCampo = campoSelect.getAlias(); // customerId as (identificador)
			String nombreCampo = campoSelect.getNombre(); // (customerId)
			String tablaReferida = campoSelect.getTableReference(); // (c).customerId
			String nombreRaw = campoSelect.getRawNombre(); // c.(customerId)

			if (nombreCampo.equals("*")) {
				this.selectEncontrados.add(mappingAliasJPAName.keySet().toArray()[0].toString());
				continue;
			}

			Class<?> claseReferida = mappingClases.get(tablaReferida);
			String nombreAtributoJPA = ClassIntrospection.getFieldName(claseReferida, nombreRaw);

			if (aliasCampo != null && aliasCampo.length() > 0) {
				nombreAtributoJPA += " as " + aliasCampo;
			}

			if (mappingAliasJPAName.containsKey(tablaReferida)) {
				// Si el FROM del campo tiene alias, lo usamos
				// SELECT ... FROM tabla a ... WHERE a.campo
				this.selectEncontrados.add(tablaReferida + "." + nombreAtributoJPA);
			} else {
				this.selectEncontrados.add(nombreAtributoJPA);
			}
		}
	}

	private void buildFrom() {
		for (CampoFrom campoFrom : this.from.getTables()) {
			String nombreCampo = campoFrom.getNombre();
			String aliasCampo = campoFrom.getAlias();

			// Tabla FROM del sql
			String nombreClaseBuscada = nombreCampo;

			// Clase JPA correspondiente a la tabla
			Class<?> claseBuscada = ClassIntrospection.getJPATableNameAnnotation(nombreClaseBuscada);
			String nombreClaseJPA = claseBuscada.getSimpleName();

			String fromString = nombreClaseJPA;
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

			this.mappingAliasJPAName.put(aliasUsado, nombreClaseJPA);
			this.mappingClases.put(aliasUsado, claseBuscada);
			this.fromEncontrados.add(fromString);
		}
	}

	private void buildWhere() {
		ArbolCondicion arbolWhere = this.where.getCondiciones();
		List<Condicion> condicionesWhere = arbolWhere.iterar();
		
		//boolean rootSeen = false;

		for (Condicion condicion : condicionesWhere) {
			String whereString = "";

			String parteIzquierda = condicion.getIzquierda();
			String operador = condicion.getOperador();
			String parteDerecha = condicion.getDerecha();

			if (parteIzquierda != null) {
				// buscar si está el atributo para diferenciar entre 'literal' vs campo bd
				if (!parteIzquierda.contains(".")) {
					// Si no tiene '.' no tiene alias, tendrá el nuestro
					String fieldToLookup = getJPAFormat(parteIzquierda);

					String nombreAtributoJPA = ClassIntrospection
							.getFieldName(mappingClases.get(mappingAliasJPAName.keySet().toArray()[0]), fieldToLookup);
					if (nombreAtributoJPA != null) {
						parteIzquierda = mappingAliasJPAName.keySet().toArray()[0] + "." + nombreAtributoJPA;
					}
				} else {
					String fieldToLookup = getJPAFormat(parteIzquierda);
					String nombreAtributoJPA = ClassIntrospection
							.getFieldName(mappingClases.get(mappingAliasJPAName.keySet().toArray()[0]), fieldToLookup);
					parteIzquierda = Utils.getFieldTable(fieldToLookup) + "." + nombreAtributoJPA;
					// tiene alias
					// buscamos en mappingClases eses alias
				}
				whereString += parteIzquierda + " ";
			}

			whereString += operador;

			if (parteDerecha != null) {
				if (!parteDerecha.contains(".")) {
					// System.out.println("formatter: " + getJPAFormat(parteIzquierda));
					String fieldToLookup = getJPAFormat(parteDerecha);

					String nombreAtributoJPA = ClassIntrospection
							.getFieldName(mappingClases.get(mappingAliasJPAName.keySet().toArray()[0]), fieldToLookup);
					if (nombreAtributoJPA != null) {
						parteDerecha = mappingAliasJPAName.keySet().toArray()[0] + "." + nombreAtributoJPA;
					}
				}
				whereString += " " + parteDerecha;
			}

			/*if (condicion.isParentesis()) {
				System.out.println(condicion.toString() + " tiene parentesis");
				if (this.whereEncontrados.size() > 0) {
					String nuevoPrimerElemento = "( " + this.whereEncontrados.get(0);
					this.whereEncontrados.set(0, nuevoPrimerElemento);
					whereString = ") " + whereString;
				}
			}
			
			if (condicion.isRoot()) {
				rootSeen = true;
			}*/

			this.whereEncontrados.add(whereString);
		}
	}

	private void checkUsoLimitador() {
		for (CampoFrom campoFrom : this.from.getTables()) {
			if (campoFrom.getAlias().matches("[a-z]")) {
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

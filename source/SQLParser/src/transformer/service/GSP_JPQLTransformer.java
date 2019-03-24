package transformer.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import elements.ArbolCondicion;
import elements.CampoFrom;
import elements.CampoSelect;
import elements.Condicion;
import transformer.api.GSP_API;
import utils.classfinder.ClassFinder;

public class GSP_JPQLTransformer extends JPQLTransformerBase {

	private static final String DIFERENCIADOR = "_";
	private String diferenciador = "";
	private char alias;

	public GSP_JPQLTransformer() {
		super();
		this.api = new GSP_API();
		this.alias = 'a';
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
		for (CampoFrom campoFrom : this.from.getTables()) {
			if (campoFrom.getAlias().matches("[a-z]")) {
				this.diferenciador = DIFERENCIADOR;
				break;
			}
		}

		HashMap<String, Class<?>> mappingClases = new HashMap<>(); // alias - claseJPA

		HashMap<String, String> mappingFrom = new HashMap<>(); // nombreClaseJPA - alias
		List<String> fromEncontrados = new LinkedList<>(); // lista con todo lo incluido (separamos luego por ',')
		for (CampoFrom campoFrom : this.from.getTables()) {
			// System.out.println("buscada: " + campoFrom.getNombre());
			String nombreClaseBuscada = campoFrom.getNombre();

			Class<?> claseBuscada = null;
			List<Class<?>> clases = ClassFinder.find("database.model");
			for (Class<?> clase : clases) {
				Annotation[] anotaciones = clase.getAnnotations();
				for (Annotation annotation : anotaciones) {
					int posNombre = annotation.toString().indexOf("Table(name=");
					int posNombreFin = annotation.toString().indexOf(", schema=");
					if (posNombre == -1 || posNombreFin == -1) {
						continue;
					}
					String nombreColumna = annotation.toString().substring(posNombre + 11, posNombreFin);
					if (nombreClaseBuscada.equalsIgnoreCase(nombreColumna)) {
						claseBuscada = clase;
						break;
					}
				}
			}

			// System.out.println("clase encontrada: " + claseBuscada.getSimpleName());
			String fromString = claseBuscada.getSimpleName();
			String aliasUsado = "";
			if (campoFrom.getAlias() == null || campoFrom.getAlias().equals("")) {
				aliasUsado = alias + diferenciador;
				fromString += " " + aliasUsado;
				mappingFrom.put(claseBuscada.getSimpleName(), aliasUsado);
				this.alias++;
			} else {
				aliasUsado = campoFrom.getAlias();
				fromString += " " + aliasUsado;
				mappingFrom.put(claseBuscada.getSimpleName(), aliasUsado);
			}

			mappingClases.put(aliasUsado, claseBuscada);
			fromEncontrados.add(fromString);
		}

		System.out.println("mapa from : " + mappingFrom);
		System.out.println("from query: " + fromQuery);

		List<String> selectEncontrados = new LinkedList<>();

		Collection<CampoSelect> selectFields = this.select.getFields();
		for (CampoSelect campoSelect : selectFields) {
			System.out.println("camposelect: " + campoSelect);

			String aliasCampo = campoSelect.getAlias();
			String nombreCampo = campoSelect.getNombre();

			// Si el select tiene alias (lo buscamos en su from)
			if (mappingFrom.containsValue(aliasCampo)) {
				Set<String> keys = mappingFrom.keySet();
				for (String key : keys) {
					if (mappingFrom.get(key).equals(aliasCampo)) {
						System.out.println("alias.campo: " + mappingFrom.get(key) + "." + nombreCampo);
						selectEncontrados.add(mappingFrom.get(key) + "." + nombreCampo);
					}
				}
			} else if (nombreCampo.equals("*")) {
				selectEncontrados.add(mappingFrom.values().toArray()[0].toString());
			} else {
				selectEncontrados.add(nombreCampo);
				// System.out.println("alias.campo: " + fromEncontrados.values().toArray()[0]);
				// jpql += fromEncontrados.values().toArray()[0] + " ";
			}
		}

		// System.out.println("join: " + String.join(", ", selectEncontrados));
		selectQuery += String.join(", ", selectEncontrados) + " ";

		// System.out.println("join: " + String.join(", ", fromEncontrados));
		fromQuery += String.join(", ", fromEncontrados) + " ";

		ArbolCondicion arbolWhere = this.where.getCondiciones();
		List<Condicion> condicionesWhere = arbolWhere.iterar();

		for (Condicion condicion : condicionesWhere) {
			String parteIzquierda = condicion.getIzquierda();
			String operador = condicion.getOperador();
			String parteDerecha = condicion.getDerecha();

			if (condicion.isParentesis()) {
				whereQuery = whereQuery.replace("WHERE", "WHERE (");
				System.out.println("where actual " + whereQuery);
				System.out.println("operador actual " + operador);
				whereQuery = whereQuery + ")";
			}

			if (parteIzquierda != null) {
				// debemos buscar si está el atributo para diferenciar entre 'literal' vs campo
				// bd
				if (!parteIzquierda.contains(".")) {
					// Si no tiene '.' no tiene alias, tendrá el nuestro

					// System.out.println("formatter: " + getJPAFormat(parteIzquierda));
					String fieldToLookup = getJPAFormat(parteIzquierda);

					for (Field f : mappingClases.get(mappingFrom.values().toArray()[0]).getDeclaredFields()) {
						// System.out.println(f.getName());
						if (f.getName().equalsIgnoreCase(fieldToLookup)) {
							parteIzquierda = mappingFrom.values().toArray()[0] + "." + f.getName();
							break;
						}
					}
				} else {
					parteIzquierda = parteIzquierda.replace("_", "");
					// tiene alias
					// buscamos en mappingClases eses alias
				}
				whereQuery += parteIzquierda + " ";
			}

			whereQuery += operador + " ";

			if (parteDerecha != null) {
				if (!parteDerecha.contains(".")) {
					// System.out.println("formatter: " + getJPAFormat(parteIzquierda));
					String fieldToLookup = getJPAFormat(parteDerecha);

					for (Field f : mappingClases.get(mappingFrom.values().toArray()[0]).getDeclaredFields()) {
						// System.out.println(f.getName());
						if (f.getName().equalsIgnoreCase(fieldToLookup)) {
							parteDerecha = mappingFrom.values().toArray()[0] + "." + f.getName();
							break;
						}
					}
				}
				whereQuery += parteDerecha + " ";
			}

		}

		System.out.println(this.select.getFieldNames());

		/*
		 * Annotation[] anotaciones = Customer.class.getAnnotations(); for (Annotation
		 * annotation : anotaciones) { int posNombre =
		 * annotation.toString().indexOf("Table(name="); int posNombreFin =
		 * annotation.toString().indexOf(", schema="); if (posNombre == -1 ||
		 * posNombreFin == -1) { continue; } String nombreColumna =
		 * annotation.toString().substring(posNombre + 11, posNombreFin);
		 * System.out.println(nombreColumna); }
		 * System.out.println(Arrays.deepToString(anotaciones));
		 */

		// c.getClass().getAnnotations();

		return selectQuery + fromQuery + whereQuery;
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

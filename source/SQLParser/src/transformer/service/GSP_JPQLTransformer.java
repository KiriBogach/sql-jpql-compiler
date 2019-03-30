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
		
		this.api.reset();
	}

	@Override
	public String transform(String sql) {
		this.reset();
		this.api.parse(sql);

		this.select = this.api.getSelect();
		this.from = this.api.getFrom();
		this.where = this.api.getWhere();

		String selectQuery = "SELECT ";
		String fromQuery = "FROM ";
		String whereQuery = "";

		// Miramos si usamos diferenciador o no
		this.checkUsoLimitador();

		// Mapeamos y construimos los datos
		this.buildFrom();
		this.buildJoin();
		this.buildSelect();
		this.buildWhere();
		
		fromQuery += String.join(", ", fromEncontrados) + " ";
		if (!joinEncontrados.isEmpty()) {
			fromQuery += String.join(" ", joinEncontrados) + " ";
		}
		selectQuery += String.join(", ", selectEncontrados) + " ";
		if (!whereEncontrados.isEmpty()) {
			whereQuery += "WHERE " + String.join(" ", whereEncontrados) + " ";
		}
		
		return (selectQuery + fromQuery + whereQuery).trim();
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

	private void buildSelect() {
		Collection<CampoSelect> selectFields = this.select.getFields();
		for (CampoSelect campoSelect : selectFields) {

			String aliasCampo = campoSelect.getAlias(); // customerId as (identificador)
			String nombreCampo = campoSelect.getNombre(); // (customerId)
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

			Pair<String, String> aliasNombreReal = getTableFromAtributo(nombreRaw);
			if (aliasNombreReal != null) {
				selectFormado = aliasNombreReal.getKey() + "." + aliasNombreReal.getValue();
			}

			if (aliasCampo != null && !aliasCampo.isEmpty()) {
				selectFormado += " as " + aliasCampo;
			}

			this.selectEncontrados.add(selectFormado);
		}
	}

	private void buildWhere() {
		String rawWhereString = this.where.getCondicionRaw();
		Set<Condicion> condiciones = this.where.getCondiciones();
		if (condiciones == null || condiciones.isEmpty()) {
			return;
		}

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
		// condionesToChange =
		// condionesToChange.stream().distinct().collect(Collectors.toList());

		// System.out.println(condionesToChange);
		String treatedWhereString = rawWhereString.toUpperCase();
		for (String condicion : condionesToChange) {
			if (condicion.startsWith("'") || condicion.startsWith("(")) {
				treatedWhereString = treatedWhereString.replace(condicion.toUpperCase(), condicion);
				continue;
			}
			String condicionParseada = this.parseCondition(condicion);
			if (condicion.equals(condicionParseada)) {
				continue;
			}
			// System.out.println("reemplazamos: " + condicion + ", con: " +
			// this.parseCondition(condicion));
			treatedWhereString = treatedWhereString.replace(condicion, this.parseCondition(condicion));

		}

		this.whereEncontrados.add(treatedWhereString);
	}

	public void buildJoin() {
		for (Join join : this.from.getJoins()) {
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

			this.mappingAliasClase.put(aliasUsado, claseJPA);
			this.mappingNombreBDAlias.put(nombreCampo, aliasUsado);
			this.mappingClasesNombreJPA.put(claseJPA.getNombreJPA(), claseJPA);
			//this.fromEncontrados.add(fromString);
		}
		
		
		for (Join join : this.from.getJoins()) {
			//System.out.println(join);
			String aliasCampo = join.getTable().getAlias();
			String nombreCampo = join.getTable().getNombre();
			TipoJoin tipoJoin = join.getTipo();
			
			ClaseJPA claseJPAJoin = ClassIntrospection.getClaseJPA(nombreCampo);
			
			if (tipoJoin.equals(TipoJoin.JOIN) || tipoJoin.equals(TipoJoin.INNER)) {
				//System.out.println(nombreCampo);
				//System.out.println(claseJPAJoin);
				//System.out.println(claseJPAJoin.getAtributoWithJoinColumn(nombreCampo));
				claseJPAJoin.getAtributoWithJoinColumn(nombreCampo);
			}
			
			for (Condicion condicion : join.getCondiciones()) {
				String parteIzquierda = condicion.getIzquierda();
				String parteDerecha = condicion.getDerecha();
				//System.out.println(parteIzquierda + "-" + parteDerecha);
				String condicionParseadaIzquierda = this.parseCondition(parteIzquierda);
				String condicionParseadaDerecha = this.parseCondition(parteDerecha);
				//System.out.println(condicionParseadaIzquierda + "-" + condicionParseadaDerecha);

				String referenciaIzquierda = Utils.getOnlyFieldTable(condicionParseadaIzquierda);
				String referenciaDerecha = Utils.getOnlyFieldTable(condicionParseadaDerecha);
				
				String campoIzquierda = Utils.getRawColumnValue(condicionParseadaIzquierda);
				String campoDerecha = Utils.getRawColumnValue(condicionParseadaDerecha);
				
				if (campoIzquierda == null || campoIzquierda.isEmpty() || campoIzquierda.equalsIgnoreCase("null")) {
					ClaseJPA claseDerecha = this.mappingAliasClase.get(referenciaDerecha);
					if (claseDerecha.getAtributoID().equalsIgnoreCase(campoDerecha)) {
						//System.out.println("YOU GOT IT");
						// buscamos algo de la primera clase (que tiene null) de la segunda clase
						ClaseJPA claseIzquierda = this.mappingAliasClase.get(referenciaIzquierda);
						//System.out.println("clase izq:" + claseIzquierda);
						//System.out.println("atributo refe:" + claseIzquierda.getAtributoWithJoinColumn(claseDerecha.getAtributoID()));
						claseIzquierda.getAtributoWithJoinColumn(claseDerecha.getAtributoID());
						//selectEncontrados.add(referenciaIzquierda + "." + claseIzquierda.getAtributoWithJoinColumn(claseDerecha.getAtributoID()));
						joinEncontrados.add("JOIN " + referenciaIzquierda + "." + claseIzquierda.getAtributoWithJoinColumn(claseDerecha.getAtributoID()) + " " + referenciaDerecha);
					} 

					//System.out.println("id:" + 	claseDerecha.getAtributoID());
					//System.out.println("NULL");
				} else {
					
					String rawWhereString = join.getCondicionRaw();
					List<String> condionesToChange = new LinkedList<>();
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

					// Ponemos primero los que tienen alias o referencia a su tabla
					condionesToChange.sort((s1, s2) -> s1.contains(".") && !s2.contains(".") ? -1 : 1);
					// condionesToChange =
					// condionesToChange.stream().distinct().collect(Collectors.toList());

					// System.out.println(condionesToChange);
					String treatedWhereString = rawWhereString.toUpperCase();
					for (String c : condionesToChange) {
						if (c.startsWith("'") || c.startsWith("(")) {
							treatedWhereString = treatedWhereString.replace(c.toUpperCase(), c);
							continue;
						}
						String condicionParseada = this.parseCondition(c);
						if (c.equals(condicionParseada)) {
							continue;
						}
						// System.out.println("reemplazamos: " + condicion + ", con: " +
						// this.parseCondition(condicion));
						treatedWhereString = treatedWhereString.replace(c, this.parseCondition(c));

					}
					
					
					joinEncontrados.add("JOIN ON " + treatedWhereString);
				}
				
			}
			
			
		}
		
		
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
				//System.out.println(aliasNombreReal);
				if (aliasNombreReal != null) {
					resultado += referencia + "." + aliasNombreReal.getValue();
				} else {
					resultado += referencia + ".null";
				}
				//System.out.println(resultado);
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

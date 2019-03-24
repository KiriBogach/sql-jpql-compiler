package transformer.service;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import elements.CampoFrom;
import elements.CampoSelect;
import transformer.api.GSP_API;
import utils.classfinder.ClassFinder;

public class GSP_JPQLTransformer extends JPQLTransformerBase {
	
	private static String DIFERENCIADOR = "_";
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

		String jqpl = "SELECT ";

		HashMap<String, String> fromEncontrados = new HashMap<>();
		String fromQuery = "FROM ";
		for (CampoFrom campoFrom : this.from.getTables()) {
			System.out.println("buscada: " + campoFrom.getNombre());
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
				try {
					clase.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				//System.out.println(clase.getSimpleName());
			}
			
			System.out.println("clase encontrada: "  + claseBuscada.getSimpleName());
			fromQuery += claseBuscada.getSimpleName();
			
			if (campoFrom.getAlias() == null || campoFrom.getAlias().equals("")) {
				fromQuery += " " + alias + DIFERENCIADOR;
				fromEncontrados.put(claseBuscada.getSimpleName(), alias + DIFERENCIADOR);
				alias++;
			} else {
				fromQuery += " " + campoFrom.getAlias();
				fromEncontrados.put(claseBuscada.getSimpleName(), campoFrom.getAlias());
			}
			
			

		}

		System.out.println(fromQuery);
		System.out.println(fromEncontrados);
		


		Collection<CampoSelect> selectFields = this.select.getFields();
		for (CampoSelect campoSelect : selectFields) {
			System.out.println("camposelect " +campoSelect);
			if (fromEncontrados.containsValue(campoSelect.getAlias())) {
				Set<String> keys = fromEncontrados.keySet();
				for (String key : keys) {
					if (fromEncontrados.get(key).equals(campoSelect.getAlias())) {
						System.out.println("alias.campo: " + fromEncontrados.get(key) + "." + campoSelect.getNombre());
						jqpl += fromEncontrados.get(key) + "." + campoSelect.getNombre() + " ";
					}
				}
			} else {
				System.out.println("alias.campo: " + fromEncontrados.values().toArray()[0]);
				jqpl += fromEncontrados.values().toArray()[0] + " ";
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
		
		return jqpl + fromQuery;
	}

}

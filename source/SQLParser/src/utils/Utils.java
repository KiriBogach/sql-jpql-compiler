package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
	
	public final static boolean CAMEL_CASE_FORMAT = false;
	
	public static boolean isEmpty(String str) {
		return str == null || str.isEmpty() || str.equalsIgnoreCase("null");
	}

	public static String getFieldTable(String field) {
		int indicePunto = field.indexOf(".");
		if (indicePunto == -1) {
			return field;
		}
		return field.substring(0, indicePunto);
	}
	
	public static String getRawFieldTable(String field) {
		int indicePunto = field.indexOf(".");
		if (indicePunto == -1) {
			return null;
		}
		return field.substring(0, indicePunto);
	}

	public static String getOnlyFieldTable(String field) {
		int indicePunto = field.indexOf(".");
		if (indicePunto == -1) {
			return null;
		}
		return field.substring(0, indicePunto);
	}

	public static String getRawColumnValue(String nombreAtributoBuscado) {
		int indicePunto = nombreAtributoBuscado.lastIndexOf(".");
		if (indicePunto == -1) {
			return nombreAtributoBuscado;
		}
		return nombreAtributoBuscado.substring(indicePunto + 1, nombreAtributoBuscado.length());
	}

	public static String reemplazarYNormalizar(String condiciones, String cadenaBuscada) {
		cadenaBuscada = cadenaBuscada.replace(" ", "\\s*");
		return condiciones.replaceAll("(AND|OR|XOR|NOT)?\\s*" + cadenaBuscada + "\\s*(AND|OR|XOR|NOT)?", "");
	}
	public static String reemplazarSinLiterales(String cadena, String sustitucion, String sustituto) {
		// Expresión regular que coge todas las coincidencias menos las que tengan 
		// comillas como 'sustitucion' o .sustitucion.
		
		/* El caso de .sustitucion es para no realizar sustituciones consecutivas, por ejemplo:
		 * sustituimos a.credit_data -> a.creditData
		 * ... luego viene otra sustitución por otro lado del campo b.credit_data -> b.creditData
		 * ... de este modo no volvemos a sustituir el primer caso.
		 */
		// Para escapar caracteres en la expresión regular como '(', ')'
		String sustitucionNormalizada = Pattern.quote(sustitucion);
		
		// No cosideramos lo que tenga delante:
		// ' . _
		
		// Construimos la expresión regular
		String re = "(?<!')(?<!\\.)(?<!\\_)" + sustitucionNormalizada + "(?!')"; 
		
		//System.out.println("cadena: " + cadena);
		//System.out.println("re: " + re);
		
		return cadena.replaceAll(re, sustituto);
	}
	
	public static String getJPAFormat(String entrada) {
		if (!CAMEL_CASE_FORMAT) {
			return entrada;
		}
		
		return transformToJPAFormat(entrada);
	}
	
	public static String transformToJPAFormat(String entrada) {
		String salida = entrada.toLowerCase();
		while (salida.contains("_")) {
			salida = salida.replaceFirst("_[a-z]",
					String.valueOf(Character.toUpperCase(salida.charAt(salida.indexOf("_") + 1))));
		}
		return salida;
	}
	
	public static boolean isNumeric(String str) {
		return str.matches("-?\\d+(\\.\\d+)?"); // match a number with optional '-' and decimal.
	}
	
	public static boolean isSubquery(String entrada) {
		String busqueda = "SELECT";
		
		// (?i) --> case insensitive
		String re = "(?<!')(?i)" + busqueda + "(?!')";
		
		Pattern pattern = Pattern.compile(re);
	    Matcher matcher = pattern.matcher(entrada);

		return matcher.find();
	}
	
	public static String addParentesis(String entrada) {
		return "(" + entrada + ")";
	}
	
}

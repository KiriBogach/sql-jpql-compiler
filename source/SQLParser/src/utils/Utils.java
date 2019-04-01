package utils;

public class Utils {

	public static String getFieldTable(String field) {
		int indicePunto = field.indexOf(".");
		if (indicePunto == -1) {
			return field;
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
		int indicePunto = nombreAtributoBuscado.indexOf(".");
		if (indicePunto == -1) {
			return nombreAtributoBuscado;
		}
		return nombreAtributoBuscado.substring(indicePunto + 1, nombreAtributoBuscado.length());
	}

	public static String reemplazarYNormalizar(String condiciones, String cadenaBuscada) {
		cadenaBuscada = cadenaBuscada.replace(" ", "\\s*");
		return condiciones.replaceAll("(AND|OR|XOR|NOT)?\\s*" + cadenaBuscada + "\\s*(AND|OR|XOR|NOT)?", "");
	}
}

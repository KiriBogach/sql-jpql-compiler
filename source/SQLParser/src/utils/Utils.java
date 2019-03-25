package utils;

public class Utils {

	public static String getFieldTable(String field) {
		int indicePunto = field.indexOf(".");
		if (indicePunto == -1) {
			return field;
		}
		return field.substring(0, indicePunto);
	}
	
	public static String eliminarReferenciaTabla(String nombreAtributoBuscado) {
		int indicePunto = nombreAtributoBuscado.indexOf(".");
		if (indicePunto == -1) {
			return nombreAtributoBuscado;
		}
		return nombreAtributoBuscado.substring(indicePunto + 1, nombreAtributoBuscado.length());
	}
}

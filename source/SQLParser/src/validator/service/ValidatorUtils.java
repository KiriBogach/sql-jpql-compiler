package validator.service;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ValidatorUtils {

	/*
	 * Equivalencias controladas: Si viene un campo COUNT(*) es igual a COUNT(a) -->
	 * entidad Introducir las equivalencias controladas en la siguiente lista
	 */
	public static final List<String> listaEquivalenciaCampos = Arrays.asList(new String[] { "COUNT(" });

	public static boolean sameValuesWithPreconditions(List<Map<String, Object>> a, List<Map<String, Object>> b) {
		for (Map<String, Object> filaA : a) {
			for (Map.Entry<String, Object> campoA : filaA.entrySet()) {
				if (!ValidatorUtils.findInMap(b, campoA)) {
					return false;
				}
			}
		}

		return true;
	}

	/*
	 * Problema: A: [ 'nombre': pedro, 'apellido': antonio ]
	 * 
	 * B: [ 'nombre': antonio, 'apellido': pedro ]
	 */
	public static boolean sameValues(List<Map<String, Object>> a, List<Map<String, Object>> b) {
		for (Map<String, Object> filaA : a) {
			Collection<?> valuesA = filaA.values();
			boolean found = false;

			for (Map<String, Object> filaB : b) {
				Collection<?> valuesB = filaB.values();
				if (!valuesA.equals(valuesB)) {
					found = true;
					break;
				}
			}

			if (!found) {
				return false;
			}
		}

		for (Map<String, Object> filaB : b) {
			Collection<?> valuesB = filaB.values();
			boolean found = false;

			for (Map<String, Object> filaA : a) {
				Collection<?> valuesA = filaA.values();
				if (!valuesA.equals(valuesB)) {
					found = true;
					break;
				}
			}

			if (!found) {
				return false;
			}
		}

		return true;
	}

	private static boolean startWithAnyEquivalenciaCampos(String campo) {
		for (String equivalencia : listaEquivalenciaCampos) {
			if (campo.startsWith(equivalencia)) {
				return true;
			}
		}

		return false;
	}

	private static boolean findInMap(List<Map<String, Object>> map, Map.Entry<String, Object> entry) {
		String nombreCampoBuscado = entry.getKey();
		Object valorCampoBuscado = entry.getValue();

		for (Map<String, Object> fila : map) {

			for (Map.Entry<String, Object> campo : fila.entrySet()) {
				String nombreCampo = campo.getKey();
				Object valorCampo = campo.getValue();

				boolean isSameValue = valorCampo.equals(valorCampoBuscado);

				if (nombreCampo.equals(nombreCampoBuscado)) {
					if (isSameValue) {
						// Tiene el mismo nombre de campo y coincide el valor
						return true;
					}
				} else if (startWithAnyEquivalenciaCampos(nombreCampo)
						&& startWithAnyEquivalenciaCampos(nombreCampoBuscado)) {
					if (isSameValue) {
						// COUNT(*) vs COUNT(entidad) y coincide el valor
						return true;
					}
				}
			}
		}

		return false;
	}

	public static String getNowFileStatement() {
		DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
		return dateFormat.format(new Date());
	}

	public static void createFolder(String folder) {
		new File(folder).mkdirs();
	}
}

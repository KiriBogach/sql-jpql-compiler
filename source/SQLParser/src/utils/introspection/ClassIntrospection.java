package utils.introspection;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import utils.Utils;

public class ClassIntrospection {

	private static final String DB_MODEL_PACKAGE = "database.model";
	private static final char PKG_SEPARATOR = '.';
	private static final char DIR_SEPARATOR = '/';
	private static final String CLASS_FILE_SUFFIX = ".class";
	private static final String BAD_PACKAGE_ERROR = "Unable to get resources from path '%s'. Are you sure the package '%s' exists?";

	public static ClaseJPA getClaseJPA(Class<?> clase) {
		ClaseJPA claseJPA = new ClaseJPA();
		
		for (Field field : clase.getDeclaredFields()) {
			Class<?> type = field.getType();
			String name = field.getName();
			Annotation[] annotations = field.getDeclaredAnnotations();
			claseJPA.addAtributo(name, type, annotations);
		}
		
		return claseJPA;
	}

	/*
	 * Busca la clase cuya notación JPA tenga como nombre de tabla el parámetro
	 * 'nombreClaseBuscada':
	 * 
	 * @Table(name = "customers")
	 */
	public static Class<?> getJPATableNameAnnotation(String nombreClaseBuscada) {
		List<Class<?>> clases = ClassIntrospection.find(DB_MODEL_PACKAGE);
		for (Class<?> clase : clases) {
			Annotation[] anotaciones = clase.getAnnotations();
			for (Annotation annotation : anotaciones) {
				int posNombre = annotation.toString().indexOf("Table(name=");
				int posNombreFin = annotation.toString().indexOf(", schema=");
				if (posNombre == -1 || posNombreFin == -1) {
					continue;
				}
				String nombreColumna = annotation.toString().substring(posNombre + "Table(name=".length(),
						posNombreFin);
				if (nombreClaseBuscada.equalsIgnoreCase(nombreColumna)) {
					return clase;
				}
			}
		}
		return null;
	}

	/*
	 * Busca el nombre exacto del atributo JPA de una clase
	 */
	public static String getFieldName(Class<?> clase, String nombreAtributoBuscado) {
		nombreAtributoBuscado = Utils.eliminarReferenciaTabla(nombreAtributoBuscado);
		for (Field f : clase.getDeclaredFields()) {
			if (f.getName().equalsIgnoreCase(nombreAtributoBuscado)) {
				return f.getName();
			}
		}
		return null;
	}

	public static List<Class<?>> find(String scannedPackage) {
		String scannedPath = scannedPackage.replace(PKG_SEPARATOR, DIR_SEPARATOR);
		URL scannedUrl = Thread.currentThread().getContextClassLoader().getResource(scannedPath);
		if (scannedUrl == null) {
			throw new IllegalArgumentException(String.format(BAD_PACKAGE_ERROR, scannedPath, scannedPackage));
		}
		File scannedDir = new File(scannedUrl.getFile());
		List<Class<?>> classes = new ArrayList<Class<?>>();
		for (File file : scannedDir.listFiles()) {
			classes.addAll(find(file, scannedPackage));
		}
		return classes;
	}

	private static List<Class<?>> find(File file, String scannedPackage) {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		String resource = scannedPackage + PKG_SEPARATOR + file.getName();
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				classes.addAll(find(child, resource));
			}
		} else if (resource.endsWith(CLASS_FILE_SUFFIX)) {
			int endIndex = resource.length() - CLASS_FILE_SUFFIX.length();
			String className = resource.substring(0, endIndex);
			try {
				classes.add(Class.forName(className));
			} catch (ClassNotFoundException ignore) {
			}
		}
		return classes;
	}

}

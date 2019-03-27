package utils.introspection;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class ClaseJPA {
	private Map<String, AtributoClaseJPA> atributos;
	
	public ClaseJPA() {
		this.atributos = new HashMap<>();
	}
	
	public String getRealName(String nombre) {
		for (String atributo : this.atributos.keySet()) {
			if (atributo.equalsIgnoreCase(nombre)) {
				return atributo;
			}
		}
		return null;
	}
	
	public void addAtributo(String nombre, Class<?> tipo, Annotation[] annotations) {
		this.atributos.put(nombre, new AtributoClaseJPA(tipo, annotations));
	}
	
	public Class<?> getTipoAtributo(String nombre) {
		return this.atributos.get(nombre).getTipo();
	}
	
	public Annotation[] getAnnotationsAtributo(String nombre) {
		return this.atributos.get(nombre).getAnnotations();
	}
	
	public boolean isAtributo(String nombre) {
		return this.atributos.containsKey(nombre);
	}
	
	public void printMap() {
		System.out.println(this.atributos);
	}
	
	private class AtributoClaseJPA {
		private Class<?> tipo;
		private Annotation[] annotations;
		
		public AtributoClaseJPA(Class<?> tipo, Annotation[] annotations) {
			this.tipo = tipo;
			this.annotations = annotations;
		}
		
		public Class<?> getTipo() {
			return tipo;
		}
		
		public Annotation[] getAnnotations() {
			return annotations;
		}

		@Override
		public String toString() {
			return "AtributoClaseJPA [tipo=" + tipo + ", annotations=" + Arrays.toString(annotations) + "]";
		}
		
	}

}

package utils.introspection;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

public class ClaseJPA {
	
	private String nombreJPA;
	private Annotation[] classAnnotations;
	private Table tableAnnotation;
	
	private Map<String, AtributoClaseJPA> atributos;

	public ClaseJPA(String nombre, Annotation[] classAnnotations) {
		this.nombreJPA = nombre;
		this.atributos = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		this.classAnnotations = classAnnotations;
		this.parseAnnotations();
	}
	
	private void parseAnnotations() {
		for (Annotation annotation : classAnnotations) {
			if (annotation instanceof Table) {
				this.tableAnnotation = (Table) annotation;
			}
		}
	}
	
	public String getDataBaseName() {
		if (this.tableAnnotation == null) {
			return null;
		}
		return this.tableAnnotation.name();
	}
	
	public String getNombreJPA() {
		return nombreJPA;
	}

	public String getAttributeRealName(String nombre) {
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

	public boolean hasAtributo(String nombre) {
		return this.atributos.containsKey(nombre);
	}
	
	public String getAtributoWithJoinColumn(String nombre) {
		for (Map.Entry<String, AtributoClaseJPA> atributo : atributos.entrySet()) {
			JoinColumn joinColumn = atributo.getValue().joinColumn;
			if (joinColumn != null && joinColumn.name().equalsIgnoreCase(nombre)) {
				return atributo.getKey();
			}
		}
		return null;
	}
	
	public String getAtributoID() {
		for (Map.Entry<String, AtributoClaseJPA> atributo : atributos.entrySet()) {
			Id id = atributo.getValue().id;
			if (id != null) {
				return atributo.getKey();
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "ClaseJPA [nombre=" + nombreJPA + ", atributos=" + atributos + "]";
	}

	private class AtributoClaseJPA {
		private Class<?> tipo;
		private Annotation[] annotations;
		private ManyToOne manyToOne;
		private JoinColumn joinColumn;
		private OneToMany oneToMany;
		private Id id;

		public AtributoClaseJPA(Class<?> tipo, Annotation[] annotations) {
			this.tipo = tipo;
			this.annotations = annotations;
			this.parseAnnotations();
		}

		private void parseAnnotations() {
			for (Annotation annotation : annotations) {
				if (annotation instanceof JoinColumn) {
					this.joinColumn = (JoinColumn) annotation;
				} else if (annotation instanceof ManyToOne) {
					this.manyToOne = (ManyToOne) annotation;
				} else if (annotation instanceof OneToMany) {
					this.oneToMany = (OneToMany) annotation;
				} else if (annotation instanceof Id) {
					this.id = (Id) annotation;
				}
			}
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

package utils.introspection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import utils.Utils;

public class ClaseJPA {

	private Class<?> claseReferenciada;
	private Map<String, AtributoClaseJPA> atributos;
	private Annotation[] classAnnotations;
	private String nombreJPA;
	private Table tableAnnotation;

	public ClaseJPA(Class<?> entidadJPA) {
		this.claseReferenciada = entidadJPA;
		this.nombreJPA = entidadJPA.getSimpleName();
		this.classAnnotations = entidadJPA.getAnnotations();
		this.atributos = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		this.parseAnnotations();
	}

	private void parseAnnotations() {
		for (Annotation annotation : classAnnotations) {
			if (annotation instanceof Table) {
				this.tableAnnotation = (Table) annotation;
			}
		}
	}

	public Class<?> getClaseReferenciada() {
		return claseReferenciada;
	}

	public void addAtributo(Field field) { // String nombre, Class<?> tipo, Annotation[] annotations) {
		this.atributos.put(field.getName(), new AtributoClaseJPA(field));
	}

	public AtributoClaseJPA getAtribuo(String name) {
		return this.atributos.get(name);
	}

	public Annotation[] getAnnotationsAtributo(String nombre) {
		return this.atributos.get(nombre).getAnnotations();
	}

	public String getAtributoID() {
		for (Map.Entry<String, AtributoClaseJPA> atributo : atributos.entrySet()) {
			Id id = atributo.getValue().getId();
			if (id != null) {
				return atributo.getKey();
			}
		}
		return null;
	}

	public AtributoClaseJPA getAtributoWithJoinColumn(String nombre) {
		for (Map.Entry<String, AtributoClaseJPA> atributo : atributos.entrySet()) {
			JoinColumn joinColumn = atributo.getValue().joinColumn;
			if (joinColumn != null && joinColumn.name().equalsIgnoreCase(nombre)) {
				return atributo.getValue();
			}
		}
		return null;
	}

	public AtributoClaseJPA getAtributoOneToOne(String nombre) {
		for (Map.Entry<String, AtributoClaseJPA> atributo : atributos.entrySet()) {
			JoinColumn joinColumn = atributo.getValue().joinColumn;
			OneToOne oneToOne = atributo.getValue().oneToOne;
			if (joinColumn != null && joinColumn.name().equalsIgnoreCase(nombre)) {
				return atributo.getValue();
			} else if (oneToOne != null && oneToOne.mappedBy().equalsIgnoreCase(nombre)) {
				return atributo.getValue();
			}
		}
		return null;
	}
	
	public AtributoClaseJPA getAtributoManyToOne(String nombre) {
		for (Map.Entry<String, AtributoClaseJPA> atributo : atributos.entrySet()) {
			JoinColumn joinColumn = atributo.getValue().joinColumn;
			//ManyToOne manyToOne = atributo.getValue().manyToOne;
			if (joinColumn != null && Utils.transformToJPAFormat(joinColumn.name()).equalsIgnoreCase(nombre)) {
				return atributo.getValue();
			}
		}
		return null;
	}

	public AtributoClaseJPA getAtributoMappedByOneToMany(String nombre) {
		for (Map.Entry<String, AtributoClaseJPA> atributo : atributos.entrySet()) {
			OneToMany oneToMany = atributo.getValue().getOneToMany();

			// Buscamos el mappedBy con ese nombre
			if (oneToMany != null && oneToMany.mappedBy().equalsIgnoreCase(nombre)) {
				return atributo.getValue();
			}
		}
		return null;
	}

	public AtributoClaseJPA getAtributoMappedByOneToOne(String nombre) {
		for (Map.Entry<String, AtributoClaseJPA> atributo : atributos.entrySet()) {

			// Si el propio nombre es el atributo lo devolvemos
			if (atributo.getKey().equalsIgnoreCase(nombre)) {
				return atributo.getValue();
			}

			// Buscamos el mappedBy con ese nombre
			OneToOne oneToOne = atributo.getValue().getOneToOne();
			if (oneToOne != null && oneToOne.mappedBy().equalsIgnoreCase(nombre)) {
				return atributo.getValue();
			}
		}
		return null;
	}

	public String getAttributeRealName(String nombre) {
		for (Map.Entry<String, AtributoClaseJPA> atributo : atributos.entrySet()) {
			String key = atributo.getKey();
			if (key.equalsIgnoreCase(nombre)) {
				return key;
			}

			// Miramos @Column(name="nombre", length=9)
			Column column = atributo.getValue().getColumn();
			if (column != null && column.name() != null && (column.name().equalsIgnoreCase(nombre))) {
				return key;
			}
		}

		return null;
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

	public Class<?> getTipoAtributo(String nombre) {
		return this.atributos.get(nombre).getTipo();
	}

	public boolean hasAtributo(String nombre) {
		return this.atributos.containsKey(nombre);
	}

	@Override
	public String toString() {
		return "ClaseJPA [nombre=" + nombreJPA + ", atributos=" + atributos + "]";
	}

	public class AtributoClaseJPA {
		private String nombre;
		private Annotation[] annotations;
		private Id id;
		private Column column;
		private JoinColumn joinColumn;
		private ManyToOne manyToOne;
		private OneToMany oneToMany;
		private OneToOne oneToOne;
		private Class<?> tipo;

		public AtributoClaseJPA(Field field) {
			this.nombre = field.getName();
			this.tipo = field.getType();
			this.annotations = field.getDeclaredAnnotations();
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
				} else if (annotation instanceof OneToOne) {
					this.oneToOne = (OneToOne) annotation;
				} else if (annotation instanceof Id) {
					this.id = (Id) annotation;
				} else if (annotation instanceof Column) {
					this.column = (Column) annotation;
				}
			}
		}

		public String getNombre() {
			return nombre;
		}

		public Annotation[] getAnnotations() {
			return annotations;
		}

		public Id getId() {
			return id;
		}

		public JoinColumn getJoinColumn() {
			return joinColumn;
		}

		public ManyToOne getManyToOne() {
			return manyToOne;
		}

		public OneToMany getOneToMany() {
			return oneToMany;
		}

		public OneToOne getOneToOne() {
			return oneToOne;
		}

		public Column getColumn() {
			return column;
		}

		public Class<?> getTipo() {
			return tipo;
		}

		public void setTipo(Class<?> tipo) {
			this.tipo = tipo;
		}

		@Override
		public String toString() {
			return "AtributoClaseJPA [tipo=" + tipo + ", annotations=" + Arrays.toString(annotations) + "]";
		}

	}

}

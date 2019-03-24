package elements;

public abstract class CampoBase implements Campo {
	
	private String nombre;
	private String alias;
	
	public CampoBase() {
	}
	
	public CampoBase(String nombre, String alias) {
		this.nombre = nombre;
		this.alias = alias;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[nombre=" + nombre + ", alias=" + alias + "]";
	}
	
	

}

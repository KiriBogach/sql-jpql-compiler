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

	// Devuelve 'c' de c.nombre
	public String getTableReference() {
		int indicePunto = this.nombre.indexOf(".");
		if (indicePunto == -1) {
			return null;
		}
		return this.nombre.substring(0, indicePunto);
	}

	// Devuelve 'nombre' de c.nombre
	public String getRawNombre() {
		int indicePunto = this.nombre.indexOf(".");
		if (indicePunto == -1) {
			return this.nombre;
		}
		return this.nombre.substring(indicePunto + 1, this.nombre.length());
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[nombre=" + nombre + ", alias=" + alias + "]";
	}

}

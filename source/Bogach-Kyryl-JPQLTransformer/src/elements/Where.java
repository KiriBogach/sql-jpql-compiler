package elements;

import java.util.Set;

public class Where {

	private Set<Condicion> condiciones;
	private String condicionRaw;

	public Where() {
	}

	public Where(Set<Condicion> condiciones) {
		this.condiciones = condiciones;
	}

	public Set<Condicion> getCondiciones() {
		return condiciones;
	}

	public void setCondiciones(Set<Condicion> condiciones) {
		this.condiciones = condiciones;
	}

	public String getCondicionRaw() {
		return condicionRaw;
	}

	public void setCondicionRaw(String condicionRaw) {
		this.condicionRaw = condicionRaw;
	}

	@Override
	public String toString() {
		return "Where [condiciones=" + condiciones + "]";
	}

}

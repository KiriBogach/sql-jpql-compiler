package elements;

import java.util.Set;

public class Having {
	private Set<Condicion> condiciones;
	private String condicionRaw;

	public Having() {
	}

	public Having(Set<Condicion> condiciones) {
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
		return "Having [condiciones=" + condiciones + ", condicionRaw=" + condicionRaw + "]";
	}

}

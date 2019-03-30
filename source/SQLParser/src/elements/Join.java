package elements;

import java.util.Set;

public class Join {
	
	private CampoFrom table;
	private TipoJoin tipo;
	private Set<Condicion> condiciones;
	private String condicionRaw;
	
	public Join(String nombre, String alias, String tipo, String condicionRaw, Set<Condicion> condiciones) {
		this.table = new CampoFrom(nombre, alias);
		this.tipo = TipoJoin.valueOf(tipo.toUpperCase());
		this.condicionRaw = condicionRaw;
		this.condiciones = condiciones;
	}

	public CampoFrom getTable() {
		return table;
	}

	public void setTable(CampoFrom table) {
		this.table = table;
	}

	public TipoJoin getTipo() {
		return tipo;
	}

	public void setTipo(TipoJoin tipo) {
		this.tipo = tipo;
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
		return "Join [table=" + table + ", tipo=" + tipo + ", condiciones=" + condiciones + "]";
	}
	
	
	
}

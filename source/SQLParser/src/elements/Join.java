package elements;

public class Join {
	
	private CampoFrom table;
	private TipoJoin tipo;
	private ArbolCondicion condiciones;
	
	public Join(String nombre, String alias, String tipo, ArbolCondicion condiciones) {
		this.table = new CampoFrom(nombre, alias);
		this.tipo = TipoJoin.valueOf(tipo.toUpperCase());
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

	public ArbolCondicion getCondiciones() {
		return condiciones;
	}

	public void setCondiciones(ArbolCondicion condiciones) {
		this.condiciones = condiciones;
	}

	@Override
	public String toString() {
		return "Join [table=" + table + ", tipo=" + tipo + ", condiciones=" + condiciones + "]";
	}
	
	
	
}

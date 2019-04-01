package elements;

import java.util.LinkedList;
import java.util.Set;

public class From {

	private LinkedList<CampoFrom> tables;
	private LinkedList<Join> joins;

	public From() {
		this.tables = new LinkedList<>();
		this.joins = new LinkedList<>();
	}

	public void addTable(String campo, String alias) {
		this.tables.add(new CampoFrom(campo, alias));
	}
	
	public void addJoin(String campo, String alias, String tipo, String condicionRaw, Set<Condicion> condiciones) {
		this.joins.add(new Join(campo, alias, tipo, condicionRaw, condiciones));
	}
	
	public LinkedList<CampoFrom> getTables() {
		return tables;
	}
	
	public LinkedList<Join> getJoins() {
		return joins;
	}

	@Override
	public String toString() {
		return "From [tables=" + tables + ", joins=" + joins + "]";
	}

	
}

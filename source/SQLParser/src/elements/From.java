package elements;

import java.util.Collection;
import java.util.LinkedList;

public class From {

	private Collection<CampoFrom> tables;
	private Collection<Join> joins;

	public From() {
		this.tables = new LinkedList<>();
		this.joins = new LinkedList<>();
	}

	public void addTable(String campo, String alias) {
		this.tables.add(new CampoFrom(campo, alias));
	}
	
	public void addJoin(String campo, String alias, String tipo, ArbolCondicion condiciones) {
		this.joins.add(new Join(campo, alias, tipo, condiciones));
	}
	
	public Collection<CampoFrom> getTables() {
		return tables;
	}
	
	public Collection<Join> getJoins() {
		return joins;
	}

	@Override
	public String toString() {
		return "From [tables=" + tables + ", joins=" + joins + "]";
	}

	
}

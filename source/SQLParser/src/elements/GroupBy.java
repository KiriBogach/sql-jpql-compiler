package elements;

import java.util.Collection;
import java.util.LinkedList;

public class GroupBy {

	private String condicionRaw;
	private Collection<CampoSelect> fields;
	private Having having;

	public GroupBy() {
		this.fields = new LinkedList<>();
	}

	public String getCondicionRaw() {
		return condicionRaw;
	}

	public void setCondicionRaw(String condicionRaw) {
		this.condicionRaw = condicionRaw;
	}

	public void addField(String campo, String alias) {
		this.fields.add(new CampoSelect(campo, alias));
	}

	public Collection<CampoSelect> getFields() {
		return fields;
	}

	public void setFields(Collection<CampoSelect> fiels) {
		this.fields = fiels;
	}
	
	public Having getHaving() {
		return having;
	}
	
	public void setHaving(Having having) {
		this.having = having;
	}

	@Override
	public String toString() {
		return "GroupBy [condicionRaw=" + condicionRaw + ", fields=" + fields + ", having=" + having + "]";
	}

	
}

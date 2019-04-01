package elements;

import java.util.Collection;
import java.util.LinkedList;

public class OrderBy {
	private String condicionRaw;
	private Collection<CampoSelect> fields;

	public OrderBy() {
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

	@Override
	public String toString() {
		return "OrderBy [condicionRaw=" + condicionRaw + ", fields=" + fields + "]";
	}

}

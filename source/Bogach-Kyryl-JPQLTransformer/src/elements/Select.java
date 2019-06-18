package elements;

import java.util.Collection;
import java.util.LinkedList;

public class Select {

	private Collection<CampoSelect> fields;

	public Select() {
		this.fields = new LinkedList<>();
	}

	public void addField(String campo, String alias) {
		this.fields.add(new CampoSelect(campo, alias));
	}
	
	public Collection<String> getFieldNames() {
		Collection<String> campos = new LinkedList<>();
		for (CampoSelect campoSelect : fields) {
			campos.add(campoSelect.getNombre());
		}
		return campos;
	}
	
	public Collection<CampoSelect> getFields() {
		return fields;
	}

	@Override
	public String toString() {
		return "Select [fields=" + fields + "]";
	}
}

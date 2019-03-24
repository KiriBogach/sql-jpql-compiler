package elements;

public class Where {

	private ArbolCondicion condiciones;
	
	public Where() {
	}

	public Where(ArbolCondicion condiciones) {
		this.condiciones = condiciones;
	}


	public ArbolCondicion getCondiciones() {
		return condiciones;
	}

	public void setCondiciones(ArbolCondicion condiciones) {
		this.condiciones = condiciones;
	}


	@Override
	public String toString() {
		return "Where [condiciones=" + condiciones + "]";
	}
	
	

}

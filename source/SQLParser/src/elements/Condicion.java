package elements;

public class Condicion {
	private String izquierda;
	private String operador;
	private String derecha;

	public Condicion() {
	}

	public Condicion(String izquierda, String operador, String derecha) {
		this.izquierda = izquierda;
		this.operador = operador;
		this.derecha = derecha;
	}

	public String getIzquierda() {
		return izquierda;
	}

	public void setIzquierda(String izquierda) {
		this.izquierda = izquierda;
	}

	public String getOperador() {
		return operador;
	}

	public void setOperador(String operador) {
		this.operador = operador;
	}

	public String getDerecha() {
		return derecha;
	}

	public void setDerecha(String derecha) {
		this.derecha = derecha;
	}
	
	public String getRawCondition() {
		return this.izquierda + this.operador + this.derecha;
	}
	
	public String getRawConditionRE() {
		return this.izquierda + "\\s*" + this.operador + "\\s*" + this.derecha;
	}

	@Override
	public String toString() {
		return "Condicion [izquierda=" + izquierda + ", operador=" + operador + ", derecha=" + derecha + "]";
	}

}

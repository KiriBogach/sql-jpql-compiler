package elements;

public class Condicion {
	private String izquierda;
	private String operador;
	private String derecha;
	private boolean parentesis;
	private boolean root;
	
	public Condicion() {
	}

	public Condicion(String izquierda, String operador, String derecha) {
		this.izquierda = izquierda;
		this.operador = operador;
		this.derecha = derecha;
		this.parentesis = false;
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
	
	public boolean isParentesis() {
		return parentesis;
	}

	public void setParentesis(boolean parentesis) {
		this.parentesis = parentesis;
	}
	
	public boolean isRoot() {
		return root;
	}

	public void setRoot(boolean root) {
		this.root = root;
	}

	@Override
	public String toString() {
		return "Condicion [izquierda=" + izquierda + ", operador=" + operador + ", derecha=" + derecha + ", parentesis="
				+ parentesis + ", root=" + root + "]";
	}

	

}

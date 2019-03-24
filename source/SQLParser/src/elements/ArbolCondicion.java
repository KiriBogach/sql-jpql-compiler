package elements;

import java.util.ArrayList;
import java.util.List;

public class ArbolCondicion {
	public static boolean DEBUG = false;
	private Node<Condicion> ramaIzquierda; // Rama izquierda desde abajo
	private Node<Condicion> raiz; // Rama izquierda desde abajo
	private Node<Condicion> ramaDerecha; // Rama derecha desde abajo
	private boolean izq = true;

	public ArbolCondicion() {
		ramaIzquierda = new Node<Condicion>();
		raiz = new Node<Condicion>();
		ramaDerecha = new Node<Condicion>();
	}

	public void build(Condicion data) {
		if (DEBUG) System.out.println("entro " + data);

		Node<Condicion> rama = (izq) ? this.ramaIzquierda : this.ramaDerecha;

		if (izq) {
			if (DEBUG) System.out.println("rama izq");
		} else {
			if (DEBUG) System.out.println("rama der");
		}

		if (DEBUG) System.out.println("llega: " + data);
		if (rama.data == null) {
			rama.data = data;
			return;
		}

		Node<Condicion> ultimoHijo = rama;
		Node<Condicion> parent = rama.parent;
		while (parent != null && parent.children.size() >= 2) {
			ultimoHijo = parent;
			parent = parent.parent;
		}

		if (parent == null) {
			if (DEBUG) System.out.println("padre nulo");
			parent = new Node<Condicion>();
			parent.data = data;
			parent.children.add(ultimoHijo);
			ultimoHijo.parent = parent;
		} else {
			if (DEBUG) System.out.println("padre con valor");
			Node<Condicion> nuevo = new Node<Condicion>();
			nuevo.parent = parent;
			nuevo.data = data;
			parent.children.add(nuevo);
		}

		if (data.isRoot()) {
			raiz.data = data;
			raiz.children.add(ramaIzquierda);
			raiz.children.add(ramaDerecha);
			izq = false;
		}

	}

	public static class Node<Condicion> {
		private Condicion data;
		private Node<Condicion> parent;
		private List<Node<Condicion>> children;
		
		public Node() {
			this.children = new ArrayList<>();
		}

		@Override
		public String toString() {
			return "Node [data=" + data + "]";
		}

	}

	@Override
	public String toString() {
		String pf = "\n";
		Node<Condicion> parentIzq = ramaIzquierda;
		while (parentIzq != null) {
			if (parentIzq.data.isParentesis()) {
				pf += "()";
			}
			pf += parentIzq;
			if (parentIzq.children != null && parentIzq.children.size() > 0) {
				pf += "hijos: \t" + parentIzq.children.get(0);
				if (parentIzq.children.size() > 1) {
					pf += "\t" + parentIzq.children.get(1);
				}
			}
			parentIzq = parentIzq.parent;
		}

		if (ramaDerecha.data == null) {
			return pf; // no hay rama derecha
		}
		pf +=  "**";

		Node<Condicion> parentDer = ramaDerecha;
		while (parentDer != null) {
			if (parentDer.data != null && parentDer.data.isParentesis()) {
				pf += "()";
			}
			pf += parentDer;
			if (parentDer.children != null && parentDer.children.size() > 0) {
				pf += "hijos: \t" + parentDer.children.get(0);
				if (parentDer.children.size() > 1) {
					pf += "\t" + parentDer.children.get(1);
				}
			}
			parentDer = parentDer.parent;
		}

		return pf + "\n";
	}

	public void fin() {

		Node<Condicion> ultimoHijoIzq = ramaIzquierda;
		Node<Condicion> parentIzq = ramaIzquierda.parent;
		while (parentIzq != null) {
			ultimoHijoIzq = parentIzq;
			parentIzq = parentIzq.parent;
		}

		Node<Condicion> ultimoHijoDer = ramaDerecha;
		Node<Condicion> parentDer = ramaDerecha.parent;
		while (parentDer != null) {
			ultimoHijoDer = parentDer;
			parentDer = parentDer.parent;
		}
		ultimoHijoDer.parent = ultimoHijoIzq;
		ultimoHijoIzq.children.add(ultimoHijoDer);
	}

}

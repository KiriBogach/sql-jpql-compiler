package TExpressionParser;

import elements.ArbolCondicion;
import elements.Condicion;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.nodes.IExpressionVisitor;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TParseTreeNode;

public class TExpressionParser implements IExpressionVisitor {

	public static boolean DEBUG = false;
	private TExpression condition;
	private ArbolCondicion arbol;

	public TExpressionParser(TExpression expr) {
		this.condition = expr;
		this.arbol = new ArbolCondicion();
	}
    
    public void imprimir() {
    	System.out.println(this.arbol);
    }

	public void printColumn() {
		this.condition.inOrderTraverse(this);
	}

	boolean is_compare_condition(EExpressionType t) {
		return ((t == EExpressionType.simple_comparison_t) || (t == EExpressionType.group_comparison_t)
				|| (t == EExpressionType.in_t));
	}

	// employees.id = data.id and 1 = 1
	public boolean exprVisit(TParseTreeNode pnode, boolean pIsLeafNode) {
		TExpression lcexpr = (TExpression) pnode;
		if (DEBUG) System.out.println("llega: \t" + lcexpr.toString());
		
		boolean root = false;
		if (lcexpr.toString().equals(this.condition.toString())) {
			if (DEBUG) System.out.println("ROOT");
			root = true;
		}
		
		 if (lcexpr.getOperatorToken() != null) {
			TExpression leftExpr = (TExpression) lcexpr.getLeftOperand();
			Condicion condicion = new Condicion();
			
			if (leftExpr.getOperatorToken() != null) {
				if (DEBUG) System.out.println("\t sin izq der");
				if (DEBUG) System.out.println("\t token '" + lcexpr.getOperatorToken().toString() + "'");
				condicion.setOperador(lcexpr.getOperatorToken().toString());
			} else {
				if (leftExpr.getStartToken().toString().equals("(")) {
					// nodo con paréntesis para sus hijos
					// (izq) operador (der)
					if (DEBUG) System.out.println("\t ()token() '" + lcexpr.getOperatorToken().toString() + "'");
					condicion.setOperador(lcexpr.getOperatorToken().toString());
					condicion.setParentesis(true);
				} else {
					if (DEBUG) System.out.println("\t " + leftExpr);
					TExpression rightExpr = (TExpression) lcexpr.getRightOperand();
					if (DEBUG) System.out.println("\t token '" + lcexpr.getOperatorToken().toString() + "'");
					if (DEBUG) System.out.println("\t " + rightExpr);

					condicion.setOperador(lcexpr.getOperatorToken().toString());
					condicion.setIzquierda(lcexpr.getLeftOperand().toScript());
					condicion.setDerecha(lcexpr.getRightOperand().toString());
				}
			}
			
			condicion.setRoot(root);
			arbol.build(condicion);
			if (DEBUG) System.out.println();
		}
		/*if (is_compare_condition(lcexpr.getExpressionType())) {
			TExpression leftExpr = (TExpression) lcexpr.getLeftOperand();

			System.out.println("column: " + leftExpr.toString());
			if (lcexpr.getComparisonOperator() != null) {
				System.out.println("Operator: " + lcexpr.getComparisonOperator().toScript());
			}
			System.out.println("value: " + lcexpr.getRightOperand().toString());
			System.out.println("");

		}*/
		return true;
	}
	
	public ArbolCondicion getArbol() {
		return arbol;
	}

	public void fin() {
		this.arbol.fin();
	}
}

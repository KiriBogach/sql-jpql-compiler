package texpressionparser;

import java.util.HashSet;
import java.util.Set;

import elements.Condicion;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.nodes.IExpressionVisitor;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TParseTreeNode;

public class TExpressionParser implements IExpressionVisitor {

	public static boolean DEBUG = false;
	private TExpression condition;
	private Set<Condicion> condiciones;

	public TExpressionParser(TExpression expr) {
		this.condition = expr;
		this.condiciones = new HashSet<>();
	}

	public String getConditionString() {
		return this.condition.toString();
	}

	public Set<Condicion> getCondiciones() {
		return condiciones;
	}

	public void parse() {
		this.condition.inOrderTraverse(this);
	}

	boolean is_compare_condition(EExpressionType t) {
		return ((t == EExpressionType.simple_comparison_t) || (t == EExpressionType.group_comparison_t)
				|| (t == EExpressionType.in_t));
	}

	// employees.id = data.id and 1 = 1
	public boolean exprVisit(TParseTreeNode pnode, boolean pIsLeafNode) {
		TExpression lcexpr = (TExpression) pnode;

		if (lcexpr.getOperatorToken() != null) {

			if (lcexpr.getStartToken().toString().equals("(") && lcexpr.getEndToken().toString().equals(")")) {
				return true;
			}

			if (lcexpr.getLeftOperand().getOperatorToken() != null
					|| lcexpr.getRightOperand().getOperatorToken() != null) {
				return true;
			}

			Condicion condicion = new Condicion();
			condicion.setOperador(lcexpr.getOperatorToken().toString());
			condicion.setIzquierda(lcexpr.getLeftOperand().toString());
			condicion.setDerecha(lcexpr.getRightOperand().toString());

			this.condiciones.add(condicion);
		}
		return true;
	}
}

package controller.test.where;

import java.util.ArrayList;
import java.util.List;

import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.TSourceTokenList;
import gudusoft.gsqlparser.nodes.IExpressionVisitor;
import gudusoft.gsqlparser.nodes.TCaseExpression;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.nodes.TOrderByItem;
import gudusoft.gsqlparser.nodes.TParseTreeNode;
import gudusoft.gsqlparser.nodes.TParseTreeNodeList;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TWhenClauseItem;
import gudusoft.gsqlparser.nodes.TWhenClauseItemList;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

public class JoinConditonsInExpr implements IExpressionVisitor {

		private TExpression expr;
		private JoinRelationAnalyze analysis;
		private TCustomSqlStatement stmt;

		public JoinConditonsInExpr(JoinRelationAnalyze analysis, TExpression expr, TCustomSqlStatement stmt) {
			this.stmt = stmt;
			this.analysis = analysis;
			this.expr = expr;
		}

		boolean is_compare_condition(EExpressionType t) {
			return ((t == EExpressionType.simple_comparison_t) || (t == EExpressionType.group_comparison_t)
					|| (t == EExpressionType.in_t));
		}

		private String getExpressionTable(TExpression expr) {
			if (expr.getObjectOperand() != null)
				return expr.getObjectOperand().getObjectString();
			else if (expr.getLeftOperand() != null && expr.getLeftOperand().getObjectOperand() != null)
				return expr.getLeftOperand().getObjectOperand().getObjectString();
			else if (expr.getRightOperand() != null && expr.getRightOperand().getObjectOperand() != null)
				return expr.getRightOperand().getObjectOperand().getObjectString();
			else
				return null;
		}

		public boolean exprVisit(TParseTreeNode pnode, boolean flag) {
			TExpression lcexpr = (TExpression) pnode;

			TExpression slexpr, srexpr, lc_expr = lcexpr;

			if (is_compare_condition(lc_expr.getExpressionType())) {
				slexpr = lc_expr.getLeftOperand();
				srexpr = lc_expr.getRightOperand();

				if (((slexpr.getExpressionType() == EExpressionType.simple_object_name_t) || (slexpr.isOracleOuterJoin())
						|| (srexpr.isOracleOuterJoin() && slexpr.getExpressionType() == EExpressionType.simple_constant_t))
						&& ((srexpr.getExpressionType() == EExpressionType.simple_object_name_t)
								|| (srexpr.isOracleOuterJoin())
								|| (slexpr.isOracleOuterJoin()
										&& srexpr.getExpressionType() == EExpressionType.simple_constant_t)
								|| (slexpr.isOracleOuterJoin() && srexpr.getExpressionType() == EExpressionType.case_t))
						|| (slexpr.getExpressionType() == EExpressionType.simple_object_name_t
								&& srexpr.getExpressionType() == EExpressionType.subquery_t)
						|| (slexpr.getExpressionType() == EExpressionType.subquery_t
								&& srexpr.getExpressionType() == EExpressionType.simple_object_name_t)) {
					TExpression lattr = null, rattr = null;
					JoinCondition jr = new JoinCondition();
					jr.sql.add(stmt);

					if (slexpr.isOracleOuterJoin()) {
						lattr = slexpr;
						jr.lefttable = lattr != null ? getExpressionTable(lattr) : null;
						jr.leftcolumn = lattr != null ? getBeforeToken(lattr.getEndToken()).toString() : null;
					} else if (slexpr.getExpressionType() == EExpressionType.simple_object_name_t) {
						lattr = slexpr;
						jr.lefttable = lattr != null ? getExpressionTable(lattr) : null;
						jr.leftcolumn = lattr != null ? lattr.getEndToken().toString() : null;
					}

					if (srexpr.isOracleOuterJoin()) {
						rattr = srexpr;
						jr.righttable = rattr != null ? getExpressionTable(rattr) : null;
						jr.rightcolumn = rattr != null ? getBeforeToken(rattr.getEndToken()).toString() : null;
						if (slexpr.getExpressionType() != EExpressionType.subquery_t) {
							analysis.joinRelationSet.add(jr);
						}
					} else if (srexpr.getExpressionType() == EExpressionType.simple_object_name_t) {
						rattr = srexpr;
						jr.righttable = rattr != null ? getExpressionTable(rattr) : null;
						jr.rightcolumn = rattr != null ? rattr.getEndToken().toString() : null;
						if (slexpr.getExpressionType() != EExpressionType.subquery_t) {
							analysis.joinRelationSet.add(jr);
						}
					} else if (srexpr.getExpressionType() == EExpressionType.case_t) {
						TCaseExpression expr = srexpr.getCaseExpression();

						TWhenClauseItemList list = expr.getWhenClauseItemList();
						for (int i = 0; i < list.size(); i++) {
							TExpression thenexpr = ((TWhenClauseItem) list.getWhenClauseItem(i)).getReturn_expr();
							if (thenexpr.getExpressionType() == EExpressionType.simple_object_name_t) {
								rattr = thenexpr;
							}
							JoinCondition condtion = new JoinCondition();
							condtion.leftcolumn = jr.leftcolumn;
							condtion.lefttable = jr.lefttable;
							condtion.sql = jr.sql;
							condtion.righttable = rattr != null ? getExpressionTable(rattr) : null;

							if (rattr != null) {
								if (rattr.isOracleOuterJoin()) {
									condtion.rightcolumn = getBeforeToken(rattr.getEndToken()).toString();
								} else {
									condtion.rightcolumn = rattr.getEndToken().toString();
								}
							} else
								condtion.rightcolumn = null;

							analysis.joinRelationSet.add(condtion);
						}
						if (expr.getElse_expr() != null) {
							TExpression elseexpr = expr.getElse_expr();
							if (elseexpr.getExpressionType() == EExpressionType.simple_object_name_t) {
								rattr = elseexpr;
							}

							JoinCondition condtion = new JoinCondition();
							condtion.leftcolumn = jr.leftcolumn;
							condtion.lefttable = jr.lefttable;
							condtion.sql = jr.sql;
							condtion.righttable = rattr != null ? getExpressionTable(rattr) : null;
							if (rattr != null) {
								if (rattr.isOracleOuterJoin()) {
									condtion.rightcolumn = getBeforeToken(rattr.getEndToken()).toString();
								} else {
									condtion.rightcolumn = rattr.getEndToken().toString();
								}
							} else
								condtion.rightcolumn = null;
							analysis.joinRelationSet.add(condtion);
						}
					}

					if (srexpr.getExpressionType() == EExpressionType.subquery_t) {
						TSelectSqlStatement subquery = (TSelectSqlStatement) srexpr.getSubQuery();
						addSubqueryJoin(jr, subquery, false);
					}

					if (slexpr.getExpressionType() == EExpressionType.subquery_t) {
						TSelectSqlStatement subquery = (TSelectSqlStatement) slexpr.getSubQuery();
						addSubqueryJoin(jr, subquery, true);
					}
				}
			}

			if (lcexpr.getExpressionType() == EExpressionType.function_t) {
				TFunctionCall func = (TFunctionCall) lcexpr.getFunctionCall();
				if (func.getArgs() != null) {
					for (int k = 0; k < func.getArgs().size(); k++) {
						TExpression expr = func.getArgs().getExpression(k);
						expr.inOrderTraverse(this);
					}
				}
				if (func.getAnalyticFunction() != null) {
					TParseTreeNodeList list = func.getAnalyticFunction().getPartitionBy_ExprList();
					searchJoinInList(list, stmt);

					if (func.getAnalyticFunction().getOrderBy() != null) {
						list = func.getAnalyticFunction().getOrderBy().getItems();
						searchJoinInList(list, stmt);
					}
				}

			} else if (lcexpr.getExpressionType() == EExpressionType.subquery_t) {
				if (lcexpr.getSubQuery() instanceof TSelectSqlStatement) {
					TSelectSqlStatement query = lcexpr.getSubQuery();
					analysis.searchSubQuery(query);
				}
			} else if (lcexpr.getExpressionType() == EExpressionType.case_t) {
				TCaseExpression expr = lcexpr.getCaseExpression();
				TExpression conditionExpr = expr.getInput_expr();
				if (conditionExpr != null) {
					conditionExpr.inOrderTraverse(this);
				}
				TExpression defaultExpr = expr.getElse_expr();
				if (defaultExpr != null) {
					defaultExpr.inOrderTraverse(this);
				}
				TWhenClauseItemList list = expr.getWhenClauseItemList();
				searchJoinInList(list, stmt);
			} else if (lcexpr.getExpressionType() == EExpressionType.exists_t) {
				if (lcexpr.getRightOperand() != null && lcexpr.getRightOperand().getSubQuery() != null) {
					TSelectSqlStatement query = lcexpr.getRightOperand().getSubQuery();
					analysis.searchSubQuery(query);
				}
			}
			return true;
		}

		private TSourceToken getBeforeToken(TSourceToken token) {
			TSourceTokenList tokens = token.container;
			int index = token.posinlist;

			for (int i = index - 1; i >= 0; i--) {
				TSourceToken currentToken = tokens.get(i);
				if (currentToken.toString().trim().length() == 0) {
					continue;
				} else {
					return currentToken;
				}
			}
			return token;
		}

		private void addSubqueryJoin(JoinCondition jr, TSelectSqlStatement subquery, Boolean isLeft) {
			if (subquery.isCombinedQuery()) {
				addSubqueryJoin(jr, subquery.getLeftStmt(), isLeft);
				addSubqueryJoin(jr, subquery.getRightStmt(), isLeft);
			} else {
				for (int i = 0; i < subquery.getResultColumnList().size(); i++) {
					TResultColumn field = subquery.getResultColumnList().getResultColumn(i);
					TColumn column = analysis.attrToColumn(field, subquery);
					for (String tableName : column.tableNames) {
						JoinCondition condtion = new JoinCondition();
						if (isLeft) {
							condtion.rightcolumn = jr.rightcolumn;
							condtion.righttable = jr.righttable;
							condtion.sql.add(stmt);
							condtion.sql.add(subquery);
							condtion.lefttable = tableName;
							condtion.leftcolumn = column.columnName;
						} else {
							condtion.leftcolumn = jr.leftcolumn;
							condtion.lefttable = jr.lefttable;
							condtion.sql.add(stmt);
							condtion.sql.add(subquery);
							condtion.righttable = tableName;
							condtion.rightcolumn = column.columnName;
						}
						analysis.joinRelationSet.add(condtion);
					}
				}
			}
		}

		private void searchJoinInList(TParseTreeNodeList list, TCustomSqlStatement stmt) {
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					List<TExpression> exprList = new ArrayList<TExpression>();

					if (list.getElement(i) instanceof TOrderByItem) {
						exprList.add((TExpression) ((TOrderByItem) list.getElement(i)).getSortKey());
					} else if (list.getElement(i) instanceof TExpression) {
						exprList.add((TExpression) list.getElement(i));
					} else if (list.getElement(i) instanceof TWhenClauseItem) {
						exprList.add(((TWhenClauseItem) list.getElement(i)).getComparison_expr());
						exprList.add(((TWhenClauseItem) list.getElement(i)).getReturn_expr());
					}

					for (TExpression lcexpr : exprList) {
						lcexpr.inOrderTraverse(this);
					}
				}
			}
		}

		public void searchExpression() {
			this.expr.inOrderTraverse(this);
		}
	}
package transformer.api.gsp;

import elements.From;
import elements.GroupBy;
import elements.Having;
import elements.OrderBy;
import elements.Select;
import elements.Where;
import exceptions.SQLParserException;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESortType;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TStatementList;
import gudusoft.gsqlparser.nodes.TAliasClause;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TGroupBy;
import gudusoft.gsqlparser.nodes.TGroupByItem;
import gudusoft.gsqlparser.nodes.TGroupByItemList;
import gudusoft.gsqlparser.nodes.TJoin;
import gudusoft.gsqlparser.nodes.TJoinItem;
import gudusoft.gsqlparser.nodes.TOrderBy;
import gudusoft.gsqlparser.nodes.TOrderByItem;
import gudusoft.gsqlparser.nodes.TOrderByItemList;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TResultColumnList;
import gudusoft.gsqlparser.nodes.TWhereClause;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import transformer.api.API;

public class GSP_API implements API {

	protected Select select;
	protected From from;
	protected Where where;
	protected OrderBy orderBy;
	protected GroupBy groupBy;

	public GSP_API() {
		this.select = new Select();
		this.from = new From();
		this.where = new Where();
		this.orderBy = new OrderBy();
		this.groupBy = new GroupBy();
	}

	public void reset() {
		this.select = new Select();
		this.from = new From();
		this.where = new Where();
		this.orderBy = new OrderBy();
		this.groupBy = new GroupBy();
	}

	@Override
	public void parse(String sql) throws SQLParserException {
		try {
			TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvmssql);
			sqlParser.sqltext = sql;
			sqlParser.parse();

			// Lista de querys (separadas con ;)
			TStatementList statementList = sqlParser.getSqlstatements();
			for (int i = 0; i < statementList.size(); i++) {
				// Statement, que puede ser (SELECT, UPDATE, DELETE ... )
				TCustomSqlStatement statement = statementList.get(i);

				if (!statement.sqlstatementtype.equals(ESqlStatementType.sstselect)) {
					continue;
				}

				// Sabemos que es un SELECT
				TSelectSqlStatement query = (TSelectSqlStatement) statement;
				parseSelect(query);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new SQLParserException("Error parseando la query: " + sql);
		}
		

	}

	private void parseSelect(TSelectSqlStatement query) {
		// SELECT
		TResultColumnList selectList = query.getResultColumnList();
		for (int j = 0; j < selectList.size(); j++) {
			TResultColumn resultColumn = selectList.getResultColumn(j);

			TExpression expresion = resultColumn.getExpr();
			TAliasClause alias = resultColumn.getAliasClause();
			// System.out.printf("\tColumn: %s, Alias: %s\n", expresion, (alias == null) ?
			// "" : alias);
			select.addField(expresion.toString(), (alias == null) ? "" : alias.toString());
		}

		// FROM
		for (int i = 0; i < query.joins.size(); i++) {
			TJoin join = query.joins.getJoin(i);

			switch (join.getKind()) {
			case TBaseType.join_source_fake:
				// Caso: select f from t1
				from.addTable(join.getTable().toString(),
						(join.getTable().getAliasClause() != null) ? join.getTable().getAliasClause().toString() : "");
				break;
			case TBaseType.join_source_table:
				// Caso: select f from t1 join t2 on t1.f1 = t2.f1
				from.addTable(join.getTable().toString(),
						(join.getTable().getAliasClause() != null) ? join.getTable().getAliasClause().toString() : "");

				for (int j = 0; j < join.getJoinItems().size(); j++) {
					TJoinItem joinItem = join.getJoinItems().getJoinItem(j);

					String type = joinItem.getJoinType().toString();
					/*
					 * System.out.printf("Join type: %s\n", type);
					 * System.out.printf("table: %s, alias: %s\n", joinItem.getTable().toString(),
					 * (joinItem.getTable().getAliasClause() != null) ?
					 * joinItem.getTable().getAliasClause().toString() : "");
					 */
					// System.out.println(joinItem.getOnCondition());
					TExpressionParser parser = new TExpressionParser(joinItem.getOnCondition());
					parser.parse();
					this.from.addJoin(joinItem.getTable().toString(),
							(joinItem.getTable().getAliasClause() != null)
									? joinItem.getTable().getAliasClause().toString()
									: "",
							type, joinItem.getOnCondition().toString(), parser.getCondiciones());

					/*
					 * from.addJoin(joinItem.getTable().toString(),
					 * (joinItem.getTable().getAliasClause() != null) ?
					 * joinItem.getTable().getAliasClause().toString() : "", type,
					 * parser.getArbol());
					 */

					// joinItem.getOnCondition().getOperatorToken()

					/*
					 * EExpressionType tipo = joinItem.getOnCondition().getExpressionType();
					 * System.out.println("tipo " + tipo); if
					 * (tipo.equals(EExpressionType.simple_comparison_t)) { String izquierda =
					 * joinItem.getOnCondition().getLeftOperand().toString(); String operador =
					 * joinItem.getOnCondition().getOperatorToken().toString(); String derecha =
					 * joinItem.getOnCondition().getRightOperand().toString(); Condicion condicion =
					 * new Condicion(izquierda, operador, derecha); arbol.createNode(condicion); }
					 * else { String operador =
					 * joinItem.getOnCondition().getOperatorToken().toString();
					 * 
					 * 
					 * if
					 * (joinItem.getOnCondition().getLeftOperand().getStartToken().toString().equals
					 * ("(")) {
					 * 
					 * } else { Condicion condicion = new Condicion("", operador, "");
					 * arbol.createNode(condicion); System.out.println(operador); }
					 * 
					 * 
					 * }
					 * 
					 * System.out.println(arbol.toString());
					 * System.out.println(joinItem.getOnCondition().getLeftOperand());
					 * 
					 * //if (joinItem.getOnCondition().getExpressionType())
					 * 
					 * 
					 * System.out.println();
					 * System.out.println(joinItem.getOnCondition().getLeftOperand().getStartToken()
					 * );
					 */

					if (joinItem.getOnCondition() != null) {
						// System.out.printf("On: %s\n", joinItem.getOnCondition().toString());
					} else if (joinItem.getUsingColumns() != null) {
						// System.out.printf("using: %s\n", joinItem.getUsingColumns().toString());
					}
				}
				break;
			case TBaseType.join_source_join:
				/*
				 * Caso: select a_join.f1 from (a as a_alias left join a1 on a1.f1 = a_alias.f1)
				 * as a_join join b on a_join.f1 = b.f1;
				 */
				TJoin source_join = join.getJoin();
				System.out.printf("\ntable: \n\t%s, alias: %s\n", source_join.getTable().toString(),
						(source_join.getTable().getAliasClause() != null)
								? source_join.getTable().getAliasClause().toString()
								: "");
				for (int j = 0; j < source_join.getJoinItems().size(); j++) {
					TJoinItem joinItem = source_join.getJoinItems().getJoinItem(j);
					System.out.printf("source_join type: %s\n", joinItem.getJoinType().toString());
					System.out.printf("table: %s, alias: %s\n", joinItem.getTable().toString(),
							(joinItem.getTable().getAliasClause() != null)
									? joinItem.getTable().getAliasClause().toString()
									: "");
					if (joinItem.getOnCondition() != null) {
						System.out.printf("On: %s\n", joinItem.getOnCondition().toString());
					} else if (joinItem.getUsingColumns() != null) {
						System.out.printf("using: %s\n", joinItem.getUsingColumns().toString());
					}
				}
				for (int j = 0; j < join.getJoinItems().size(); j++) {
					TJoinItem joinItem = join.getJoinItems().getJoinItem(j);
					System.out.printf("Join type: %s\n", joinItem.getJoinType().toString());
					System.out.printf("table: %s, alias: %s\n", joinItem.getTable().toString(),
							(joinItem.getTable().getAliasClause() != null)
									? joinItem.getTable().getAliasClause().toString()
									: "");
					if (joinItem.getOnCondition() != null) {
						System.out.printf("On: %s\n", joinItem.getOnCondition().toString());
					} else if (joinItem.getUsingColumns() != null) {
						System.out.printf("using: %s\n", joinItem.getUsingColumns().toString());
					}
				}
				break;
			default:
				System.out.println("unknown type in join!");
				break;
			}
		}

		// WHERE
		TWhereClause whereClause = query.getWhereClause();
		if (whereClause != null) {
			TExpression expresion = whereClause.getCondition();

			TExpressionParser parser = new TExpressionParser(expresion);
			parser.parse();

			this.where.setCondiciones(parser.getCondiciones());
			this.where.setCondicionRaw(parser.getConditionString());
		}

		// ORDER BY
		TOrderBy tOrderBy = query.getOrderbyClause();
		if (tOrderBy != null) {
			this.orderBy.setCondicionRaw(tOrderBy.toString());
			TOrderByItemList orderItems = tOrderBy.getItems();
			for (int i = 0; i < orderItems.size(); i++) {
				TOrderByItem orderItem = orderItems.getOrderByItem(i);
				String alias = "";
				if (!orderItem.getSortOrder().equals(ESortType.none) ) {
					alias = orderItem.getSortOrder().toString();
				}
				TExpression expresion = orderItems.getOrderByItem(i).getSortKey();
				this.orderBy.addField(expresion.toString(), alias);
			}
		}
		
		// GROUP BY
		TGroupBy tGroupBy = query.getGroupByClause();
		if (tGroupBy != null) {
			this.groupBy.setCondicionRaw(tGroupBy.toString());
			TGroupByItemList groupItems = tGroupBy.getItems();

			for (int i = 0; i < groupItems.size(); i++) {
				TGroupByItem groupItem = groupItems.getGroupByItem(i);
				TExpression expresion = groupItem.getExpr();
				this.groupBy.addField(expresion.toString(), "");
			}

			TExpression expresion = tGroupBy.getHavingClause();
			if (expresion != null) {
				Having having = new Having();
				having.setCondicionRaw(expresion.toString());

				TExpressionParser parser = new TExpressionParser(expresion);
				parser.parse();

				having.setCondiciones(parser.getCondiciones());
				having.setCondicionRaw(parser.getConditionString());
				
				this.groupBy.setHaving(having);
			}
		}

		/*
		 * System.out.println(query.getGroupByClause());
		 * System.out.println(query.getGroupByClause().getItems());
		 * 
		 * 
		 * System.out.println(query.getOrderbyClause());
		 * System.out.println(query.getOrderbyClause().getItems());
		 */

		// System.out.println("\n\n**\n\n");

		/*
		 * TWhereClause where = query.getWhereClause(); TSourceTokenList whereTokenList
		 * = new TSourceTokenList();
		 * 
		 * where.addAllMyTokensToTokenList(whereTokenList, 0);
		 */
		/*
		 * for (int k = 0; k < whereTokenList.size(); k++) { TSourceToken token =
		 * whereTokenList.get(k); String texto = token.toString().trim(); if
		 * (!texto.isEmpty()) { System.out.println(texto); } }
		 */

		/*
		 * TExpression expresion = where.getCondition();
		 * System.out.println("expresion: " + expresion);
		 * System.out.println(expresion.getSubQuery());
		 * System.out.println(expresion.getBetweenOperand());
		 * System.out.println(expresion.getConstantOperand()); TExpression right =
		 * expresion.getRightOperand(); System.out.println("right " + right);
		 * TExpression right2 = right.getRightOperand(); System.out.println("right2 " +
		 * right2); TExpression left = expresion.getLeftOperand();
		 * System.out.println(left); TExpression left2 = left.getLeftOperand();
		 * System.out.println(left2);
		 * 
		 * TExpression left3 = left2.getLeftOperand(); System.out.println(left3);
		 */

		// EExpressionType;
		/*
		 * System.out.println(where.getStartToken());
		 * System.out.println(where.getEndToken());
		 */
	}

	@Override
	public Select getSelect() {
		return this.select;
	}

	@Override
	public From getFrom() {
		return this.from;
	}

	@Override
	public Where getWhere() {
		return this.where;
	}

	@Override
	public OrderBy getOrderBy() {
		return this.orderBy;
	}
	
	@Override
	public GroupBy getGroupBy() {
		return this.groupBy;
	}
}

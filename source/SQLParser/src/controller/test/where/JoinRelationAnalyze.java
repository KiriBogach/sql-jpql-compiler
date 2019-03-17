package controller.test.where;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ETableSource;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TCTE;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.nodes.TJoin;
import gudusoft.gsqlparser.nodes.TJoinItem;
import gudusoft.gsqlparser.nodes.TOrderByItem;
import gudusoft.gsqlparser.nodes.TParseTreeNodeList;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TTableList;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

public class JoinRelationAnalyze {

	private static boolean isOutputFile;
	private StringBuilder buffer = new StringBuilder();
	private HashMap cteMap = new HashMap();
	private HashMap tableAliasMap = new HashMap();
	private List<TCustomSqlStatement> searchInSubQuerys = new ArrayList<TCustomSqlStatement>();
	private List<TCustomSqlStatement> searchInTables = new ArrayList<TCustomSqlStatement>();
	private List<TCustomSqlStatement> searchInClauses = new ArrayList<TCustomSqlStatement>();
	public HashMap queryAliasMap = new HashMap();
	public HashSet<JoinCondition> joinRelationSet = new HashSet<JoinCondition>();
	private List<JoinCondition> conditions = new ArrayList<JoinCondition>();

	public String getAnalysisResult() {
		return buffer.toString();
	}

	public List<JoinCondition> getJoinConditions() {
		return conditions;
	}

	public static void main(String[] args) {
		
		String[] arguments = {"consulta1.txt","output1.txt"};
		args = arguments;
		if (args.length == 0) {
			System.out.println("Usage: joinRelationAnalyze <sql script file path> <output file path>");
			System.out.println("sql script file path: The sql file will be analyzed.");
			System.out.println("output file path: Option, write the analysis result to the specified file.");
			// Console.Read();
			return;
		}

		String outputFile = null;
		FileOutputStream writer = null;
		if (args.length > 1) {
			outputFile = args[1];
			isOutputFile = true;
		}
		try {
			if (outputFile != null) {

				writer = new FileOutputStream(outputFile);
				System.setOut(new PrintStream(writer));

			}
			JoinRelationAnalyze analysis = new JoinRelationAnalyze(new File(args[0]), EDbVendor.dbvoracle);
			System.out.print(analysis.getAnalysisResult());
			// if (args.length <= 1)
			// {
			// Console.Read();
			// }
			// else
			{
				if (writer != null) {
					writer.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	} // main

	public JoinRelationAnalyze(String sql, EDbVendor dbVendor) {
		TGSqlParser sqlparser = new TGSqlParser(dbVendor);
		sqlparser.sqltext = sql;
		analyzeSQL(sqlparser);
	}

	public JoinRelationAnalyze(File file, EDbVendor dbVendor) {
		TGSqlParser sqlparser = new TGSqlParser(dbVendor);
		sqlparser.sqlfilename = file.getAbsolutePath();
		analyzeSQL(sqlparser);
	}

	private void analyzeSQL(TGSqlParser sqlparser) {
		int ret = sqlparser.parse();

		if (ret != 0) {
			buffer.append(sqlparser.getErrormessage());
			return;
		} else {

			TCustomSqlStatement select = (TCustomSqlStatement) sqlparser.sqlstatements.get(0);
			if (select.getCteList() != null && select.getCteList().size() > 0) {
				for (int i = 0; i < select.getCteList().size(); i++) {
					TCTE expression = (TCTE) select.getCteList().getCTE(i);
					cteMap.put(expression.getTableName(), expression.getSubquery());
				}
			}

			analyzeStatement(select);
		}

		buffer.append("JoinTable1\tJoinColumn1\tJoinTable2\tJoinColumn2\r\n");

		conditions.clear();

		for (JoinCondition join : joinRelationSet) {
			String lefttable = join.lefttable;
			String righttable = join.righttable;
			String leftcolumn = join.leftcolumn;
			String rightcolumn = join.rightcolumn;

			if ((lefttable == null || lefttable.length() == 0) && (righttable == null || righttable.length() == 0))
				continue;

			List<String[]> leftJoinNameList = getRealName(lefttable, leftcolumn, join.sql);
			List<String[]> rightJoinNameList = getRealName(righttable, rightcolumn, join.sql);

			for (String[] leftJoinNames : leftJoinNameList) {
				for (String[] rightJoinNames : rightJoinNameList) {
					if (leftJoinNames[0] != null && rightJoinNames[0] != null && leftJoinNames[1] != null
							&& rightJoinNames[1] != null) {
						JoinCondition condition = new JoinCondition();
						condition.lefttable = leftJoinNames[0];
						condition.righttable = rightJoinNames[0];
						condition.leftcolumn = leftJoinNames[1];
						condition.rightcolumn = rightJoinNames[1];

						if (!conditions.contains(condition)) {
							conditions.add(condition);
							buffer.append(fillString(condition.lefttable) + "\t" + fillString(condition.leftcolumn)
									+ "\t" + fillString(condition.righttable) + "\t" + fillString(condition.rightcolumn)
									+ "\r\n");
						}
					}
				}
			}
		}
	}

	private void analyzeStatement(TCustomSqlStatement select) {
		if (select instanceof TSelectSqlStatement) {
			TSelectSqlStatement stmt = (TSelectSqlStatement) select;

			searchJoinFromStatement(stmt);

			if (stmt.isCombinedQuery()) {
				analyzeStatement(stmt.getLeftStmt());
				analyzeStatement(stmt.getRightStmt());
			} else {
				for (int i = 0; i < select.getResultColumnList().size(); i++) {
					TResultColumn field = select.getResultColumnList().getResultColumn(i);
					searchFields(field, select);
				}
			}
		} else {
			for (int i = 0; i < select.getResultColumnList().size(); i++) {
				TResultColumn field = select.getResultColumnList().getResultColumn(i);
				searchFields(field, select);
			}
		}
	}

	private void searchJoinFromStatement(TSelectSqlStatement stmt) {
		if (stmt.joins != null) {
			for (int i = 0; i < stmt.joins.size(); i++) {
				TJoin join = stmt.joins.getJoin(i);
				if (join.getJoinItems() != null) {
					for (int j = 0; j < join.getJoinItems().size(); j++) {
						TJoinItem joinItem = join.getJoinItems().getJoinItem(j);
						TExpression expr = joinItem.getOnCondition();
						searchExpression(expr, stmt);
					}
				}
			}
		}
	}

	private List<String[]> getRealName(String tableAlias, String columnAlias, List<TCustomSqlStatement> stmtList) {
		List<String[]> nameList = new ArrayList<String[]>();
		for (TCustomSqlStatement stmt : stmtList) {

			gudusoft.gsqlparser.nodes.TTable table = null;
			String columnName = columnAlias;
			if ((tableAlias == null || tableAlias.length() == 0) && stmt instanceof TSelectSqlStatement
					&& ((TSelectSqlStatement) stmt).tables.size() == 1
					&& ((TSelectSqlStatement) stmt).tables.getTable(0).getAliasClause() == null) {
				table = ((TSelectSqlStatement) stmt).tables.getTable(0);
				getTableNames(nameList, table, columnName);
				continue;
			} else if (tableAlias == null || tableAlias.length() == 0) {
				nameList.add(new String[] { null, columnName });
				continue;
			}

			if (tableAliasMap.containsKey(tableAlias.toLowerCase() + ":" + stmt.toString())) {
				table = (gudusoft.gsqlparser.nodes.TTable) tableAliasMap
						.get(tableAlias.toLowerCase() + ":" + stmt.toString());
				getTableNames(nameList, table, columnName);
				continue;
			} else if (tableAliasMap.containsKey(tableAlias.toLowerCase())
					&& !containsKey(tableAliasMap, tableAlias.toLowerCase() + ":")) {
				table = (gudusoft.gsqlparser.nodes.TTable) tableAliasMap.get(tableAlias.toLowerCase());
				getTableNames(nameList, table, columnName);
				continue;
			} else {
				if (queryAliasMap.containsKey(tableAlias.toLowerCase())) {
					Object value = queryAliasMap.get(tableAlias.toLowerCase());
					if (value instanceof TSelectSqlStatement) {
						TSelectSqlStatement sql = (TSelectSqlStatement) value;
						getRealNameFromSql(nameList, columnAlias, stmt, sql);
					}
					continue;
				} else if (stmt instanceof TSelectSqlStatement) {
					findTableByAlias(nameList, (TSelectSqlStatement) stmt, tableAlias, columnAlias,
							new ArrayList<TSelectSqlStatement>());
					continue;
				}
				continue;
			}
		}
		return nameList;
	}

	private void getTableNames(List<String[]> nameList, gudusoft.gsqlparser.nodes.TTable table, String columnName) {
		if (!(table.getSubquery() instanceof TSelectSqlStatement)) {
			nameList.add(new String[] { table.getFullName(), columnName });
		} else {
			TSelectSqlStatement stmt = (TSelectSqlStatement) table.getSubquery();
			getRealNameFromSql(nameList, columnName, null, stmt);
		}
	}

	private void getRealNameFromSql(List<String[]> nameList, String columnAlias, TCustomSqlStatement stmt,
			TSelectSqlStatement sql) {
		gudusoft.gsqlparser.nodes.TTable table = null;
		String columnName = null;

		if (sql.isCombinedQuery()) {
			getRealNameFromSql(nameList, columnAlias, stmt, sql.getLeftStmt());
			getRealNameFromSql(nameList, columnAlias, stmt, sql.getRightStmt());
		} else {
			for (int i = 0; i < sql.getResultColumnList().size(); i++) {
				TResultColumn field = sql.getResultColumnList().getResultColumn(i);
				switch (field.getExpr().getExpressionType()) {
				case simple_object_name_t:
					TColumn column = attrToColumn(field, sql);
					if (((column.columnAlias == null || column.columnAlias.length() == 0)
							&& columnAlias.trim().equalsIgnoreCase(column.columnName.trim()))
							|| ((column.columnAlias != null && column.columnAlias.length() > 0)
									&& columnAlias.trim().equals(column.columnAlias.trim()))
							|| column.columnName.equals("*")) {
						if (column.columnPrex != null) {
							if (stmt != null && tableAliasMap
									.containsKey(column.columnPrex.toLowerCase() + ":" + stmt.toString())) {
								table = (gudusoft.gsqlparser.nodes.TTable) tableAliasMap
										.get(column.columnPrex.toLowerCase() + ":" + stmt.toString());
							} else if (tableAliasMap.containsKey(column.columnPrex.toLowerCase())) {
								table = (gudusoft.gsqlparser.nodes.TTable) tableAliasMap
										.get(column.columnPrex.toLowerCase());
							}
						} else {
							table = sql.tables.getTable(0);
						}

						if (column.columnName.equals("*")) {
							columnName = columnAlias;
						} else {
							columnName = column.columnName;
						}
					}
					break;
				}
			}
			if (table != null) {
				nameList.add(new String[] { table.getFullName(), columnName });
			}
		}
	}

	private void findTableByAlias(List<String[]> nameList, TSelectSqlStatement stmt, String tableAlias,
			String columnAlias, List<TSelectSqlStatement> stats) {
		if (stats.contains(stmt))
			return;
		else
			stats.add(stmt);

		if (stmt.isCombinedQuery()) {
			findTableByAlias(nameList, stmt.getLeftStmt(), tableAlias, columnAlias, stats);
			findTableByAlias(nameList, stmt.getRightStmt(), tableAlias, columnAlias, stats);
		} else {
			for (int i = 0; i < stmt.tables.size(); i++) {
				gudusoft.gsqlparser.nodes.TTable table = stmt.tables.getTable(i);
				if (table.getAliasClause() != null && table.getAliasClause().toString().length() > 0) {
					if (table.getAliasClause().toString().equalsIgnoreCase(tableAlias)) {
						nameList.add(new String[] { table.getTableName().toString(), columnAlias });
						return;
					}
				} else if (table.getTableName() != null) {
					if (table.getTableName().toString().equalsIgnoreCase(tableAlias)) {
						nameList.add(new String[] { table.getTableName().toString(), columnAlias });
						return;
					}
				}
			}
		}
		if (nameList.size() == 0 && stmt.getParentStmt() instanceof TSelectSqlStatement) {
			findTableByAlias(nameList, (TSelectSqlStatement) stmt.getParentStmt(), tableAlias, columnAlias, stats);
		}

	}

	private boolean containsKey(HashMap tableAliasMap, String key) {
		String[] collection = (String[]) tableAliasMap.keySet().toArray(new String[0]);
		for (String str : collection) {
			if (str.toLowerCase().startsWith(key.toLowerCase()))
				return true;
		}
		return false;
	}

	private String fillString(String text) {
		int tablength = 8;
		if (isOutputFile) {
			tablength = 9;
		}

		if (text.length() < tablength)
			text += "\t";
		return text;
	}

	public void searchFields(TResultColumn field, TCustomSqlStatement select) {
		switch (field.getExpr().getExpressionType()) {
		case simple_object_name_t:
			searchTables(select);
			searchClauses(select);
			break;
		case simple_constant_t:
			searchExpression(field.getExpr(), select);
			searchTables(select);
			searchClauses(select);
			break;
		case case_t:
			searchExpression(field.getExpr(), select);
			searchTables(select);
			searchClauses(select);
			break;
		case function_t:
			searchExpression(field.getExpr(), select);
			searchTables(select);
			searchClauses(select);

			TFunctionCall func = field.getExpr().getFunctionCall();
			// buffer.AppendLine("function name {0}",
			// func.funcname.AsText);

			// check column : function arguments
			if (func.getArgs() != null) {
				for (int k = 0; k < func.getArgs().size(); k++) {
					TExpression expr = (TExpression) func.getArgs().getExpression(k);
					searchExpression(expr, select);
				}
			} else {
				if (select.tables.getTable(0).getAliasClause() != null) {
					String alias = select.tables.getTable(0).getAliasClause().toString();
					if (!tableAliasMap.containsKey(alias.toLowerCase().trim() + ":" + select.toString())) {
						tableAliasMap.put(alias.toLowerCase().trim() + ":" + select.toString(),
								select.tables.getTable(0));
					}
					if (!tableAliasMap.containsKey(alias.toLowerCase().trim())) {
						tableAliasMap.put(alias.toLowerCase().trim(), select.tables.getTable(0));
					}
				}
			}

			if (func.getAnalyticFunction() != null) {
				TParseTreeNodeList list = func.getAnalyticFunction().getPartitionBy_ExprList();

				searchExpressionList(select, list);

				if (func.getAnalyticFunction().getOrderBy() != null) {
					list = func.getAnalyticFunction().getOrderBy().getItems();
					searchExpressionList(select, list);
				}
			}

			// check order by clause
			// if (select instanceof TSelectSqlStatement &&
			// ((TSelectSqlStatement)select).GroupbyClause != null)
			// {
			// for (int j = 0; j <
			// ((TSelectSqlStatement)select).GroupbyClause.GroupItems.Count();
			// j++)
			// {
			// TLzGroupByItem i =
			// (TLzGroupByItem)((TSelectSqlStatement)select).GroupbyClause.GroupItems[j];
			// searchExpression((TExpression)i._ndExpr, select);
			// searchTables(select);
			// }

			// }

			break;
		case subquery_t:
			if (field.getExpr().getSubQuery() instanceof TSelectSqlStatement) {
				searchSubQuery(field.getExpr().getSubQuery());
			}
			break;
		default:
			buffer.append("searchFields of type: " + field.getExpr().getExpressionType() + " not implemented yet\r\n");
			break;
		}
	}

	private void searchExpressionList(TCustomSqlStatement select, TParseTreeNodeList list) {
		if (list == null)
			return;

		for (int i = 0; i < list.size(); i++) {
			TExpression lcexpr = null;
			if (list.getElement(i) instanceof TOrderByItem) {
				lcexpr = (TExpression) ((TOrderByItem) list.getElement(i)).getSortKey();
			} else if (list.getElement(i) instanceof TExpression) {
				lcexpr = (TExpression) list.getElement(i);
			}

			if (lcexpr != null) {
				searchExpression(lcexpr, select);
			}
		}
	}

	private void searchClauses(TCustomSqlStatement select) {
		if (!searchInClauses.contains(select)) {
			searchInClauses.add(select);
		} else {
			return;
		}
		if (select instanceof TSelectSqlStatement) {

			TSelectSqlStatement statement = (TSelectSqlStatement) select;
			HashMap clauseTable = new HashMap();

			// if (statement.SortClause != null)
			// {
			// TLzOrderByList sortList = (TLzOrderByList)statement.SortClause;
			// for (int i = 0; i < sortList.Count(); i++)
			// {
			// TLzOrderBy orderBy = sortList[i];
			// TExpression expr = orderBy.SortExpr;
			// clauseTable.add(expr, ClauseType.orderby);
			// }
			// }

			if (statement.getWhereClause() != null) {
				clauseTable.put((statement.getWhereClause().getCondition()), ClauseType.where);
			}
			// if (statement.ConnectByClause != null)
			// {
			// clauseTable.add((TExpression)statement.ConnectByClause,
			// ClauseType.connectby);
			// }
			// if (statement.StartwithClause != null)
			// {
			// clauseTable.add((TExpression)statement.StartwithClause,
			// ClauseType.startwith);
			// }
			for (TExpression expr : (TExpression[]) clauseTable.keySet().toArray(new TExpression[0])) {
				ClauseType type = (ClauseType) clauseTable.get(expr);
				searchExpression(expr, select);
				searchTables(select);

			}
		}
	}

	void searchTables(TCustomSqlStatement select) {
		if (!searchInTables.contains(select)) {
			searchInTables.add(select);
		} else {
			return;
		}

		gudusoft.gsqlparser.nodes.TTableList tables = select.tables;

		if (tables.size() == 1) {
			gudusoft.gsqlparser.nodes.TTable lzTable = tables.getTable(0);
			if ((lzTable.getTableType() == ETableSource.objectname)
					&& (lzTable.getAliasClause() == null || lzTable.getAliasClause().toString().trim().length() == 0)) {
				if (cteMap.containsKey(lzTable.getTableName().toString())) {
					searchSubQuery((TSelectSqlStatement) cteMap.get(lzTable.getTableName().toString()));
				} else {
					if (lzTable.getAliasClause() != null) {
						String alias = lzTable.getAliasClause().toString();
						if (!tableAliasMap.containsKey(alias.toLowerCase().trim() + ":" + select.toString())) {
							tableAliasMap.put(alias.toLowerCase().trim() + ":" + select.toString(), lzTable);
						}
						if (!tableAliasMap.containsKey(alias.toLowerCase().trim())) {
							tableAliasMap.put(alias.toLowerCase().trim(), lzTable);
						}
					}
				}
			}
		}

		for (int i = 0; i < tables.size(); i++) {
			gudusoft.gsqlparser.nodes.TTable lztable = tables.getTable(i);
			switch (lztable.getTableType()) {
			case objectname:
				TTable table = TLzTaleToTable(lztable);
				String alias = table.tableAlias;
				if (alias != null)
					alias = alias.trim();
				else if (table.tableName != null)
					alias = table.tableName.trim();

				if (cteMap.containsKey(lztable.getTableName().toString())) {
					searchSubQuery((TSelectSqlStatement) cteMap.get(lztable.getTableName().toString()));
				} else {
					if (alias != null) {
						if (!tableAliasMap.containsKey(alias.toLowerCase().trim() + ":" + select.toString())) {
							tableAliasMap.put(alias.toLowerCase().trim() + ":" + select.toString(), lztable);
						}
						if (!tableAliasMap.containsKey(alias.toLowerCase().trim())) {
							tableAliasMap.put(alias.toLowerCase().trim(), lztable);
						}
					}
				}
				break;
			case subquery:
				if (lztable.getAliasClause() != null) {
					String tableAlias = lztable.getAliasClause().toString().trim();
					if (!queryAliasMap.containsKey(tableAlias.toLowerCase())) {
						queryAliasMap.put(tableAlias.toLowerCase(), (TSelectSqlStatement) lztable.getSubquery());
					}
				}
				searchSubQuery((TSelectSqlStatement) lztable.getSubquery());
				break;
			default:
				break;
			}
		}
	}

	public void searchSubQuery(TSelectSqlStatement select) {
		if (!searchInSubQuerys.contains(select)) {
			searchInSubQuerys.add(select);
		} else {
			return;
		}

		searchJoinFromStatement(select);

		if (select.isCombinedQuery()) {
			searchSubQuery(select.getLeftStmt());
			searchSubQuery(select.getRightStmt());
		} else {
			for (int i = 0; i < select.getResultColumnList().size(); i++) {
				TResultColumn field = select.getResultColumnList().getResultColumn(i);
				searchFields(field, select);
			}
		}
	}

	public TColumn attrToColumn(TResultColumn field, TCustomSqlStatement stmt) {
		TColumn column = new TColumn();

		TExpression attr = field.getExpr();

		column.columnAlias = field.getAliasClause() == null ? null : field.getAliasClause().toString();
		column.columnName = attr.getEndToken().toString();

		if (attr.toString().indexOf(".") > 0) {
			column.columnPrex = attr.toString().substring(0, attr.toString().lastIndexOf("."));

			String tableName = column.columnPrex;
			if (tableName.indexOf(".") > 0) {
				tableName = tableName.substring(tableName.lastIndexOf(".") + 1);
			}
			if (!column.tableNames.contains(tableName)) {
				column.tableNames.add(tableName);
			}
		} else {
			TTableList tables = stmt.tables;
			for (int i = 0; i < tables.size(); i++) {
				gudusoft.gsqlparser.nodes.TTable lztable = tables.getTable(i);
				TTable table = TLzTaleToTable(lztable);
				if (!column.tableNames.contains(table.tableName)) {
					column.tableNames.add(table.tableName);
				}
			}
		}

		return column;
	}

	TTable TLzTaleToTable(gudusoft.gsqlparser.nodes.TTable lztable) {
		TTable table = new TTable();
		if (lztable.getTableName() != null) {
			table.tableName = lztable.getName();
			if (lztable.getTableName().toString().indexOf(".") > 0) {
				table.prefixName = lztable.getTableName().toString().substring(0, lztable.getFullName().indexOf('.'));
			}
		}

		if (lztable.getAliasClause() != null) {
			table.tableAlias = lztable.getAliasClause().toString();
		}
		return table;
	}

	void searchExpression(TExpression expr, TCustomSqlStatement stmt) {
		JoinConditonsInExpr c = new JoinConditonsInExpr(this, expr, stmt);
		c.searchExpression();
	}

}

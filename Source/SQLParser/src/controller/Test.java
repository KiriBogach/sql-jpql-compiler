package controller;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.TSourceTokenList;
import gudusoft.gsqlparser.TStatementList;
import gudusoft.gsqlparser.nodes.TAliasClause;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TResultColumnList;
import gudusoft.gsqlparser.nodes.TWhereClause;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

public class Test {
	
	public static void parseSelect(TSelectSqlStatement query) {
		
		TResultColumnList selectList = query.getResultColumnList();
		for (int j = 0; j < selectList.size(); j++) {
			TResultColumn resultColumn = selectList.getResultColumn(j);

			TExpression expresion = resultColumn.getExpr();
			TAliasClause alias = resultColumn.getAliasClause();
			System.out.printf("\tColumn: %s, Alias: %s\n", expresion, (alias == null) ? "" : alias);
		}

		TWhereClause where = query.getWhereClause();
		TSourceTokenList whereTokenList = new TSourceTokenList();
		
		where.addAllMyTokensToTokenList(whereTokenList, 0);

		for (int k = 0; k < whereTokenList.size(); k++) {
			TSourceToken token = whereTokenList.get(k);
			String texto = token.toString().trim();
			if (!texto.isEmpty()) {
				System.out.println(texto);
			}
		}
		
		TExpression expresion = where.getCondition();
		System.out.println(expresion);
		System.out.println(where.getStartToken());
		System.out.println(where.getEndToken());
	}

	public static void main(String[] args) {
		TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvmssql);

		sqlParser.sqltext = ""
				+ " SELECT employee_id, last_name"
				+ " FROM employees" 
				+ " WHERE department_id = 90"
				+ " and last_name LIKE \"%bogach%\""
				+ " GROUP BY employee_id" 
				+ " ORDER BY last_name"
				+ ";";
		
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

	}

}

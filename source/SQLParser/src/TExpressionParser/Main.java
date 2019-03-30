package texpressionparser;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;

public class Main {

	public static void main(String[] args) {
		TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
		sqlparser.sqltext = "Select firstname, lastname, age from Clients where (employees.id = data.id and 1 = 1 and 0 = 0) or (employyes.id = data.subid and 0 = 0)";
		sqlparser.sqltext = "Select firstname, lastname, age from Clients where (employees.id IN (2,2,3) and 1 = 1 and 0 = 0) or (employyes.id = data.subid and 0 = 0)";
		sqlparser.sqltext = "Select firstname, lastname, age from Clients where employees.id = (select cosa from tabla) or (employees.id IN (2,2,3) and (select cosa from tabla2))";
		
		int i = sqlparser.parse();
		if (i == 0) {
			TExpressionParser w = new TExpressionParser(sqlparser.sqlstatements.get(0).getWhereClause().getCondition());
			w.parse();
		} else
			System.out.println(sqlparser.getErrormessage());
		
		
	}
}

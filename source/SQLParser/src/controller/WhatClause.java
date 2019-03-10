package controller;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

public class WhatClause {

	public static void main(String args[]) {
		TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
		
		sqlparser.sqltext = "select employee_id,last_name\n" + "from employees\n" + "where department_id = 90\n"
				+ "group by employee_id\n" + "order by last_name;";
		sqlparser.parse();

		TSelectSqlStatement select = (TSelectSqlStatement) sqlparser.sqlstatements.get(0);
		TTable table = select.tables.getTable(0);
		TObjectName o;
		System.out.println("Select statement, find out what clause a TObjectName belongs to:");
		for (int i = 0; i < table.getObjectNameReferences().size(); i++) {
			o = table.getObjectNameReferences().getObjectName(i);
			System.out.println(o.toString() + "\t\t\tlocation:" + o.getLocation());
		}

		sqlparser.sqltext = "select * from employees E\n" + "where employee_id = \n" + "(select employee_sal\n"
				+ "from emp_history\n" + "where employee_id = e.employee_id);";
		sqlparser.parse();

		select = (TSelectSqlStatement) sqlparser.sqlstatements.get(0);
		table = select.tables.getTable(0);

		System.out.println("\n\nselect statement, find out what clause a TObjectName belongs to:");
		for (int i = 0; i < table.getObjectNameReferences().size(); i++) {
			o = table.getObjectNameReferences().getObjectName(i);
			System.out.println(o.toString() + "\t\t\tlocation:" + o.getLocation());
		}

		// subquery in where clause
		select = (TSelectSqlStatement) select.getStatements().get(0);
		TTable table1 = select.tables.getTable(0);
		System.out.println("\nsubquery in delete statement, find out what clause a TObjectName belongs to:");
		for (int i = 0; i < table1.getObjectNameReferences().size(); i++) {
			o = table1.getObjectNameReferences().getObjectName(i);
			System.out.println(o.toString() + "\t\t\tlocation:" + o.getLocation());
		}

	}

}
package test;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EFunctionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.stmt.TAssignStmt;
import gudusoft.gsqlparser.stmt.TCommonBlock;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.oracle.TBasicStmt;
import junit.framework.TestCase;

public class testFunction extends TestCase {

    public void test0(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = " SELECT DISTINCT CONVERT(VARCHAR(16), sd.COBDate, 103) AS SubmissionTypeName FROM [bc].[SystemDate] sd";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TExpression expr = selectSqlStatement.getResultColumnList().getResultColumn(0).getExpr();
        TFunctionCall functionCall = expr.getFunctionCall();
        assertTrue(functionCall.getFunctionType() == EFunctionType.convert_t);
        assertTrue(functionCall.getTypename().toString().equalsIgnoreCase("VARCHAR(16)"));
        assertTrue(functionCall.getParameter().toString().equalsIgnoreCase("sd.COBDate"));
        assertTrue(functionCall.getStyle().toString().equalsIgnoreCase("103"));
    }

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "DECLARE cname VARCHAR; BEGIN cname := schema1.pkg1.GETCUSTOMERNAME(2); END;";
        assertTrue(sqlparser.parse() == 0);
        TCommonBlock commonBlock = (TCommonBlock)sqlparser.sqlstatements.get(0);
        //System.out.println(commonBlock.getBodyStatements().get(0).sqlstatementtype);
        TAssignStmt assignStmt = (TAssignStmt)commonBlock.getBodyStatements().get(0);
        TFunctionCall functionCall = assignStmt.getExpression().getFunctionCall();
        assertTrue(functionCall.getFunctionName().getSchemaString().equalsIgnoreCase("schema1"));
        assertTrue(functionCall.getFunctionName().getPackageString().equalsIgnoreCase("pkg1"));
        assertTrue(functionCall.getFunctionName().getObjectString().equalsIgnoreCase("GETCUSTOMERNAME"));

    }
}

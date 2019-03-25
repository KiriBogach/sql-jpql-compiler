package transformer.client;

import transformer.factory.JPQLTransformerFactory;
import transformer.factory.JPQLTransformers;
import transformer.service.JPQLTransformer;
import validator.service.Validator;

public class Cliente {

	public static void main(String[] args) {

		final boolean validate = true;
		final boolean showValidateResults = true;
		
		JPQLTransformer transfomer = JPQLTransformerFactory.getInstance(JPQLTransformers.GSP);

		String sql = "SELECT * FROM Customers WHERE (customerId = 2 and (customerId = 2 or customerName = 'pedro')) or (customerId = 3)";
		sql = "SELECT * FROM Customers c WHERE (c.customerid = 1 and (1 = 1 or 1 = 0)) and (c.customerid = 1)";	
		//sql = "SELECT Orders.OrderID, Customers.CustomerName, Orders.OrderDate FROM Orders INNER JOIN Customers ON Orders.CustomerID=Customers.CustomerID";

		System.out.println("SQL = '" + sql + "'\n");
		
		String jpql = transfomer.transform(sql);
		
		System.out.println("\nJPQL = '" + jpql + "'");
		
		if (validate) {
			Validator.validate(sql, jpql, showValidateResults);
		}
	}

}

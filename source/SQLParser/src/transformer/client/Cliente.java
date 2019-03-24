package transformer.client;

import transformer.factory.JPQLTransformerFactory;
import transformer.factory.JPQLTransformers;
import transformer.service.JPQLTransformer;
import validator.service.Validator;

public class Cliente {

	public static void main(String[] args) {

		final boolean validate = true;
		final boolean showValidateResults = false;
		
		JPQLTransformer transfomer = JPQLTransformerFactory.getInstance(JPQLTransformers.GSP);

		String sql = "SELECT * FROM Customers WHERE (customerId = 2 and (customerId = 2 or customerName = 'pedro')) or (customerId = 3)";
		//sql = "SELECT c.*, e.* FROM Customers c, Employees e";
		

		System.out.println("SQL = '" + sql + "'\n");
		
		String jpql = transfomer.transform(sql);
		
		System.out.println("\nJPQL = '" + jpql + "'");
		
		if (validate) {
			Validator.validate(sql, jpql, showValidateResults);
		}
	}

}

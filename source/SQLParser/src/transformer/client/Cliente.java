package transformer.client;

import transformer.factory.JPQLTransformerFactory;
import transformer.factory.JPQLTransformers;
import transformer.service.JPQLTransformer;

public class Cliente {

	public static void main(String[] args) {
		
		JPQLTransformer transfomer = JPQLTransformerFactory.getInstance(JPQLTransformers.GSP);
		
		String slqSelectQuery = "SELECT * FROM Customers";
		String jpqlSelectQuery = transfomer.transform(slqSelectQuery);
		
		System.out.println(jpqlSelectQuery);

	}

}

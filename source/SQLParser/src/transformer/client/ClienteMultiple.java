package transformer.client;

import java.util.HashMap;
import java.util.Map;

import transformer.factory.JPQLTransformerFactory;
import transformer.factory.JPQLTransformers;
import transformer.service.JPQLTransformer;

public class ClienteMultiple {

	public static void main(String[] args) {

		JPQLTransformer transfomer = JPQLTransformerFactory.getInstance(JPQLTransformers.GSP);

		HashMap<String, String> queries = new HashMap<>();

		String sql = "";
		
		sql = "SELECT * FROM Customers WHERE (customerId = 2 and (customerId = 2 or customername = 'pedro')) or (Customers.customerId = 3)";
		queries.put(sql, null);
		
		sql = "SELECT * FROM Customers c, Orders o WHERE (c.cuStoMerid = 2 and (orders.OrderID = 2 or o.OrderID = 'pedro')) or (Customers.cuStomerId = 3) or (orderID = '2') or (orderID IN (1,'ab'))";
		queries.put(sql, null);
		
		sql = "SELECT * FROM Customers c WHERE (c.customerid = 1 and (1 = 1 or 1 = 0)) and (c.customerid = 1)";
		queries.put(sql, null);
		
		sql = "SELECT * FROM Customers, Orders";
		queries.put(sql, null);
		
		sql = "SELECT * FROM Customers";
		queries.put(sql, null);
		
		sql = "SELECT c.* FROM Customers c, Orders o";
		queries.put(sql, null);
		
		sql = "SELECT customers.* FROM Customers";
		queries.put(sql, null);
		
		sql = "SELECT ord.OrderID, Customers.CustomerName FROM Orders ord, Customers";
		queries.put(sql, null);
		
		sql = "SELECT OrderID FROM Orders";
		queries.put(sql, null);
		
		sql = "SELECT CUSTOMERNAME FROM CUSTOMERS, ORDERS";
		queries.put(sql, null);
		
		sql = "SELECT CUSTOMERNAME as campoguay FROM CUSTOMERS, ORDERS";
		queries.put(sql, null);
		
		sql = "SELECT CUSTOMERNAME as campoguay FROM CUSTOMERS, ORDERS WHERE customername = 'Pepe'";
		queries.put(sql, null);
		
		sql = "SELECT CUSTOMERNAME as campoguay FROM CUSTOMERS, ORDERS WHERE customers.customername = 'Pepe'";
		queries.put(sql, null);
		
		sql = "SELECT CUSTOMERNAME as campoguay FROM CUSTOMERS custom, ORDERS WHERE custom.customername = 'Pepe'";
		queries.put(sql, null);
		
		sql = "SELECT Orders.OrderID, Customers.CustomerName, Orders.OrderDate FROM Orders INNER JOIN Customers ON Orders.CustomerID=Customers.CustomerID";
		queries.put(sql, null);
		
		sql = "SELECT o.OrderID, c.CustomerName, o.OrderDate FROM Orders o INNER JOIN Customers c ON o.CustomerID=c.CustomerID";
		queries.put(sql, null);
		
		sql = "SELECT * FROM PRODUCTS JOIN CATEGORIES ON PRODUCTS.CATEGORYID=CATEGORIES.CATEGORYID";
		queries.put(sql, null);
		
		sql = "SELECT * FROM Products p JOIN Categories c ON p.CATEGORYID=c.CATEGORYID";
		queries.put(sql, null);
		
		sql = "SELECT p.*, c.categoryid FROM Products p LEFT JOIN Categories c ON DESCRIPTION = 'test'";
		queries.put(sql, null);
		
		sql = "SELECT p.*, c.categoryid FROM Products p JOIN Categories c ON 1=1";
		queries.put(sql, null);
		
		sql = "SELECT p.*, c.categoryid FROM Products p JOIN Categories c ON (categoryName > 'pf') OR   p.CATEGORYID=c.CATEGORYID";
		queries.put(sql, null);
		
		sql = "SELECT Orders.OrderID, Customers.CustomerName, Shippers.ShipperName " 
				+ "FROM Orders "
				+ "INNER JOIN Customers ON Orders.CustomerID = Customers.CustomerID "
				+ "INNER JOIN Shippers ON Orders.ShipperID = Shippers.ShipperID "
				+ "ORDER BY Orders.OrderID DESC, CustomerName";
		queries.put(sql, null);
		
		sql = "SELECT COUNT(CustomerID), Country " 
				+ "FROM Customers " 
				+ "GROUP BY Country";
		queries.put(sql, null);
		
		sql = "SELECT COUNT(CustomerID), Country " 
				+ "FROM Customers " 
				+ "GROUP BY Country "
				+ "HAVING COUNT(CustomerID) > 5 " 
				+ "ORDER BY COUNT(CustomerID) DESC";
		queries.put(sql, null);
		
		sql = "SELECT CustomerID, Country " 
				+ "FROM Customers " 
				+ "GROUP BY Country "
				+ "HAVING CustomerID > 5 "
				+ "ORDER BY COUNT(CustomerID) DESC";
		queries.put(sql, null);

		for (Map.Entry<String, String> pair : queries.entrySet()) {
			pair.setValue(transfomer.transform(pair.getKey()));
		}

		for (Map.Entry<String, String> pair : queries.entrySet()) {
			System.out.println("SQL  => " + pair.getKey());
			System.out.println("JPQL => " + pair.getValue() + "\n");
		}
	}

}

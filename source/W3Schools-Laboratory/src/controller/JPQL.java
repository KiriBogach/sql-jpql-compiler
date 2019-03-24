package controller;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import model.Customer;

public class JPQL {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("W3Schools-Laboratory");
		EntityManager em = emf.createEntityManager();
		
		final String sqlQuery = "SELECT * FROM Customers WHERE (customerId = 2 and (customerId = 2 or customerName = 'pedro')) or (customerId = 3)";
		final String jpqlQuery = "SELECT a FROM Customer a WHERE ( a.customerID = 2 and a.customerID = 2 or a.customerName = 'pedro' )or a.customerID = 3";
		
		// JPQL QUERY
		TypedQuery<Customer> typedQuery = em.createQuery(jpqlQuery, Customer.class);
		List<Customer> resultado = typedQuery.getResultList();
		for (Customer customer : resultado) {
			System.out.println(customer);
		}

		// NATIVE QUERY - MySQL
		Query nativeQuery = em.createNativeQuery(sqlQuery);
		List<Object[]> resultadoNative = nativeQuery.getResultList();
		for (Object[] o : resultadoNative) {
			System.out.println(Arrays.deepToString(o));
		}

		// NATIVE QUERY MAPEADA A LA ENTIDAD - MySQL
		Query nativeQueryMapped = em.createNativeQuery(sqlQuery, Customer.class);
		List<Customer> resultadoNativeMapped = nativeQueryMapped.getResultList();
		for (Object o : resultadoNativeMapped) {
			System.out.println(o);
		}

		System.out.println("\n**********************");
		
		// Comparamos si es igual
		if (resultado.equals(resultadoNativeMapped)) {
			System.out.println("Resultado idéntico.");
		} else if (resultado.containsAll(resultadoNativeMapped) && resultadoNativeMapped.containsAll(resultado)) {
			System.out.println("Mismo contenido, distinto orden.");
		} else {
			System.out.println("Resultado distinto.");
		}
		

	}

}

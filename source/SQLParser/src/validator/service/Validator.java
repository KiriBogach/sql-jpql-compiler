package validator.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import database.model.Customer;

public class Validator {

	public static final String PU_NAME = "W3Schools-Laboratory";
	private static EntityManagerFactory emf = Persistence.createEntityManagerFactory(PU_NAME);

	public static void validate(String sql, String jpql, boolean showResults) {
		System.out.println("\n**********************");

		EntityManager em = emf.createEntityManager();
		
		// JPQL QUERY
		TypedQuery<Customer> typedQuery = em.createQuery(jpql, Customer.class);
		List<Customer> resultado = typedQuery.getResultList();
		for (Customer customer : resultado) {
			if (showResults) System.out.println(customer);
		}

		// NATIVE QUERY MAPEADA A LA ENTIDAD - MySQL
		Query nativeQueryMapped = em.createNativeQuery(sql, Customer.class);
		@SuppressWarnings("unchecked")
		List<Customer> resultadoNativeMapped = nativeQueryMapped.getResultList();
		for (Object o : resultadoNativeMapped) {
			if (showResults) System.out.println(o);
		}

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

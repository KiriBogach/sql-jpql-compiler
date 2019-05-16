package validator.service;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.config.ResultType;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.sessions.ArrayRecord;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.Session;

public class Validator {
	
	private static boolean sameValues(List<Map<String, Object>> a, List<Map<String, Object>> b) {
		for (Map<String, Object> filaA : a) {
			Collection<?> valuesA = filaA.values();
			boolean found = false;
			
			for (Map<String, Object> filaB : b) {
				Collection<?> valuesB = filaB.values();
				if (!valuesA.equals(valuesB)) {
					found = true;
					break;
				}
			}
			
			if (!found) {
				return false;
			}
		}
		
		for (Map<String, Object> filaB : b) {
			Collection<?> valuesB = filaB.values();
			boolean found = false;
			
			for (Map<String, Object> filaA : a) {
				Collection<?> valuesA = filaA.values();
				if (!valuesA.equals(valuesB)) {
					found = true;
					break;
				}
			}
			
			if (!found) {
				return false;
			}
		}
		
		return true;
	}

	@SuppressWarnings("unchecked")
	public static void validate(String sql, String jpql, boolean showResults) throws Exception {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("W3Schools-Laboratory");
		EntityManager em = emf.createEntityManager();

		//final String sqlInput = "SELECT p.*, c.categoryid FROM Products p JOIN Categories c ON 1=1";
		//final String jpqlOutput = "SELECT p, c.categoryID FROM Product p JOIN Category c ON 1=1";
		
		final String sqlInput = sql;
		final String jpqlOutput = jpql;

		// NATIVE QUERY - MySQL
		Query nativeQuery = em.createNativeQuery(sqlInput);
		nativeQuery.setHint(QueryHints.RESULT_TYPE, ResultType.Map);
		List<ArrayRecord> resultadoSqlOriginal = nativeQuery.getResultList();

		// JPQL QUERY - La que sacamos con el parser
		Query jpqlQuery = em.createQuery(jpqlOutput);
		jpqlQuery.setHint(QueryHints.RESULT_TYPE, ResultType.Map);
		
		// JPQL convertida en SQL para ser lanzada en la BD.
		
		Session session = em.unwrap(JpaEntityManager.class).getActiveSession(); 
		DatabaseQuery databaseQuery = ((EJBQueryImpl<?>)jpqlQuery).getDatabaseQuery(); 
		databaseQuery.prepareCall(session, new DatabaseRecord());
		//https://stackoverflow.com/questions/4362876/how-to-view-the-sql-queries-issued-by-jpa
		String jpqlConvertida = databaseQuery.getTranslatedSQLString(session, null);
		
		
		nativeQuery = em.createNativeQuery(jpqlConvertida);
		nativeQuery.setHint(QueryHints.RESULT_TYPE, ResultType.Map);
		List<ArrayRecord> resultadoJpqlConvertida = nativeQuery.getResultList();

		System.out.println("\n**********************");
		System.out.println("Nº filas SQL Original: " + resultadoSqlOriginal.size());
		System.out.println("Nº filas JPQL: " + resultadoJpqlConvertida.size());
		System.out.println("**********************");
		
		//System.out.println("\n**********************");
		//System.out.println("Resultados SQL Original: " + resultadoSqlOriginal);
		//System.out.println("Resultados JPQL: " + resultadoJpqlConvertida);
		//System.out.println("**********************");
		
		
		
		// Rellenamos A
		List<Map<String, Object>> filasSqlOriginal = new ArrayList<Map<String, Object>>();
		for (ArrayRecord arrayRecord : resultadoSqlOriginal) {
			Map<String, Object> fila = new HashMap<>();
			
			Vector<DatabaseField> fields = arrayRecord.getFields();
			Vector<Object> values = arrayRecord.getValues();
			
			for (int i = 0; i < fields.size(); i++) {
				fila.put(fields.get(i).getNameForComparisons(), values.get(i));
			}
			
			filasSqlOriginal.add(fila);
		}

		// Rellenamos B
		List<Map<String, Object>> filasJpqlConvertida = new ArrayList<Map<String, Object>>();
		for (ArrayRecord arrayRecord : resultadoJpqlConvertida) {
			Map<String, Object> fila = new HashMap<>();
			
			Vector<DatabaseField> fields = arrayRecord.getFields();
			Vector<Object> values = arrayRecord.getValues();
			
			for (int i = 0; i < fields.size(); i++) {
				fila.put(fields.get(i).getNameForComparisons(), values.get(i));
			}
			
			filasJpqlConvertida.add(fila);
		}
		
		
		
		PrintWriter writer; 
		
		writer = new PrintWriter("sql.txt", "UTF-8");
		writer.println(filasSqlOriginal);
		writer.close();
		
		writer = new PrintWriter("jpql.txt", "UTF-8");
		writer.println(filasJpqlConvertida);
		writer.close();

		// Comparamos si es igual
		
		if (resultadoSqlOriginal.size() != filasJpqlConvertida.size()) {
			System.err.println("Resultado distinto. Número de filas distintas");
		} else if (filasSqlOriginal.equals(filasJpqlConvertida)) {
			System.out.println("Resultado idéntico.");
		} else if (filasSqlOriginal.containsAll(filasJpqlConvertida) && filasJpqlConvertida.containsAll(filasSqlOriginal)) {
			System.out.println("Mismo contenido, distinto orden.");
		} else if (sameValues(filasSqlOriginal, filasJpqlConvertida)) {
			/*
			 *  Esto ocurre cuando cogemos un COUNT(*),
			 *  en SQL la cabecera será COUNT(*) en JPQL COUNT(id)
			 */
			System.out.println("Mismo resultado, cabecera distintas.");
		} else if (filasSqlOriginal.stream().map(a -> a.values()).collect(Collectors.toList()).containsAll(filasJpqlConvertida.stream().map(a -> a.values()).collect(Collectors.toList()))) {
			System.out.println("Mismo resultado, cabecera distintas.");
		} else {
			System.err.println("Resultado distinto.");
		}

	}

}

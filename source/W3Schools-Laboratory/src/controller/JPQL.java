package controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

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

public class JPQL {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("W3Schools-Laboratory");
		EntityManager em = emf.createEntityManager();

		final String sqlInput = "SELECT p.*, c.categoryid FROM Products p JOIN Categories c ON 1=1";
		final String jpqlOutput = "SELECT p, c.categoryID FROM Product p JOIN Category c ON 1=1";

		// NATIVE QUERY - MySQL
		Query nativeQuery = em.createNativeQuery(sqlInput);
		nativeQuery.setHint(QueryHints.RESULT_TYPE, ResultType.Map);
		List<ArrayRecord> resultadoSqlOriginal = nativeQuery.getResultList();

		// JPQL QUERY - La que sacamos con el parser
		Query jpqlQuery = em.createQuery(jpqlOutput);
		jpqlQuery.setHint(QueryHints.RESULT_TYPE, ResultType.Map);
		
		// JPQL convertida en SQL para ser lanzada en la BD.
		String jpqlConvertida = jpqlQuery.unwrap(EJBQueryImpl.class).getDatabaseQuery().getSQLString();
		System.out.println(jpqlQuery.unwrap(EJBQueryImpl.class).getDatabaseQuery().getSQLStrings());
		
		Session session = em.unwrap(JpaEntityManager.class).getActiveSession(); 
		DatabaseQuery databaseQuery = ((EJBQueryImpl<?>)jpqlQuery).getDatabaseQuery(); 
		databaseQuery.prepareCall(session, new DatabaseRecord());

		String sqlString = databaseQuery.getSQLString();
		System.out.println(sqlString);
		
		//https://stackoverflow.com/questions/4362876/how-to-view-the-sql-queries-issued-by-jpa
		String real = databaseQuery.getTranslatedSQLString(session, null);
		System.out.println(real);
		
		jpqlConvertida = real;
		
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
		List<Map<String, Object>> filasJpqlConvertida= new ArrayList<Map<String, Object>>();
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
		if (filasSqlOriginal.equals(filasJpqlConvertida)) {
			System.out.println("Resultado idéntico.");
		} else if (filasSqlOriginal.containsAll(filasJpqlConvertida) && filasJpqlConvertida.containsAll(filasSqlOriginal)) {
			System.out.println("Mismo contenido, distinto orden.");
		} else {
			System.out.println("Resultado distinto.");
		}

	}

}

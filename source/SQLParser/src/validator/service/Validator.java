package validator.service;

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

public class Validator {

	public static final String FOLDER_SAVE_RESULTS = "resultados/";
	
	@SuppressWarnings("unchecked")
	public static String validate(String sql, String jpql, boolean saveResults) throws Exception {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("W3Schools-Laboratory");
		EntityManager em = emf.createEntityManager();

		// final String sqlInput = "SELECT p.*, c.categoryid FROM Products p JOIN
		// Categories c ON 1=1";
		// final String jpqlOutput = "SELECT p, c.categoryID FROM Product p JOIN
		// Category c ON 1=1";

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
		DatabaseQuery databaseQuery = ((EJBQueryImpl<?>) jpqlQuery).getDatabaseQuery();
		databaseQuery.prepareCall(session, new DatabaseRecord());
		// https://stackoverflow.com/questions/4362876/how-to-view-the-sql-queries-issued-by-jpa
		String jpqlConvertida = databaseQuery.getTranslatedSQLString(session, null);

		nativeQuery = em.createNativeQuery(jpqlConvertida);
		nativeQuery.setHint(QueryHints.RESULT_TYPE, ResultType.Map);
		List<ArrayRecord> resultadoJpqlConvertida = nativeQuery.getResultList();

		System.out.println("\n**********************");
		System.out.println("N� filas SQL Original: " + resultadoSqlOriginal.size());
		System.out.println("N� filas JPQL: " + resultadoJpqlConvertida.size());
		System.out.println("**********************");

		// System.out.println("\n**********************");
		// System.out.println("Resultados SQL Original: " + resultadoSqlOriginal);
		// System.out.println("Resultados JPQL: " + resultadoJpqlConvertida);
		// System.out.println("**********************");

		/*
		 * Como contenemos optamos por una lista de mapas: Cada elemento de la lista es
		 * una fila Cada mapa es una relaci�n de nombreCampo, ValorCampo
		 */

		// Rellenamos nuestro contenedor A
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

		// Rellenamos nuestro contenedor B
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

		if (saveResults) {
			// Escribimos los resultados en los ficheros
			PrintWriter writer;
			
			String folder = FOLDER_SAVE_RESULTS + ValidatorUtils.getNowFileStatement();
			ValidatorUtils.createFolder(folder);

			writer = new PrintWriter(folder + "/sql_input.txt", "UTF-8");
			writer.println(sqlInput);
			writer.close();
			
			writer = new PrintWriter(folder + "/jpql_output.txt", "UTF-8");
			writer.println(jpqlOutput);
			writer.close();
			
			writer = new PrintWriter(folder + "/sql_results.txt", "UTF-8");
			writer.println(filasSqlOriginal);
			writer.close();

			writer = new PrintWriter(folder + "/jpql_results.txt", "UTF-8");
			writer.println(filasJpqlConvertida);
			writer.close();
		}
		
		String resultado;

		// Comparamos si los resultados son iguales
		if (resultadoSqlOriginal.size() != filasJpqlConvertida.size()) {
			resultado = "Resultado distinto. N�mero de filas distintas";
		} else if (filasSqlOriginal.equals(filasJpqlConvertida)) {
			resultado = "Resultado id�ntico.";
		} else if (filasSqlOriginal.containsAll(filasJpqlConvertida)
				&& filasJpqlConvertida.containsAll(filasSqlOriginal)) {
			resultado = "Mismo contenido, distinto orden.";
		} else if (ValidatorUtils.sameValuesWithPreconditions(filasSqlOriginal, filasJpqlConvertida)) {
			/*
			 * Esto ocurre cuando cogemos un COUNT(*), en SQL la cabecera ser� COUNT(*) en
			 * JPQL COUNT(id)
			 */
			resultado = "Mismo resultado, cabeceras controladas.";
		} else if (ValidatorUtils.sameValues(filasSqlOriginal, filasJpqlConvertida)) {
			resultado = "Mismo resultado, cabeceras distintas.";
		} else {
			resultado = "Resultado distinto.";
		}
		
		return resultado;
	}

}

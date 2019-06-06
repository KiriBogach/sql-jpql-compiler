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

import config.Config;
import exceptions.ValidationException;
import utils.validator.ValidatorUtils;
import validator.model.Result;

public class Validator {

	public static final String FOLDER_SAVE_RESULTS = "resultados/";

	public static Result validate(String sql, String jpql, boolean saveResults) throws ValidationException {
		try {
			return validateProcess(sql, jpql, saveResults);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ValidationException();
		}

	}

	@SuppressWarnings("unchecked")
	private static Result validateProcess(String sql, String jpql, boolean saveResults) throws Exception {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(Config.PU);
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

		try {
			jpqlQuery.setHint(QueryHints.RESULT_TYPE, ResultType.Map);
		} catch (IllegalArgumentException ex) {

		}

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
		System.out.println("Nº filas SQL Original: " + resultadoSqlOriginal.size());
		System.out.println("Nº filas JPQL: " + resultadoJpqlConvertida.size());
		System.out.println("**********************");

		// System.out.println("\n**********************");
		// System.out.println("Resultados SQL Original: " + resultadoSqlOriginal);
		// System.out.println("Resultados JPQL: " + resultadoJpqlConvertida);
		// System.out.println("**********************");

		/*
		 * Como contenemos optamos por una lista de mapas: Cada elemento de la lista es
		 * una fila Cada mapa es una relación de nombreCampo, ValorCampo
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

		String mensaje;
		boolean fracaso = false;
		double rate = ValidatorUtils.getMatchingRate(filasSqlOriginal, filasJpqlConvertida);

		// Comparamos si los resultados son iguales
		if (resultadoSqlOriginal.size() != filasJpqlConvertida.size()) {
			mensaje = "Resultado distinto. Número de filas distintas";
			fracaso = true;
		} else if (filasSqlOriginal.equals(filasJpqlConvertida)) {
			mensaje = "Resultado idéntico.";
		} else if (filasSqlOriginal.containsAll(filasJpqlConvertida)
				&& filasJpqlConvertida.containsAll(filasSqlOriginal)) {
			mensaje = "Mismo contenido, distinto orden.";
		} else if (ValidatorUtils.sameValuesWithPreconditions(filasSqlOriginal, filasJpqlConvertida)
				|| ValidatorUtils.sameValues(filasSqlOriginal, filasJpqlConvertida)) {

			/*
			 * Esto ocurre cuando cogemos un COUNT(*), en SQL la cabecera será COUNT(*) en
			 * JPQL COUNT(id)
			 */
			mensaje = "Mismo resultado, cabeceras distintas.";
		} else {
			mensaje = "Resultado distinto.";
			fracaso = true;
		}
		
		if (fracaso) {
			mensaje += " Rate: " + rate * 100 + " %";
		}
		
		return new Result(sqlInput, jpqlConvertida, resultadoSqlOriginal.size(), resultadoJpqlConvertida.size(), !fracaso, mensaje, rate);
	}

}

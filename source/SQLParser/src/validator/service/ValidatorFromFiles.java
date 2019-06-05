package validator.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import exceptions.SQLParserException;
import exceptions.ValidationException;
import transformer.factory.JPQLTransformerFactory;
import transformer.factory.JPQLTransformers;
import transformer.service.JPQLTransformer;

public class ValidatorFromFiles {
	public static final String FOLDER_FICHEROS = "resultados_validator/";
	public static final String NOMBRE_FICHERO_SALIDA_SQL = "/sql_inputs.txt";
	public static final String NOMBRE_FICHERO_SALIDA_JPQL = "/jpql_outputs.txt";
	public static final String NOMBRE_FICHERO_SALIDA_VALIDACION = "/validator.txt";
	public static final String CODIFICATION = "UTF-8";
	
	/* Para simular una ejecución desde java -jar */
	public static final boolean FROM_ARGS = false;
	public static final String FICHERO_ENTRADA = "entrada_ValidatorFromFiles.txt";
	
	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {
		if (FROM_ARGS && args.length != 1) {
			System.err.println("Debe indicar como parámetro el nombre del fichero con las querys a testear. "
					+ "java -jar ValidatorFromFiles.jar [entradaSQL.txt]");
			return;
		}
		
		String ficheroIndicado = (FROM_ARGS) ? args[0] : FICHERO_ENTRADA;
		
		final boolean validate = true;
		final boolean saveResults = false;
		
		String folder = FOLDER_FICHEROS + ValidatorUtils.getNowFileStatement();
		ValidatorUtils.createFolder(folder);
		
		JPQLTransformer transfomer = JPQLTransformerFactory.getInstance(JPQLTransformers.GSP);
		PrintWriter writerSql = new PrintWriter(folder + NOMBRE_FICHERO_SALIDA_SQL, CODIFICATION);
		PrintWriter writerJpql = new PrintWriter(folder + NOMBRE_FICHERO_SALIDA_JPQL, CODIFICATION);
		PrintWriter writerValidation = new PrintWriter(folder + NOMBRE_FICHERO_SALIDA_VALIDACION, CODIFICATION);
		
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(ficheroIndicado));
			String sql = reader.readLine();
			while (sql != null) {
				String jpql = "";
				String validation = "";
				String error = "";
				
				try {
					
					jpql = transfomer.transform(sql);
					if (validate) {
						validation = Validator.validate(sql, jpql, saveResults);
					}
				} catch (SQLParserException ex) {
					error = "Error SQL Parser";
				} catch (ValidationException ex) {
					error = "Error Validación";
				} catch (Exception ex) {
					error = "Error JPQL Transformer";
				}
				
				finally {
					if (!error.isEmpty()) {
						if (jpql == null || jpql.isEmpty()) {
							jpql = error;
						}
						validation = error;
					}
					writerSql.println(sql);
					writerJpql.println(jpql);
					if (validate) {
						writerValidation.println(validation);
					}
				}
				
				// Siguiente linea
				sql = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	

		writerSql.close();
		writerJpql.close();
		writerValidation.close();
	}

}

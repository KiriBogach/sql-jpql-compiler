package validator.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import transformer.factory.JPQLTransformerFactory;
import transformer.factory.JPQLTransformers;
import transformer.service.JPQLTransformer;

public class ValidatorFromFiles {
	public static final String FOLDER_FICHEROS = "resultados_validator/";
	public static final String NOMBRE_FICHERO_SALIDA_SQL = "/sql_inputs.txt";
	public static final String NOMBRE_FICHERO_SALIDA_JPQL = "/jpql_outputs.txt";
	public static final String NOMBRE_FICHERO_SALIDA_VALIDACION = "/validator.txt";
	public static final String CODIFICATION = "UTF-8";
	
	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.err.println("Debe indicar como parámetro el nombre del fichero con las querys a testear. "
					+ "java -jar ValidatorFromFiles.jar [entradaSQL.txt]");
			return;
		}
		
		String ficheroIndicado = args[0];
		
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
				try {
					jpql = transfomer.transform(sql);
					if (validate) {
						validation = Validator.validate(sql, jpql, saveResults);
					}
				} catch (Exception ex) {
					jpql = "ERROR";
					validation = "ERROR";
				} finally {
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

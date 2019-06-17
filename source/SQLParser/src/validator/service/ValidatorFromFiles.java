package validator.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.xmlmodel.ObjectFactory;

import exceptions.SQLParserException;
import exceptions.ValidationException;
import transformer.factory.JPQLTransformerFactory;
import transformer.factory.JPQLTransformers;
import transformer.service.JPQLTransformer;
import utils.validator.ValidatorUtils;
import validator.model.Result;
import validator.model.ResultList;

public class ValidatorFromFiles {
	public static final String FOLDER_FICHEROS = "resultados_validator/";
	public static final String NOMBRE_FICHERO_SALIDA_SQL = "/sql_inputs.txt";
	public static final String NOMBRE_FICHERO_SALIDA_JPQL = "/jpql_outputs.txt";
	public static final String NOMBRE_FICHERO_SALIDA_VALIDACION = "/validator.txt";
	public static final String NOMBRE_FICHERO_SALIDA_REGISTROS = "/registros.txt";
	public static final String NOMBRE_FICHERO_SALIDA_JSON = "/informe.json";
	public static final String CODIFICATION = "UTF-8";
	
	/* Para simular una ejecución desde java -jar */
	public static final boolean FROM_ARGS = false;
	public static final String FICHERO_ENTRADA = "entrada_ValidatorFromFiles.txt";
	
	public static String getJSONObject(ResultList lista) throws JAXBException {
	    Map<String, Object> properties = new HashMap<>();
	    properties.put(JAXBContextProperties.MEDIA_TYPE, "application/json");
	    properties.put(JAXBContextProperties.JSON_INCLUDE_ROOT, false);

	    JAXBContext jaxbContext = 
	        JAXBContextFactory.createContext(new Class[]  {
	        		ResultList.class,    ObjectFactory.class}, properties);
	    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
	    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

	    StringWriter stringWriter = new StringWriter();
	    jaxbMarshaller.marshal(lista, stringWriter);
	    return stringWriter.toString();
	}
	
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
		PrintWriter writerRegistros = new PrintWriter(folder + NOMBRE_FICHERO_SALIDA_REGISTROS, CODIFICATION);
		PrintWriter writerJSON = new PrintWriter(folder + NOMBRE_FICHERO_SALIDA_JSON, CODIFICATION);
		
		ResultList listaResultados = new ResultList();
		
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(ficheroIndicado));
			String sql = reader.readLine();
			while (sql != null) {
				String jpql = "";
				String validation = "";
				String error = "";
				int registros = -1;
				Result resultado = null;
				
				try {
					
					jpql = transfomer.transform(sql);
					if (validate) {
						resultado = Validator.validate(sql, jpql, saveResults);
						validation = resultado.getMensaje();
						registros = resultado.getRegistrosSQL();
						listaResultados.addResult(resultado);
					}
				} catch (SQLParserException ex) {
					error = "Error SQL Parser";
				} catch (ValidationException ex) {
					error = "Error Validación";
				} catch (Exception ex) {
					ex.printStackTrace();
					error = "Error JPQL Transformer";
				}
				
				finally {
					if (!error.isEmpty()) {
						if (jpql == null || jpql.isEmpty()) {
							jpql = error;
						}
						validation = error;
						
						Result resultadoErroneo = new Result(sql, jpql, error);
						listaResultados.addResult(resultadoErroneo);
					}
					writerSql.println(sql);
					writerJpql.println(jpql);
					writerRegistros.println(registros);
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
		

		writerJSON.println(getJSONObject(listaResultados));

		writerSql.close();
		writerJpql.close();
		writerValidation.close();
		writerRegistros.close();
		writerJSON.close();
	}

}

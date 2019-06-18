package elements;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FuncionParser {

	// Traducimos de SQL-92 a:
	// Primero intentamos traducir a las de JPQL
	// Luego, EclipseLink
	// Por último delegamos en BD
	
	@SuppressWarnings("unused")
	public static String parse(String funcion) {
		// Parseamos solo las funciones definidas en SQL92
		FuncionSQL92 funcionSQL92;
		try {
			funcionSQL92 = FuncionSQL92.valueOf(funcion);
		} catch (IllegalArgumentException ex) {
			// Si no encontramos la función de SQL-92
			return DBDelegation(funcion);
		}

		// Si encontramos una equivalencia con JPQL la devolvemos
		FuncionJPQL funcionJQPL = SQL92_JPQL.get(funcionSQL92);
		if (TRY_MAPPING_SQL92_JPQL && funcionJQPL != null) {
			if (SQL92_SIN_PARENTESIS.contains(funcionJQPL)) {
				return funcionJQPL.toString();
			}
			return funcionJQPL.toString() + "(";
		}

		// Si encontramos una equivalencia con EclipseLink la devolvemos
		FuncionEclipseLink funcionEclipseLink = SQL92_EclipseLink.get(funcionSQL92);
		if (TRY_MAPPING_SQL92_EclipseLink && funcionEclipseLink != null) {
			// OPERATOR('funcion ', datos)
			return "OPERATOR('" + funcionEclipseLink.toString() + "', ";
		}

		return DBDelegation(funcion);
	}
	
	public static String DBDelegation(String funcion) {
		// FUNC('funcion', datos)
		return "FUNC('" + funcion + "', ";
	}
	

	// Para activar/desactivar el parseo de las tecnologías
	// Si ambas se desactivan, siempre delegamos en BD
	private static final boolean TRY_MAPPING_SQL92_JPQL = true;
	private static final boolean TRY_MAPPING_SQL92_EclipseLink = false;

	public static Map<FuncionSQL92, FuncionJPQL> SQL92_JPQL = initializeMappingSQL92_JPQL();
	public static Map<FuncionSQL92, FuncionEclipseLink> SQL92_EclipseLink = initializeMappingSQL92_EclipseLink();
	
	// Para las funciones que no requieran paréntesis
	public static List<FuncionJPQL> SQL92_SIN_PARENTESIS = Arrays.asList(FuncionJPQL.CURRENT_DATE);

	private static Map<FuncionSQL92, FuncionJPQL> initializeMappingSQL92_JPQL() {
		Map<FuncionSQL92, FuncionJPQL> mapa = new HashMap<>();
		mapa.put(FuncionSQL92.COUNT, 			FuncionJPQL.COUNT);
		mapa.put(FuncionSQL92.ABS, 				FuncionJPQL.ABS);
		mapa.put(FuncionSQL92.COALESCE, 		FuncionJPQL.COALESCE);
		mapa.put(FuncionSQL92.CONCAT, 			FuncionJPQL.CONCAT);
		mapa.put(FuncionSQL92.SYSDATE, 			FuncionJPQL.CURRENT_DATE);
		mapa.put(FuncionSQL92.CURDATE, 			FuncionJPQL.CURRENT_DATE);
		mapa.put(FuncionSQL92.SYSTIME, 			FuncionJPQL.CURRENT_TIME);
		mapa.put(FuncionSQL92.SYSTIMESTAMP, 	FuncionJPQL.CURRENT_TIMESTAMP);
		mapa.put(FuncionSQL92.LENGTH, 			FuncionJPQL.LENGTH);
		mapa.put(FuncionSQL92.LOCATE, 			FuncionJPQL.LOCATE);
		mapa.put(FuncionSQL92.LOWER, 			FuncionJPQL.LOWER);
		mapa.put(FuncionSQL92.MOD, 				FuncionJPQL.MOD);
		mapa.put(FuncionSQL92.NULLIF, 			FuncionJPQL.NULLIF);
		mapa.put(FuncionSQL92.SQRT, 			FuncionJPQL.SQRT);
		mapa.put(FuncionSQL92.SUBSTRING, 		FuncionJPQL.SUBSTRING);
		mapa.put(FuncionSQL92.UPPER, 			FuncionJPQL.UPPER);

		// JPQL tiene trim (pero no RTRIM o LTRIM), 
		// delegamos en EclipseLink que sí que tiene
		return mapa;
	}
	
	private static Map<FuncionSQL92, FuncionEclipseLink> initializeMappingSQL92_EclipseLink() {
		Map<FuncionSQL92, FuncionEclipseLink> mapa = new HashMap<>();
		mapa.put(FuncionSQL92.LTRIM, 			FuncionEclipseLink.LeftTrim);
		mapa.put(FuncionSQL92.RTRIM, 			FuncionEclipseLink.RightTrim);
		mapa.put(FuncionSQL92.NEXT_DAY, 		FuncionEclipseLink.NextDay);
		mapa.put(FuncionSQL92.CEILING, 			FuncionEclipseLink.Ceil);
		mapa.put(FuncionSQL92.ADD_MONTHS, 		FuncionEclipseLink.AddMonths);
		mapa.put(FuncionSQL92.ASCII, 			FuncionEclipseLink.Ascii);
		mapa.put(FuncionSQL92.ASIN, 			FuncionEclipseLink.Asin);
		mapa.put(FuncionSQL92.ATAN, 			FuncionEclipseLink.Atan);
		mapa.put(FuncionSQL92.ATAN2, 			FuncionEclipseLink.Atan2);
		mapa.put(FuncionSQL92.CHAR, 			FuncionEclipseLink.ToChar);
		mapa.put(FuncionSQL92.CHR, 				FuncionEclipseLink.Chr);
		mapa.put(FuncionSQL92.COS, 				FuncionEclipseLink.Cos);
		mapa.put(FuncionSQL92.DAYNAME, 			FuncionEclipseLink.DateName);
		mapa.put(FuncionSQL92.EXP, 				FuncionEclipseLink.Exp);
		mapa.put(FuncionSQL92.FLOOR, 			FuncionEclipseLink.Floor);
		mapa.put(FuncionSQL92.GREATEST, 		FuncionEclipseLink.Greatest);
		mapa.put(FuncionSQL92.INITCAP, 			FuncionEclipseLink.Initcap);
		mapa.put(FuncionSQL92.INSTR, 			FuncionEclipseLink.Instring);
		mapa.put(FuncionSQL92.LEAST, 			FuncionEclipseLink.Least);
		mapa.put(FuncionSQL92.LPAD, 			FuncionEclipseLink.LeftPad);
		mapa.put(FuncionSQL92.RPAD, 			FuncionEclipseLink.RightPad);
		mapa.put(FuncionSQL92.LENGTH, 			FuncionEclipseLink.Length);
		mapa.put(FuncionSQL92.LOCATE, 			FuncionEclipseLink.Locate);
		mapa.put(FuncionSQL92.LOG10, 			FuncionEclipseLink.Log);
		mapa.put(FuncionSQL92.MONTHS_BETWEEN, 	FuncionEclipseLink.MonthsBetween);
		mapa.put(FuncionSQL92.NEXT_DAY, 		FuncionEclipseLink.NextDay);
		mapa.put(FuncionSQL92.NVL, 				FuncionEclipseLink.Nvl);
		mapa.put(FuncionSQL92.POWER, 			FuncionEclipseLink.Power);
		mapa.put(FuncionSQL92.REPLACE, 			FuncionEclipseLink.Replicate); // ???
		//mapa.put(FuncionSQL92.ROUND, 			FuncionEclipseLink.Round);
		mapa.put(FuncionSQL92.SIGN, 			FuncionEclipseLink.Sign);
		mapa.put(FuncionSQL92.SIN, 				FuncionEclipseLink.Sin);
		mapa.put(FuncionSQL92.ASIN, 			FuncionEclipseLink.Asin);
		mapa.put(FuncionSQL92.TAN, 				FuncionEclipseLink.Tan);
		mapa.put(FuncionSQL92.ATAN, 			FuncionEclipseLink.Atan);
		mapa.put(FuncionSQL92.ATAN2, 			FuncionEclipseLink.Atan2);
		mapa.put(FuncionSQL92.TO_CHAR, 			FuncionEclipseLink.ToChar);
		mapa.put(FuncionSQL92.TO_NUMBER, 		FuncionEclipseLink.ToNumber);
		mapa.put(FuncionSQL92.TRANSLATE, 		FuncionEclipseLink.Translate);
		return mapa;
	}
	
	// Funciones SQL-92 conocidas sin mapping -> delegamos

	// FuncionSQL92.CONVERT
	// FuncionSQL92.DATABASE
	// FuncionSQL92.DAYOFMONTH
	// FuncionSQL92.DAYOFWEEK
	// FuncionSQL92.DAYOFYEAR
	// FuncionSQL92.DB_NAME
	// FuncionSQL92.DECODE
	// FuncionSQL92.DEGREES
	// FuncionSQL92.HOUR
	// FuncionSQL92.IFNULL
	// FuncionSQL92.INSERT
	// FuncionSQL92.LAST_DAY
	// FuncionSQL92.LCASE
	// FuncionSQL92.MINUTE
	// FuncionSQL92.MONTH
	// FuncionSQL92.MONTHNAME
	// FuncionSQL92.NOW
	// FuncionSQL92.PI
	// FuncionSQL92.PREFIX
	// FuncionSQL92.RADIANS
	// FuncionSQL92.RAND
	// FuncionSQL92.REPEAT
	// FuncionSQL92.ROWID
	// FuncionSQL92.SECOND
	// FuncionSQL92.SUFFIX
	// FuncionSQL92.TO_DAT
	// FuncionSQL92.TO_TIME
	// FuncionSQL92.TO_TIMESTAMP
	// FuncionSQL92.UCASE
	// FuncionSQL92.USER
	// FuncionSQL92.WEEK
	// FuncionSQL92.YEAR
	
}

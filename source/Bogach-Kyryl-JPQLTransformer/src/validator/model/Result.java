package validator.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {"sql", "jpql", "sqlTransformada", "exito", "mensaje", "rateExito", "registrosSQL", "registrosSQLTransformada"})
public class Result implements Serializable {
   private static final long serialVersionUID = 1L;

	private String sql;
	private String jpql;
	private String sqlTransformada;
	
	private boolean exito;
	private String mensaje;

	private double rateExito;
	private int registrosSQL;
	private int registrosSQLTransformada;

	public Result() {

	}

	public Result(String sql, String jpql, String sqlTransformada, int registrosSQL, int registrosSQLTransformada,
			boolean exito, String mensaje, double rateExito) {
		super();
		this.sql = sql;
		this.jpql = jpql;
		this.sqlTransformada = sqlTransformada;
		this.registrosSQL = registrosSQL;
		this.registrosSQLTransformada = registrosSQLTransformada;
		this.exito = exito;
		this.mensaje = mensaje;
		this.rateExito = rateExito;
	}
	
	public Result(String sql, String jpql, String mensaje) {
		this.sql = sql;
		this.jpql = jpql;
		this.sqlTransformada = null;
		this.registrosSQL = -1;
		this.registrosSQLTransformada = -1;
		this.exito = false;
		this.mensaje = mensaje;
		this.rateExito = 0;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getJpql() {
		return jpql;
	}

	public void setJpql(String jpql) {
		this.jpql = jpql;
	}

	public String getSqlTransformada() {
		return sqlTransformada;
	}

	public void setSqlTransformada(String sqlTransformada) {
		this.sqlTransformada = sqlTransformada;
	}

	public int getRegistrosSQL() {
		return registrosSQL;
	}

	public void setRegistrosSQL(int registrosSQL) {
		this.registrosSQL = registrosSQL;
	}

	public int getRegistrosSQLTransformada() {
		return registrosSQLTransformada;
	}

	public void setRegistrosSQLTransformada(int registrosSQLTransformada) {
		this.registrosSQLTransformada = registrosSQLTransformada;
	}

	public boolean isExito() {
		return exito;
	}

	public void setExito(boolean exito) {
		this.exito = exito;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public double getRateExito() {
		return rateExito;
	}

	public void setRateExito(double rateExito) {
		this.rateExito = rateExito;
	}
	
	


}

package validator.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class Result implements Serializable {
   private static final long serialVersionUID = 1L;

	private String queryOriginal;
	private String queryTransformada;
	private int registrosOriginal;
	private int registrosTransformada;

	private boolean exito;
	private String mensaje;
	private double rateExito;

	public Result() {

	}

	public Result(String queryOriginal, String queryTransformada, int registrosOriginal, int registrosTransformada,
			boolean exito, String mensaje, double rateExito) {
		this.queryOriginal = queryOriginal;
		this.queryTransformada = queryTransformada;
		this.registrosOriginal = registrosOriginal;
		this.registrosTransformada = registrosTransformada;
		this.exito = exito;
		this.mensaje = mensaje;
		this.rateExito = rateExito;
	}

	public String getQueryOriginal() {
		return queryOriginal;
	}

	public void setQueryOriginal(String queryOriginal) {
		this.queryOriginal = queryOriginal;
	}

	public String getQueryTransformada() {
		return queryTransformada;
	}

	public void setQueryTransformada(String queryTransformada) {
		this.queryTransformada = queryTransformada;
	}

	public int getRegistrosOriginal() {
		return registrosOriginal;
	}

	public void setRegistrosOriginal(int registrosOriginal) {
		this.registrosOriginal = registrosOriginal;
	}

	public int getRegistrosTransformada() {
		return registrosTransformada;
	}

	public void setRegistrosTransformada(int registrosTransformada) {
		this.registrosTransformada = registrosTransformada;
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

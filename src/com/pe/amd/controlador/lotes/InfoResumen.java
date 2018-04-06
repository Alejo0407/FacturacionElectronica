package com.pe.amd.controlador.lotes;

import java.util.HashMap;

import com.pe.amd.modelo.beans.ResumenDiario;

public class InfoResumen{
	private String referencia;
	private String fecha;
	private String ticket;
	private String correlativo;
	private String tipo;
	
	private String tipo_o;
	
	public InfoResumen(ResumenDiario datos) {
		this.setFecha(datos.getFecha());
		this.setReferencia(datos.getFecha_referencia());
		this.setTicket(datos.getTicket());
		this.setCorrelativo(datos.getCorrelativo());
		this.setTipo_o(datos.getTipo());
		if(datos.getTipo().equals("RA"))
			this.setTipo("R. Bajas");
		else
			this.setTipo("R. Boletas");
	}
	public InfoResumen (HashMap<String,String> datos) {
		this.setFecha(datos.get("fecha_generacion"));
		this.setReferencia(datos.get("fecha_referencia"));
		this.setTicket(datos.get("ticket"));
		this.setCorrelativo(datos.get("correlativo"));
	}
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public String getTicket() {
		return ticket;
	}
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
	public String getCorrelativo() {
		return correlativo;
	}
	public void setCorrelativo(String correlativo) {
		this.correlativo = correlativo;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getTipo_o() {
		return tipo_o;
	}
	public void setTipo_o(String tipo_o) {
		this.tipo_o = tipo_o;
	}
}

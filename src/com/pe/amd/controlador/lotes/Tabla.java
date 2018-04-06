package com.pe.amd.controlador.lotes;

import java.io.Serializable;

import javax.faces.context.FacesContext;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

public class Tabla implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String numeracion;
	private String numeracion_nueva;
	private String tipo_doc;
	private String total;
	private String homologado;
	private String cliente;
	private StreamedContent file = new DefaultStreamedContent(FacesContext.getCurrentInstance()
			.getExternalContext().getResourceAsStream("/archivos/defecto.txt"),"text/txt","defecto");
	private StreamedContent sunat = new DefaultStreamedContent(FacesContext.getCurrentInstance()
			.getExternalContext().getResourceAsStream("/archivos/defecto.txt"),"text/txt","defecto");
	

	
	public String getNumeracion() {
		return numeracion;
	}
	public void setNumeracion(String numeracion) {
		this.numeracion = numeracion;
	}
	public String getNumeracion_nueva() {
		return numeracion_nueva;
	}
	public void setNumeracion_nueva(String numeracion_nueva) {
		this.numeracion_nueva = numeracion_nueva;
	}
	public String getTipo_doc() {
		return tipo_doc;
	}
	public void setTipo_doc(String tipo_doc) {
		this.tipo_doc = tipo_doc;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public String getHomologado() {
		return homologado;
	}
	public void setHomologado(String homologado) {
		this.homologado = homologado;
	}
	public StreamedContent getFile() {
		return file;
	}
	public void setFile(StreamedContent file) {
		this.file = file;
	}
	public String getCliente() {
		return cliente;
	}
	public void setCliente(String cliente) {
		this.cliente = cliente;
	}
	public StreamedContent getSunat() {
		return sunat;
	}
	public void setSunat(StreamedContent sunat) {
		this.sunat = sunat;
	}
	
	
	
}

package com.pe.amd.controlador.monitoreo;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@ManagedBean(name = "monitorTablaBean")
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
	private String fecha;
	private StreamedContent file = new DefaultStreamedContent(FacesContext.getCurrentInstance()
			.getExternalContext().getResourceAsStream("/archivos/defecto.txt"),"text/txt","defecto");
	private StreamedContent sunat = new DefaultStreamedContent(FacesContext.getCurrentInstance()
			.getExternalContext().getResourceAsStream("/archivos/defecto.txt"),"text/txt","defecto");
	
	private String transaccion;
	
	private String link;
	
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
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public String getTransaccion() {
		return transaccion;
	}
	public void setTransaccion(String transaccion) {
		this.transaccion = transaccion;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	
	public void redir() {
		RequestContext.getCurrentInstance().execute("window.open('" + link + "')");
	}
	
	
}

package com.pe.amd.controlador.config;

import java.io.Serializable;
import java.sql.Connection;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.primefaces.context.RequestContext;

import com.pe.amd.modelo.app.Programa;
import com.pe.amd.modelo.app.out.URLSunat;
@ManagedBean(name = "systemBean")
@RequestScoped	
public class Sistema implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4765639724207848554L;
	
	private com.pe.amd.modelo.beans.Sistema sistema;
	
	private String beta_factura = URLSunat.BetaFactura.getValor();
	private String produccion_factura = URLSunat.ProduccionFactura.getValor();
	
	private String beta_remision = URLSunat.BetaGuia.getValor();
	private String produccion_remision= URLSunat.ProduccionRemision.getValor();
	
	private String beta_retencion = URLSunat.BetaRetenciones.getValor();
	private String produccion_retencion= URLSunat.ProduccionRetencionPercepcion.getValor();
	
	private String ce = URLSunat.ConsultaValidezDeFE.getValor();
	
	private String cdr = URLSunat.ConsultaDeCDR.getValor();

	
	@PostConstruct
	public void init() {
		try(Connection avalon = ((DataSource)new InitialContext().lookup("sunat")).getConnection()){
			Programa p = new Programa(avalon,null,true);
			sistema = p.getSistema();
		} catch (Exception e) {
			RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage("Error"
					,"Error en la inicializacion de los datos de Sistema: "+ e.getMessage()));
			e.printStackTrace();
		}
	}
	
	public void submit() {
		try(Connection avalon = ((DataSource)new InitialContext().lookup("sunat")).getConnection()){
			Programa p = new Programa(avalon,null,true);
			p.updateSistema();
			RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage("Mensaje"
					,"Actualización correcta"));
		} catch (Exception e) {
			RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage("Error"
					,"Error en la actualizacion de los datos de Sistema: "+ e.getMessage()));
			e.printStackTrace();
		}
	}

	public com.pe.amd.modelo.beans.Sistema getSistema() {
		return sistema;
	}
	public void setSistema(com.pe.amd.modelo.beans.Sistema sistema) {
		this.sistema = sistema;
	}


	public String getBeta_factura() {
		return beta_factura;
	}


	public void setBeta_factura(String beta_factura) {
		this.beta_factura = beta_factura;
	}


	public String getProduccion_factura() {
		return produccion_factura;
	}


	public void setProduccion_factura(String produccion_factura) {
		this.produccion_factura = produccion_factura;
	}


	public String getBeta_remision() {
		return beta_remision;
	}


	public void setBeta_remision(String beta_remision) {
		this.beta_remision = beta_remision;
	}


	public String getProduccion_remision() {
		return produccion_remision;
	}


	public void setProduccion_remision(String produccion_remision) {
		this.produccion_remision = produccion_remision;
	}


	public String getBeta_retencion() {
		return beta_retencion;
	}


	public void setBeta_retencion(String beta_retencion) {
		this.beta_retencion = beta_retencion;
	}


	public String getProduccion_retencion() {
		return produccion_retencion;
	}


	public void setProduccion_retencion(String produccion_retencion) {
		this.produccion_retencion = produccion_retencion;
	}


	public String getCe() {
		return ce;
	}


	public void setCe(String ce) {
		this.ce = ce;
	}


	public String getCdr() {
		return cdr;
	}


	public void setCdr(String cdr) {
		this.cdr = cdr;
	}	
}

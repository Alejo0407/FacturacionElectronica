package com.pe.amd.controlador;

import java.io.IOException;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "facturacionBean")
@ViewScoped
public class Facturacion implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{logginBean}")
	private Loggin loggin;
	private String cont = "default";//Panel central de contenido
	
	@PostConstruct
	public void init() {
		
		if(loggin.getIdent() == null) {
			try {FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");} 
			catch (IOException e) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
				e.printStackTrace();
			}
		}
	}
	
	//Cambio de contenido central
	
	public void cambiar(String valor) {
		cont = valor;
	}
	public String salir() {
		return loggin.loggout();
	}
	//***-**************************
	public Loggin getLoggin() {
		return loggin;
	}
	public void setLoggin(Loggin loggin) {
		this.loggin = loggin;
	}

	public String getCont() {
		return cont;
	}

	public void setCont(String cont) {
		this.cont = cont;
	}
}

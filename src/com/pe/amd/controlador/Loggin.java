package com.pe.amd.controlador;

import java.io.Serializable;
import java.sql.Connection;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.primefaces.context.RequestContext;

import com.pe.amd.modelo.app.Programa;
import com.pe.amd.modelo.beans.Usuario;

@ManagedBean(name = "logginBean")
@SessionScoped
public class Loggin implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1371578839369019973L;
	/**
	 * 
	 */
	private String ident;
	private String pass;
	
	private Integer rango;
	
	public String loggout() {
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "index.xhtml";
	}
	
	public String loggin() {
		
		String ret ="";
		if(ident != null && pass != null) {
			try (Connection avalon = ( (DataSource)new InitialContext().lookup("sunat") ).getConnection()){
				Usuario u = new Programa(avalon,null,true).loggin(ident, pass);
				if(u != null) {
					rango = u.getRango() ;
					if(rango != null) 
						ret = "/facturacion.xhtml";
					else 
						RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_ERROR, "¡Error!"
								, "Error en la contraseña o Usuario"));
				}
				else {
					RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_ERROR, "¡Error!"
							, "Error en la contraseña o Usuario"));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_ERROR, "¡Error!"
						, "Error en el sistema: " + e.getMessage()));
				e.printStackTrace();
			} 
		}
		return ret;
	}

	public String getIdent() {
		return ident;
	}

	public void setIdent(String ident) {
		this.ident = ident;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}


	@Override
	public String toString() {
		return "Loggin [ ident=" + ident + ", pass=" + pass + "]";
	}

	public Integer getRango() {
		return rango;
	}

	public void setRango(Integer rango) {
		this.rango = rango;
	}
	
}

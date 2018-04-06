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
import com.pe.amd.modelo.beans.BeanManager;
import com.pe.amd.modelo.beans.Correlacion;

@ManagedBean(name = "correlacionBean")
@RequestScoped
public class Correlativo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6392733307536273630L;
	
	private Correlacion c_factura;
	private Correlacion c_boleta;
	
	@PostConstruct
	public void init() {
		try(Connection avalon = ((DataSource)new InitialContext().lookup("sunat")).getConnection()){
			Programa p = new Programa(avalon,null,true);
			this.setC_factura(p.getCorrelacion(BeanManager.COD_FACTURA));
			this.setC_boleta(p.getCorrelacion(BeanManager.COD_BOLETA));
		} catch (Exception e) {
			RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage("Error"
					,"Error en la inicializacion de la correlacion: "+ e.getMessage()));
			e.printStackTrace();
		}
	}
	
	public void handleKeyEvent(String tipo) {
		if(tipo.equals("sb")) {
			if(c_boleta.getSerie().length() > 3)
				c_boleta.setSerie(c_boleta.getSerie().substring(0, 3));
		}
		else if (tipo.equals("sf")) {
			if(c_factura.getSerie().length() > 3)
				c_factura.setSerie(c_factura.getSerie().substring(0, 3));
		}
		else if(tipo.equals("cb")) {
			if(c_boleta.getCorrelativo() > 99999999)
				c_boleta.setCorrelativo(99999999);
		}
		else if(tipo.equals("cf")) {
			if(c_factura.getCorrelativo() > 99999999)
				c_factura.setCorrelativo(99999999);
		}
	}
	
	public void submit() {
		try(Connection avalon = ((DataSource)new InitialContext().lookup("sunat")).getConnection()){
			Programa p = new Programa(avalon,null,true);
			p.updateCorrelacion(getC_boleta());
			p.updateCorrelacion(getC_factura());
			RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage("Mensaje"
					,"Actualización correcta"));
		} catch (Exception e) {
			RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage("Error"
					,"Error en la actualizacion de la correlacion: "+ e.getMessage()));
			e.printStackTrace();
		}
	}
	
	public Correlacion getC_factura() {
		return c_factura;
	}
	public void setC_factura(Correlacion c_factura) {
		this.c_factura = c_factura;
	}
	public Correlacion getC_boleta() {
		return c_boleta;
	}
	public void setC_boleta(Correlacion c_boleta) {
		this.c_boleta = c_boleta;
	}
	
}

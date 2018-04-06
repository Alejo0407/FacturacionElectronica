package com.pe.amd.controlador.lotes;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FlowEvent;
import org.primefaces.model.ByteArrayContent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.pe.amd.modelo.app.Programa;
import com.pe.amd.modelo.beans.ResumenDiario;

@ManagedBean(name = "statusBean")
@ViewScoped
public class Status implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5342520426779828289L;
	
	private Date fecha;
	private List<InfoResumen> lista;
	private StreamedContent file = new DefaultStreamedContent(FacesContext.getCurrentInstance()
			.getExternalContext().getResourceAsStream("/archivos/defecto.txt"),"text/txt","defecto");
	private String status;
	
	private String selectedTicket;
	private String correlativo;
	
	private boolean referencia;
	private String fecha_generacion;
	private String tipo;
	
	public void init() {
		this.setStatus("-");
		file = new DefaultStreamedContent(FacesContext.getCurrentInstance()
				.getExternalContext().getResourceAsStream("/archivos/defecto.txt"),"text/txt","defecto");
	}
	
	public void load() {
		lista = new ArrayList<>();
		try (Connection avalon = ( (DataSource)new InitialContext().lookup("sunat") ).getConnection()){
			Programa p = new Programa(avalon,null,true);
			
			List<ResumenDiario> data = new ArrayList<>();
			if(referencia) {
				if(tipo.equals("-")) {
					data.addAll(p.getResumenesBajasPorReferencia(getFecha()));
					data.addAll(p.getResumenesDiariosPorReferencia(getFecha()));
				}else if(tipo.equals("RA"))
					data.addAll(p.getResumenesBajasPorReferencia(getFecha()));
				else if(tipo.equals("RC"))
					data.addAll(p.getResumenesDiariosPorReferencia(getFecha()));
			}
			else {
				if(tipo.equals("-")) {
					data.addAll(p.getResumenesBajasPorEmision(getFecha()));
					data.addAll(p.getResumenesDiariosPorEmision(getFecha()));
				}else if(tipo.equals("RA"))
					data.addAll(p.getResumenesBajasPorEmision(getFecha()));
				else if(tipo.equals("RC"))
					data.addAll(p.getResumenesDiariosPorEmision(getFecha()));
			}
			for(ResumenDiario rs:data)
				lista.add(new InfoResumen(rs));
		}
		catch(Exception e) {
			RequestContext.getCurrentInstance()
			.showMessageInDialog(new FacesMessage("Error en la Extracción de datos: "
					+e.getMessage()));
		}
	}
	
	public void requestStatus() {
		try (Connection avalon = ( (DataSource)new InitialContext().lookup("sunat") ).getConnection()){
			Object[] datos = new Programa(avalon,null,true).
					getStatus(getSelectedTicket(),fecha_generacion,correlativo,tipo,false);
			
			if(datos[0] instanceof String)
				this.setStatus((String)datos[0]);
			else {
				int rsp = (int)datos[0];
				
				if(rsp == 0)
					this.setStatus(String.format("%d - %s", rsp,"CORRECTO"));
				else if(rsp == 98)
					this.setStatus(String.format("%d - %s", rsp,"PROCESANDO"));
				else if(rsp == 99)
					this.setStatus(String.format("%d - %s", rsp,"ERRORES"));
				else if(rsp == 127)
					this.setStatus(String.format("%d - %s", rsp,"NO EXISTE EL TICKET"));
				else
					this.setStatus(String.valueOf(rsp));
				
				if((boolean)datos[4] == false) {
					RequestContext.getCurrentInstance()
					.showMessageInDialog(new FacesMessage("ERROR","Error en la actualizacion de la BD: "
							));
				}
			}
			this.setFile(new ByteArrayContent((byte[])datos[1],(String)datos[2],(String)datos[3]));
		}catch(Exception e) {
			RequestContext.getCurrentInstance()
			.showMessageInDialog(new FacesMessage("ERROR","Error en la peticion del estatus: "
					+e.getMessage()));
		}
	}
	
	public void accion(String ticket,String correlativo,String fecha_generacion,String tipo) {
		init();
		
		setSelectedTicket(ticket);
		this.setCorrelativo(correlativo);
		this.setFecha_generacion(fecha_generacion);
		this.setTipo(tipo);
		RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage("Info-Message"
				,"Se ha seleccionado: "+ticket + "::" + correlativo + "::" + fecha_generacion+"::"+tipo));
	}
	
	public String onFlowProcess(FlowEvent event) {
		String ret = event.getNewStep();
		if(event.getNewStep().equals("tab2"))
			load();
		if(event.getNewStep().equals("tab3")) {
			if(this.getSelectedTicket() == null) {
				RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage("Error"
						,"Debe seleccionar un ticket"));
				ret = "tab2";
			}
			else if(this.getSelectedTicket().isEmpty()) {
				RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage("Error"
						,"Debe seleccionar un ticket"));
				ret = "tab2";
			}
			System.out.println(getSelectedTicket());
			requestStatus();
		}
		return ret;
	}
	
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public List<InfoResumen> getLista() {
		return lista;
	}
	public void setLista(List<InfoResumen> lista) {
		this.lista = lista;
	}
	public String getSelectedTicket() {
		return selectedTicket;
	}
	public void setSelectedTicket(String selectedTicket) {
		this.selectedTicket = selectedTicket;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	public String getCorrelativo() {
		return correlativo;
	}

	public void setCorrelativo(String correlativo) {
		this.correlativo = correlativo;
	}

	public boolean isReferencia() {
		return referencia;
	}

	public void setReferencia(boolean referencia) {
		this.referencia = referencia;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getFecha_generacion() {
		return fecha_generacion;
	}

	public void setFecha_generacion(String fecha_generacion) {
		this.fecha_generacion = fecha_generacion;
	}
}

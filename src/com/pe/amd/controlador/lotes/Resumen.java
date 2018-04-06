package com.pe.amd.controlador.lotes;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
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
import com.pe.amd.modelo.beans.Cabdocumentos;

@ManagedBean(name="resumenBean")
@ViewScoped
public class Resumen {
	private List<Tabla> lista;
	private StreamedContent file = new DefaultStreamedContent(FacesContext.getCurrentInstance()
			.getExternalContext().getResourceAsStream("/archivos/defecto.txt"),"text/txt","defecto");
	private StreamedContent respuesta = new DefaultStreamedContent(FacesContext.getCurrentInstance()
			.getExternalContext().getResourceAsStream("/archivos/defecto.txt"),"text/txt","defecto");
	
	private Date fecha;
	
	private String nom_archivo;
	private String fecha_referencia;
	private String fecha_generacion;
	private String ticket;
	
	public String onFlowProcess(FlowEvent event) {
		String page = event.getNewStep();
		if(event.getNewStep().equals("tab2")) {
			page = this.getDatosResumen(event);
		}
		else if(event.getNewStep().equals("tab4")) {
			page = generarResumen(event);
		}
		
		return page;
	}
	private String generarResumen(FlowEvent event) {
		String page = event.getNewStep();
		try (Connection avalon = ( (DataSource)new InitialContext().lookup("sunat") ).getConnection()){
			Object[] ans = new Programa(avalon,null,true).generarResumenDiario(fecha);
			this.ticket = (String)ans[0];
			this.setFile(new ByteArrayContent((byte[])ans[1],(String)ans[3], (String)ans[2]));
			this.setRespuesta(new ByteArrayContent((byte[])ans[4],(String)ans[6], (String)ans[5]));
			
		} catch (Exception  e) {
			RequestContext.getCurrentInstance()
			.showMessageInDialog(new FacesMessage("Error","Error en la generacion del resumen: "
					+e.getMessage()));
			e.printStackTrace();
		} 
		return page;
	}
	private String getDatosResumen(FlowEvent event) {
		String page = event.getNewStep();
		try(Connection avalon = ( (DataSource)new InitialContext().lookup("sunat") ).getConnection()){
			List<Cabdocumentos> cab = new Programa(avalon,null,true).getDatosResumenenDiario(fecha);
			if(cab.size() == 0) {
				RequestContext.getCurrentInstance().showMessageInDialog(
						new FacesMessage("Error","El día: "+ getFecha() + " no tiene boletas registradas"));
				page = "tab1";
			}
			else {
				this.lista = new ArrayList<>();
				for(Cabdocumentos boleta:cab) {
					Tabla t = new Tabla();
					t.setCliente(boleta.getNomcliente());
					t.setHomologado(String.valueOf(boleta.getHomologado()));
					t.setNumeracion(boleta.getSerie()+"-"+boleta.getNumero());
					t.setTotal(String.format("%.2f", boleta.getTotaldoc()));
					t.setFile(new DefaultStreamedContent(boleta.getArchivo().getBinaryStream(),
							"text/xml",boleta.getNombre_archivo()));
					t.setNumeracion_nueva(boleta.getSerieelec()+"-"+boleta.getNumeroelec());
					t.setTipo_doc(boleta.getTipodocumento());
					lista.add(t);
				}
				this.setFecha_referencia(cab.get(0).getFechaemision().substring(0, 4) 
						+ "-" + cab.get(0).getFechaemision().substring(4, 6)
						+ "-" + cab.get(0).getFechaemision().substring(6));
				GregorianCalendar calendar = new GregorianCalendar();
				setFecha_generacion(String.format("%02d",calendar.get(GregorianCalendar.DATE))+"-"
						+ String.format("%02d",calendar.get(GregorianCalendar.MONTH)+1) + "-"
						+String.format("%04d",calendar.get(GregorianCalendar.YEAR)));
				
			}
			this.setNom_archivo("Prueba");
		} catch (Exception e) {
			RequestContext.getCurrentInstance()
			.showMessageInDialog(new FacesMessage("Error","Error en la generacion del resumen: "
					+e.getMessage()));
			lista = new ArrayList<>();
			e.printStackTrace();
			page = "tab1";
		}
		return page;
	}
	
	
	public StreamedContent getFile() {
		return file;
	}
	public void setFile(StreamedContent file) {
		this.file = file;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public List<Tabla> getLista() {
		return lista;
	}
	public void setLista(List<Tabla> lista) {
		this.lista = lista;
	}
	public String getNom_archivo() {
		return nom_archivo;
	}
	public void setNom_archivo(String nom_archivo) {
		this.nom_archivo = nom_archivo;
	}
	public String getFecha_referencia() {
		return fecha_referencia;
	}
	public void setFecha_referencia(String fecha_referencia) {
		this.fecha_referencia = fecha_referencia;
	}
	public String getFecha_generacion() {
		return fecha_generacion;
	}
	public void setFecha_generacion(String fecha_generacion) {
		this.fecha_generacion = fecha_generacion;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public StreamedContent getRespuesta() {
		return respuesta;
	}

	public void setRespuesta(StreamedContent respuesta) {
		this.respuesta = respuesta;
	}
}

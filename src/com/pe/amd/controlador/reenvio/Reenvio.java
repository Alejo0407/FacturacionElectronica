package com.pe.amd.controlador.reenvio;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
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
import com.pe.amd.modelo.beans.BeanManager;
import com.pe.amd.modelo.beans.Cabdocumentos;

@ManagedBean(name = "reenvioBean")
@ViewScoped
public class Reenvio {

	private List<Cabdocumentos> documentos;
	private Date fechaInicio,fechaFin;
	private Cabdocumentos seleccionado;
	private StreamedContent xml = new DefaultStreamedContent(FacesContext.getCurrentInstance()
			.getExternalContext().getResourceAsStream("/archivos/defecto.txt"),"text/txt","defecto");
	
	
	private String status;
	private String mensaje;
	private StreamedContent response = new DefaultStreamedContent(FacesContext.getCurrentInstance()
			.getExternalContext().getResourceAsStream("/archivos/defecto.txt"),"text/txt","defecto");
	
	@PostConstruct
	public void init() {
	}
	
	public String onFlowListener(FlowEvent event) {
		
		String ret = event.getNewStep();
		if(ret.equals("tab2")) {
			fill();
		}
		else if(ret.equals("tab4")) {
			try(Connection avalon = ( (DataSource)new InitialContext().lookup("sunat") ).getConnection()){
				Programa programa = new Programa(avalon, null, true);
				Object[] datos = programa.reenviarDocumento(seleccionado);
				
				Integer i = (int)datos[0];
				if(i == -1)
					this.status = "-1 => Error en Produccion";
				else if(i == 1)
					this.status = " 1 => Aceptado";
				else if(i == -2)
					this.status = "-2 => Rechazado";
				
				this.mensaje = (String)datos[1];
				
				this.response = new ByteArrayContent((byte[])datos[2],(String)datos[3],(String)datos[4]);
				
			} catch ( Exception e) {
				// TODO Auto-generated catch block
				RequestContext.getCurrentInstance().showMessageInDialog(
						new FacesMessage("Error","Error en el envio..."+e.getMessage()));
				e.printStackTrace();
			}
		}
		
		return ret;
	}
	
	public void seleccionar(Cabdocumentos in) throws SQLException {
		this.seleccionado = in;
		xml = new DefaultStreamedContent(this.seleccionado.getArchivo().getBinaryStream(),
				"text/xml",this.seleccionado.getNombre_archivo());
	}
	
	
	public void fill() {
		try(Connection avalon = ( (DataSource)new InitialContext().lookup("sunat") ).getConnection()){
			Programa programa = new Programa(avalon, null, true);
			this.documentos = programa.getDocumentos(fechaInicio, fechaFin, BeanManager.COD_FACTURA);
			filtrar();
			programa.close();
		} catch ( Exception e) {
			RequestContext.getCurrentInstance().showMessageInDialog(
					new FacesMessage("Error","Error en la carga de datos..."+e.getMessage()));
			e.printStackTrace();
		}
	}
	
	private void filtrar(){
		List<Cabdocumentos> filtrado = new ArrayList<>();
		for(Cabdocumentos doc:documentos) {
			if(doc.getArchivo() != null)
				filtrado.add(doc);
		}
		this.documentos = filtrado;
	}

	public List<Cabdocumentos> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(List<Cabdocumentos> documentos) {
		this.documentos = documentos;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	public Cabdocumentos getSeleccionado() {
		return seleccionado;
	}

	public void setSeleccionado(Cabdocumentos seleccionado) {
		this.seleccionado = seleccionado;
	}

	public StreamedContent getXml() {
		return xml;
	}

	public void setXml(StreamedContent xml) {
		this.xml = xml;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public StreamedContent getResponse() {
		return response;
	}

	public void setResponse(StreamedContent response) {
		this.response = response;
	}
}

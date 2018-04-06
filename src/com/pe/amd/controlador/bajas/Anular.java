package com.pe.amd.controlador.bajas;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
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
import com.pe.amd.modelo.beans.Cabdocumentos;

@ManagedBean(name = "anularDocBean")
@ViewScoped
public class Anular implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2938480143585814245L;
	
	private boolean enviado;
	private Cabdocumentos doc;
	private String razon;
	
	private StreamedContent file = new DefaultStreamedContent(FacesContext.getCurrentInstance()
			.getExternalContext().getResourceAsStream("/archivos/defecto.txt"),"text/txt","defecto");
	private StreamedContent respuesta = new DefaultStreamedContent(FacesContext.getCurrentInstance()
			.getExternalContext().getResourceAsStream("/archivos/defecto.txt"),"text/txt","defecto");
	private String ticket;
	
	
	private Date fecha_emision;
	
	@PostConstruct
	public void init() {
		this.doc = new Cabdocumentos();
		file = new DefaultStreamedContent(FacesContext.getCurrentInstance()
				.getExternalContext().getResourceAsStream("/archivos/defecto.txt"),"text/txt","defecto");
		respuesta = new DefaultStreamedContent(FacesContext.getCurrentInstance()
				.getExternalContext().getResourceAsStream("/archivos/defecto.txt"),"text/txt","defecto");
	}
	/**
	 * Este metodo es para un flujo normal de anulacion de documentos en el cual
	 * el elemento elegido esta registrado correctamente en la BD
	 * @param event
	 * @return
	 */
	public String onFlowProcess1(FlowEvent event) {
		file = new DefaultStreamedContent(FacesContext.getCurrentInstance()
				.getExternalContext().getResourceAsStream("/archivos/defecto.txt"),"text/txt","defecto");
		respuesta = new DefaultStreamedContent(FacesContext.getCurrentInstance()
				.getExternalContext().getResourceAsStream("/archivos/defecto.txt"),"text/txt","defecto");
		
		String newStep = event.getNewStep();
		if(event.getNewStep().equals("tab2")) {
			
			try(Connection avalon = ( (DataSource)new InitialContext().lookup("sunat") )
					.getConnection()){
				
				Programa p = new Programa(avalon,null,true);
				HashMap<String,String> mapa = new HashMap<>();
				mapa.put("serieelec", doc.getSerieelec());
				mapa.put("numeroelec", doc.getNumeroelec());
				List<Cabdocumentos> temporal = p.getDocumentos(mapa);
				
				if(temporal.size() == 0) {
					RequestContext.getCurrentInstance()
					.showMessageInDialog(new FacesMessage("ERROR"
							,"No existe documento con esa serie y numero"));
					newStep = "tab1";
				}
					
				else {
					this.doc = temporal.get(0);	
					if(doc.getAnulado() == 1) {
						RequestContext.getCurrentInstance()
						.showMessageInDialog(new FacesMessage("INFORMACION"
								,"El documento: " + doc.getSerieelec()
								+"-"+doc.getNumeroelec()+ " ya ha sido registrado como anulado"));
					}		
				}
				
			}catch(Exception e) {
				RequestContext.getCurrentInstance()
				.showMessageInDialog(new FacesMessage("ERROR",e.getMessage()));
			}
		}
		else if(event.getNewStep().equals("tab3")) {
			try(Connection avalon = ( (DataSource)new InitialContext().lookup("sunat") )
					.getConnection()){
				Programa p = new Programa(avalon,null,true);
				if(enviado) {
					ArrayList<Cabdocumentos> s = new ArrayList<>();s.add(this.doc);
					ArrayList<String> r = new ArrayList<>();r.add(this.razon);
					Object[] ans = p.generarResumenBajas( s , r);
					this.ticket = (String)ans[0];
					
					this.setFile(new ByteArrayContent((byte[])ans[1],(String)ans[3], (String)ans[2]));
					this.setRespuesta(new ByteArrayContent((byte[])ans[4],(String)ans[6], (String)ans[5]));
					
				}else {
					newStep = "tab2";
					p.anularDocumento(doc);
				}
				RequestContext.getCurrentInstance()
					.showMessageInDialog(new FacesMessage("Información","Documento anulado con exito"));
				
			}catch(Exception e) {
				RequestContext.getCurrentInstance()
				.showMessageInDialog(new FacesMessage("ERROR",e.getMessage()));
			}
		}
		
		return newStep;
	}
	/**
	 * Este metodo maneja un flujo para cuando existane errores, y un documento determinado
	 * exista en la sunat mas no en la BD del sistema (ERROR DE SISTEMA)
	 * @param event
	 * @return
	 */
	public String onFlowProcess2(FlowEvent event) {
		file = new DefaultStreamedContent(FacesContext.getCurrentInstance()
				.getExternalContext().getResourceAsStream("/archivos/defecto.txt"),"text/txt","defecto");
		respuesta = new DefaultStreamedContent(FacesContext.getCurrentInstance()
				.getExternalContext().getResourceAsStream("/archivos/defecto.txt"),"text/txt","defecto");
		
		String newStep = event.getNewStep();
		if(event.getNewStep().equals("tab2")) {
			try(Connection avalon = ( (DataSource)new InitialContext().lookup("sunat") )
					.getConnection()){
				
				GregorianCalendar g = new GregorianCalendar();
				g.setTime(fecha_emision);
				
				this.getDoc().setFechaemision(
						String.format("%04d", g.get(GregorianCalendar.YEAR))+
						String.format("%02d", g.get(GregorianCalendar.MONTH)+1)+
						String.format("%02d", g.get(GregorianCalendar.DATE))
						);
				
				Programa p = new Programa(avalon,null,true);
				List<Cabdocumentos> temporal = new ArrayList<>();
				List<String> razones = new ArrayList<>();
				razones.add("ERROR EN EL SISTEMA");
				temporal.add(doc);
				
				Object[] ans = p.generarResumenBajas(temporal, razones);
				
				this.ticket = (String)ans[0];
				this.setFile(new ByteArrayContent((byte[])ans[1],(String)ans[3], (String)ans[2]));
				this.setRespuesta(new ByteArrayContent((byte[])ans[4],(String)ans[6], (String)ans[5]));
					
			}catch(Exception e) {
				RequestContext.getCurrentInstance()
				.showMessageInDialog(new FacesMessage("ERROR",e.getMessage()));
			}
		}

		
		return newStep;
	}
	
	public boolean isEnviado() {
		return enviado;
	}
	public void setEnviado(boolean enviado) {
		this.enviado = enviado;
	}
	public Cabdocumentos getDoc() {
		return doc;
	}
	public void setDoc(Cabdocumentos doc) {
		this.doc = doc;
	}
	public String getRazon() {
		return razon;
	}
	public void setRazon(String razon) {
		this.razon = razon;
	}
	public StreamedContent getFile() {
		return file;
	}
	public void setFile(StreamedContent file) {
		this.file = file;
	}
	public StreamedContent getRespuesta() {
		return respuesta;
	}
	public void setRespuesta(StreamedContent respuesta) {
		this.respuesta = respuesta;
	}
	public String getTicket() {
		return ticket;
	}
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
	public Date getFecha_emision() {
		return fecha_emision;
	}
	public void setFecha_emision(Date fecha_emision) {
		this.fecha_emision = fecha_emision;
	}
	
	
}

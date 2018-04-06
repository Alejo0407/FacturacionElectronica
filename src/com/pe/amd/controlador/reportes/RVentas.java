package com.pe.amd.controlador.reportes;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
/*
 * 
				<h:commandLink id = "btn" style = "font-size:small" value = "Generar" 
					onclick= "window.open('#{rventasBean.link}')" target = "_blank"/>
 * */
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

import com.pe.amd.modelo.app.Programa;

@ManagedBean(name = "rventasBean")
@ViewScoped
public class RVentas implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5595641962728795422L;
	
	private Integer tipo;
	private Date fechaInicio;
	private Date fechaFin;
	
	private String link;
	@PostConstruct
	public void init() {
		setTipo(-1);
	}
	
	public void generar() {
		String ret = "";
		if(tipo == -1)
			ret = Programa.sistema.getUrlServidor()+"birt/preview?__report=resumen_ventas.rptdesign&fecha_inicio="
			+this.getDateAsString(fechaInicio)+"&fecha_fin="+this.getDateAsString(fechaFin)
			+"&__format=xlsx&__svg=true&__locale=es_ES"
			+ "&__timezone=America%2FBogota&__masterpage=true&__rtl=false&__cubememsize=10"
			+"&__dpi=96&__emitterid=uk.co.spudsoft.birt.emitters.excel.XlsxEmitter&-1989758030";
		else 
			ret = Programa.sistema.getUrlServidor()+"birt/preview?__report=resumen_ventas2.rptdesign&fecha_inicio="
					+this.getDateAsString(fechaInicio)+"&fecha_fin="+this.getDateAsString(fechaFin)
					+"&tipo="+String.format("%02d", tipo)+"&__format=xlsx&__svg=true&__locale=es_ES"
					+ "&__timezone=America%2FBogota&__masterpage=true&__rtl=false&__cubememsize=10"
					+"&__dpi=96&__emitterid=uk.co.spudsoft.birt.emitters.excel.XlsxEmitter&-1989758030";
		
		this.setLink(ret);
		System.out.println("llamo al evento");
		RequestContext.getCurrentInstance().execute("window.open('"+this.getLink()+"')");
		RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(
				"Generado","Documento generado"));
		//return ret;
	}
	private String getDateAsString(Date fecha) {
		GregorianCalendar date = new GregorianCalendar();
		date.setTime(fecha);
			
		String anio = String.format("%04d", date.get(GregorianCalendar.YEAR)),
				mes = String.format("%02d", date.get(GregorianCalendar.MONTH)+1),
				dia = String.format("%02d", date.get(GregorianCalendar.DATE));
		return anio+mes+dia;
	}
	public Integer getTipo() {
		return tipo;
	}

	public void setTipo(Integer tipo) {
		this.tipo = tipo;
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

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
}

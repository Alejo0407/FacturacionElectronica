package com.pe.amd.controlador.lotes;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.primefaces.context.RequestContext;
import org.primefaces.model.ByteArrayContent;
import org.primefaces.model.DefaultStreamedContent;

import com.pe.amd.modelo.app.Programa;
import com.pe.amd.modelo.beans.Cabdocumentos;
import com.pe.amd.modelo.beans.Contingencia;

@ManagedBean(name = "lotesBean")
@ViewScoped
public class Lotes implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Date fecha;

	private boolean contingencia = false;
	private boolean facturas = false;
	private boolean boletas = false;
	private boolean ncredito = false;
	private boolean ndebito = false;
	private boolean corregido = false;
	private List<Tabla> list;
	
	
	public void lotesGenerar() {
		if(fecha == null)
			RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage("Hubo un error en la fecha"));
		else {
			list = new ArrayList<>();
			try (Connection avalon = ( (DataSource)new InitialContext().lookup("sunat") ).getConnection()){
				Programa p = new Programa(avalon,null,true);
				if(this.isContingencia()) {
					Object[] dato = p.generarContingencia(fecha);
					Tabla t = new Tabla();
					t.setCliente("--");t.setHomologado("--");
					t.setNumeracion("--");t.setNumeracion_nueva("--");
					t.setTipo_doc("CONTINGENCIA");
					t.setTotal("--");t.setFile(new ByteArrayContent((byte[])dato[1],
							(String)dato[2],(String)dato[0]));
					list.add(t);
				}
				if(this.isBoletas()) {
					Object temporal = p.generarBoletas(fecha)[0];
					@SuppressWarnings("unchecked")
					List<Cabdocumentos> datos = (List<Cabdocumentos>) temporal;
					for(Cabdocumentos boleta:datos) {
						Tabla t = new Tabla();
						t.setCliente(boleta.getNomcliente());
						t.setFile(new DefaultStreamedContent(boleta.getArchivo().getBinaryStream(),
								"text/xml",boleta.getNombre_archivo()));
						if(boleta.getHomologado() == 0)
							t.setHomologado("SIN GENERAR");
						else if(boleta.getHomologado() == 1)
							t.setHomologado("CORRECTO");
						else if(boleta.getHomologado() == -1)
							t.setHomologado("ERROR EN PRODUCCION");
						else
							t.setHomologado("ERROR - RECHAZO");
						t.setNumeracion(boleta.getSerie() + "-" + boleta.getNumero());
						t.setNumeracion_nueva(boleta.getSerieelec()+"-"+boleta.getNumeroelec());
						t.setTipo_doc("BOLETA");
						t.setTotal(String.format("%.2f", boleta.getTotaldoc()));
						if(Programa.sistema.getValidarBoletas() == 1)
							t.setSunat(new DefaultStreamedContent(boleta.getArchivo_homologado().getBinaryStream(),
									(boleta.getNom_archivo_homologado().contains(".zip")?"application/zip":"text/xml"),
									boleta.getNom_archivo_homologado()));
						list.add(t);
					}
				}
				if(this.isFacturas()) {
					Object temporal = p.generarFacturas(fecha)[0];
					@SuppressWarnings("unchecked")
					List<Cabdocumentos> datos = (List<Cabdocumentos>) temporal;
					for(Cabdocumentos factura:datos) {
						Tabla t = new Tabla();
						t.setCliente(factura.getNomcliente());
						t.setFile(new DefaultStreamedContent(factura.getArchivo().getBinaryStream(),
								"text/xml",factura.getNombre_archivo()));
						if(factura.getHomologado() == 0)
							t.setHomologado("SIN GENERAR");
						else if(factura.getHomologado() == 1)
							t.setHomologado("CORRECTO");
						else if(factura.getHomologado() == -1)
							t.setHomologado("ERROR EN PRODUCCION");
						else
							t.setHomologado("ERROR - RECHAZO");
						t.setNumeracion(factura.getSerie() + "-" + factura.getNumero());
						t.setNumeracion_nueva(factura.getSerieelec()+"-"+factura.getNumeroelec());
						t.setTipo_doc("FACTURA");
						t.setTotal(String.format("%.2f", factura.getTotaldoc()));
						t.setSunat(new DefaultStreamedContent(factura.getArchivo_homologado().getBinaryStream(),
								(factura.getNom_archivo_homologado().contains(".zip")?"application/zip":"text/xml"),
								factura.getNom_archivo_homologado()));
						list.add(t);
					}
				}
			}catch(Exception e) {
				RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage("Error durante la generacion:\n"
						+e.getMessage()));
				System.err.println(e.getMessage());
				list = new ArrayList<>();
				e.printStackTrace();
			}
			
		}
		
	}
	
	public void lotesMigrar() {
		if(fecha == null)
			RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage("Hubo un error en la fecha"));
		else {
			list = new ArrayList<>();
			Connection avalon = null,origen = null;
			try {
				avalon = ( (DataSource)new InitialContext().lookup("sunat") ).getConnection();
				origen = ( (DataSource)new InitialContext().lookup("origen") ).getConnection();
				Programa p = new Programa(avalon,origen,true);
				if(this.contingencia) {
					List<Contingencia> datos = p.migrarContingencia(fecha);
					for(Contingencia linea:datos) {
						Tabla t = new Tabla();
						t.setCliente(linea.getNombrecli());
						t.setHomologado("--");
						t.setTipo_doc(linea.getTipodocum());
						t.setNumeracion(linea.getSerie()+"-"+linea.getNumero());
						t.setNumeracion_nueva("--");
						t.setTotal(String.format("%.2f", linea.getTotalcomp()));
						list.add(t);
					}
				}
				if(this.facturas) {
					List<Cabdocumentos> datos = p.migrarFacturas(fecha, isCorregido());
					for(Cabdocumentos factura:datos) {
						Tabla t = new Tabla();
						t.setCliente(factura.getNomcliente());
						t.setHomologado("SIN ENVIAR");
						t.setNumeracion(factura.getSerie() + "-" + factura.getNumero());
						t.setNumeracion_nueva("--");
						t.setTipo_doc("FACTURA");
						t.setTotal(String.format("%.2f", factura.getTotaldoc()));
						list.add(t);
					}
				}
				if(this.isBoletas()) {
					List<Cabdocumentos> datos = p.migrarBoletas(fecha,isCorregido());
					for(Cabdocumentos boleta:datos) {
						Tabla t = new Tabla();
						t.setCliente(boleta.getNomcliente());
						t.setHomologado("SIN ENVIAR");
						t.setNumeracion(boleta.getSerie() + "-" + boleta.getNumero());
						t.setNumeracion_nueva("--");
						t.setTipo_doc("BOLETA");
						t.setTotal(String.format("%.2f", boleta.getTotaldoc()));
						list.add(t);
					}
				}
			}catch(Exception e) {
				try{avalon.rollback();}catch(Exception e1) {};
				RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage("Error durante la migracion:\n"
						+e.getMessage()));
				System.err.println(e.getMessage());
				list = new ArrayList<>();
				e.printStackTrace();
			}
			finally {
				try {avalon.close();}catch(Exception e) {}
				try {origen.close();}catch(Exception e) {}
			}
			
		}
		
	}
	
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public boolean isFacturas() {
		return facturas;
	}
	public void setFacturas(boolean facturas) {
		this.facturas = facturas;
	}
	public boolean isBoletas() {
		return boletas;
	}
	public void setBoletas(boolean boletas) {
		this.boletas = boletas;
	}
	public boolean isNcredito() {
		return ncredito;
	}
	public void setNcredito(boolean ncredito) {
		this.ncredito = ncredito;
	}
	public boolean isNdebito() {
		return ndebito;
	}
	public void setNdebito(boolean ndebito) {
		this.ndebito = ndebito;
	}
	public boolean isContingencia() {
		return contingencia;
	}
	public void setContingencia(boolean contingencia) {
		this.contingencia = contingencia;
	}
	public List<Tabla> getList() {
		return list;
	}
	public void setList(List<Tabla> list) {
		this.list = list;
	}

	public boolean isCorregido() {
		return corregido;
	}

	public void setCorregido(boolean corregido) {
		this.corregido = corregido;
	}
}

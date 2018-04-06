package com.pe.amd.controlador.config;

import java.io.Serializable;
import java.sql.Connection;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;
import org.primefaces.model.UploadedFile;

import com.pe.amd.controlador.Facturacion;
import com.pe.amd.modelo.app.Programa;

@ViewScoped
@ManagedBean(name = "beanEmpresa")
public class Empresa implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8207618642472043020L;
	
	@ManagedProperty(value = "#{facturacionBean}")
	private Facturacion factura;	
	
	
	private String ruc;
	private String nombre;
	
	private String nombreComercial;
	private String ubigeo;
	private String urbanizacion;
	private String provincia;
	private String departamento;
	private String distrito;
	private String telefono;
	private String fax;
	private String email;
	private String web;
	private String direccion;
	
	private UploadedFile ce;
	private String nce;
	private String pin;
	private String pinRevocar;
	private String alias;
	
	private String usr_secundario;
	private String usr_pass;
	
	private boolean skip;
	
	
	private boolean tab1,tab2,tab3;
	
	public void upload(FileUploadEvent event) {
		this.ce = event.getFile();
		if(ce == null) 
			RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage("Error",
					"Error en la carga del archivo"));
		else {
			System.out.println(ce.getFileName());
			nce = ce.getFileName();
		}
	}
	
	private void loadData() {
		try (Connection avalon = ( (DataSource)new InitialContext().lookup("sunat") ).getConnection()){
			com.pe.amd.modelo.beans.Empresa datos = new Programa(avalon,null,true).getDatosEmpresa();
			this.setRuc(datos.getRuc());
			this.setNombre(datos.getNombre());
			this.setNombreComercial(datos.getNombre_comercial());
			this.setUbigeo(datos.getUbigeo());
			this.setUrbanizacion(datos.getUrbanizacion());
			this.setProvincia(datos.getProvincia());
			this.setDepartamento(datos.getDepartamento());
			this.setDistrito(datos.getDistrito());
			this.setTelefono(datos.getTelefono());
			this.setFax(datos.getFax());
			this.setEmail(datos.getMail_empresa());
			this.setWeb(datos.getWeb());
			this.setPin(datos.getPin());
			this.setPinRevocar(datos.getPin_revocar());
			this.setAlias(datos.getAlias());
			this.setUsr_secundario(datos.getUsrSecundario());
			this.setUsr_pass(datos.getPass());
			this.setDireccion(datos.getDireccion());
			
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage("Error",
					"Error en la carga del archivo: "+ e.getMessage()));
			e.printStackTrace();
		}
	}
	@PostConstruct
	public void init() {
		loadData();
	}
	
	public void submit() {
		HashMap <String,String> datos = new HashMap<>();
		if(this.isTab1()) {
			datos.put("ruc", getRuc());
			datos.put("nombre", getNombre());
		}
		
		if(this.isTab2()) {
			datos.put("nombre_comercial",getNombreComercial().isEmpty()? null :getNombreComercial());
			datos.put("ubigeo",getUbigeo().isEmpty()?null:getUbigeo());
			datos.put("urbanizacion", getUrbanizacion().isEmpty()? null:getUrbanizacion());
			datos.put("provincia", getProvincia().isEmpty()? null:getProvincia());
			datos.put("departamento",getDepartamento().isEmpty()?null:getDepartamento());
			datos.put("distrito", getDistrito().isEmpty()?null:getDistrito());
			datos.put("telefono", getTelefono().isEmpty()?null:getTelefono());
			datos.put("fax",getFax().isEmpty()?null:getFax());
			datos.put("mail_empresa", getEmail().isEmpty()?null:getEmail());
			datos.put("web", getWeb().isEmpty()?null:getWeb());
			datos.put("direccion", getDireccion().isEmpty()?null:getDireccion());
		}
		
		if(this.isTab3()) {
			datos.put("pin",getPin());
			datos.put("pin_revocar", getPinRevocar().isEmpty()?null:getPinRevocar());
			datos.put("alias", getAlias());
			if(ce != null)
				datos.put("nce", ce.getFileName());
			datos.put("usr_secundario", getUsr_secundario());
			datos.put("pass", getUsr_pass());
		}
		
		try (Connection avalon = ( (DataSource)new InitialContext().lookup("sunat") ).getConnection()){
			new Programa(avalon,null,false).actualizarDatosEmpresa(datos,
					(tab3 == false || ce == null)?null:ce.getInputstream(),
							(tab3==false || ce == null)?-66666:ce.getSize());
							
			RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage("Informacion modificada con exito"));
			factura.setCont("default");
			
		}catch(Exception e) {
			RequestContext.getCurrentInstance().showMessageInDialog(
					new FacesMessage("Error en la actualizacion de Datos: "+e.getMessage()));
			e.printStackTrace();
		}
		loadData();
	}
	
	
	public String onFlowProcess(FlowEvent event) {
		String ret = "identificador";
		setTab1(true);
		if(skip) {
			setSkip(false);
			setTab1(true);
			setTab2(true);
			setTab3(true);
			if(event.getNewStep().equals("tab2")) {
				setTab2(false);
				setTab3(false);
			}
			else if(event.getNewStep().equals("tab3")) {
				setTab3(false);
			}
			ret = "tab4";
		}else {
			ret = event.getNewStep();
			if(event.getNewStep().equals("tab2")) 
				setTab2(true);
			else if(event.getNewStep().equals("tab3")) 
				setTab3(true);
		}
		return ret;
	}

	public Facturacion getFactura() {
		return factura;
	}
	public void setFactura(Facturacion factura) {
		this.factura = factura;
	}
	public String getRuc() {
		return ruc;
	}
	public void setRuc(String ruc) {
		this.ruc = ruc;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getNombreComercial() {
		return nombreComercial;
	}
	public void setNombreComercial(String nombreComercial) {
		this.nombreComercial = nombreComercial;
	}
	public String getUbigeo() {
		return ubigeo;
	}
	public void setUbigeo(String ubigeo) {
		this.ubigeo = ubigeo;
	}
	public String getUrbanizacion() {
		return urbanizacion;
	}
	public void setUrbanizacion(String urbanizacion) {
		this.urbanizacion = urbanizacion;
	}
	public String getProvincia() {
		return provincia;
	}
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}
	public String getDepartamento() {
		return departamento;
	}
	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}
	public String getDistrito() {
		return distrito;
	}
	public void setDistrito(String distrito) {
		this.distrito = distrito;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getWeb() {
		return web;
	}
	public void setWeb(String web) {
		this.web = web;
	}
	public UploadedFile getCe() {
		return ce;
	}
	public void setCe(UploadedFile ce) {
		this.ce = ce;
	}
	public String getPin() {
		return pin;
	}
	public void setPin(String pin) {
		this.pin = pin;
	}
	public String getPinRevocar() {
		return pinRevocar;
	}
	public void setPinRevocar(String pinRevocar) {
		this.pinRevocar = pinRevocar;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public boolean isSkip() {
		return skip;
	}
	public void setSkip(boolean skip) {
		this.skip = skip;
	}
	public boolean isTab2() {
		return tab2;
	}
	public void setTab2(boolean tab2) {
		this.tab2 = tab2;
	}
	public boolean isTab1() {
		return tab1;
	}
	public void setTab1(boolean tab1) {
		this.tab1 = tab1;
	}
	public boolean isTab3() {
		return tab3;
	}
	public void setTab3(boolean tab3) {
		this.tab3 = tab3;
	}


	@Override
	public String toString() {
		return "Empresa [factura=" + factura + ", ruc=" + ruc + ", nombre=" + nombre + ", nombreComercial="
				+ nombreComercial + ", ubigeo=" + ubigeo + ", urbanizacion=" + urbanizacion + ", provincia=" + provincia
				+ ", departamento=" + departamento + ", distrito=" + distrito + ", telefono=" + telefono + ", fax="
				+ fax + ", email=" + email + ", web=" + web + ", ce=" + ce + ", pin=" + pin + ", pinRevocar="
				+ pinRevocar + ", alias=" + alias + ", skip=" + skip + ", tab1=" + tab1 + ", tab2=" + tab2 + ", tab3="
				+ tab3 + "]";
	}

	public String getNce() {
		return nce;
	}

	public void setNce(String nce) {
		this.nce = nce;
	}

	public String getUsr_secundario() {
		return usr_secundario;
	}

	public void setUsr_secundario(String usr_secundario) {
		this.usr_secundario = usr_secundario;
	}

	public String getUsr_pass() {
		return usr_pass;
	}

	public void setUsr_pass(String usr_pass) {
		this.usr_pass = usr_pass;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
}

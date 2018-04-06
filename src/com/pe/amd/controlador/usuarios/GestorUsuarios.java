package com.pe.amd.controlador.usuarios;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FlowEvent;

import com.pe.amd.controlador.Facturacion;
import com.pe.amd.modelo.app.Programa;
import com.pe.amd.modelo.beans.Usuario;

@ManagedBean(name = "gusrBean")
@ViewScoped
public class GestorUsuarios implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1050790420443632894L;
	
	private String ident;
	private String pass;
	private String confirmpass;
	
	private String dni;
	private String correo;
	private String nombres;
	private String apellidos;
	
	private Integer rango = 1;//USUARIO NORMAL
	private String mpass;//contraseña a mpstrar
	
	private boolean skip;
	private boolean seleccionado;
	
	@ManagedProperty(value = "#{facturacionBean}")
	private Facturacion factura;	
	private List<Usuario> idents;//Guarda todos los identificadores existentes
	private Usuario selectedUsr = null;
	
	public void show() {
		System.out.println("show invoked!");
		if(selectedUsr != null)
			System.out.println(selectedUsr.getId());
		else
			System.out.println("Es nulo");
	}
	
	@PostConstruct
	public void init() {
		load();
	}
	
	public void submit() {
		try (Connection avalon = ( (DataSource)new InitialContext().lookup("sunat") ).getConnection();){
			new Programa(avalon,null,true).crearUsuario(getIdent(),getPass(),getDni(),
					getCorreo(),getNombres(),getApellidos(),getRango());
			RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage("El usuario ["+this.getIdent()+"]"
					+ " fue creado con exito"));
			factura.setCont("default");
			close();
			load();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage("Error en la creacion del Usuario: "
			+e.getMessage()));
			e.printStackTrace();
		}
	}
	
	public void modify() {
		Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		this.ident = params.get("identificador");
		System.out.println("llamo al modificador: " + ident);
		seleccionado = true;
	}
	
	public void eliminate() {
		System.out.println(this.toString());
	}
	
	public String onFlowProcessGenerar(FlowEvent event) {
		String ret = "identificador";
		if(skip) {
			skip = false;
			setDni(null);
			setCorreo(null);
			setNombres(null);
			setApellidos(null);
			ret = "confirmar";
			mpass = "";
			for(int i = 0 ; i < pass.length() ; i++) {
				if(i < pass.length()/2)
					mpass += String.valueOf(pass.charAt(i));
				else
					mpass += "*";
			}
		}else {
			if(validar())
				ret = event.getNewStep();
		}
		
		return ret;
	}
	
	private boolean validar() {
		setIdent(ident.trim());
		setPass(pass.trim());
		setConfirmpass(confirmpass.trim());
		//Para el IDENT
		if(!ident.matches("[a-zA-Z0-9]+")) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("El identificador"
					+ " no es alfanumerico"));
			return false;
		}
		for(Usuario x:idents)
			if(x.getId().equals(ident)){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("El identificador ya existe"));
				return false;
			}
		
		if(!pass.equals(confirmpass)) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("El segundo valor de la contraseña"
					+ " no es identico al primero"));
			return false;
		}
		mpass = "";
		for(int i = 0 ; i < pass.length() ; i++) {
			if(i < pass.length()/2)
				mpass += String.valueOf(pass.charAt(i));
			else
				mpass += "*";
		}
		return true;
	}
	private void load() {
		
		try (Connection avalon = ( (DataSource)new InitialContext().lookup("sunat") ).getConnection();){
			idents = new Programa(avalon,null,true).getUsuarios();
		}
		catch (Exception e) {
			RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage("Error en el listado de Usuarios: "
					+e.getMessage()));
			e.printStackTrace();
		}
		if(idents == null)
			idents = new ArrayList<>();
	}
	
	private void close() {
		setIdent(null);
		setPass(null);
		setConfirmpass(null);
		setDni(null);
		setCorreo(null);
		setNombres(null);
		setApellidos(null);
		setMpass(null);
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
	public String getDni() {
		return dni;
	}
	public void setDni(String dni) {
		this.dni = dni;
	}
	public String getCorreo() {
		return correo;
	}
	public void setCorreo(String correo) {
		this.correo = correo;
	}
	public String getNombres() {
		return nombres;
	}
	public void setNombres(String nombres) {
		this.nombres = nombres;
	}
	public String getApellidos() {
		return apellidos;
	}
	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}
	public Integer getRango() {
		return rango;
	}
	public void setRango(Integer rango) {
		this.rango = rango;
	}
	public String getConfirmpass() {
		return confirmpass;
	}
	public void setConfirmpass(String confirmpass) {
		this.confirmpass = confirmpass;
	}
	public boolean isSkip() {
		return skip;
	}
	public void setSkip(boolean skip) {
		this.skip = skip;
	}
	public String getMpass() {
		return mpass;
	}
	public void setMpass(String mpass) {
		this.mpass = mpass;
	}
	@Override
	public String toString() {
		return "GestorUsuarios [ident=" + ident + ", pass=" + pass + ", confirmpass=" + confirmpass + ", dni=" + dni
				+ ", correo=" + correo + ", nombres=" + nombres + ", apellidos=" + apellidos + ", rango=" + rango
				+ ", mpass=" + mpass + ", skip=" + skip + ", idents=" + idents + "]";
	}
	public Facturacion getFactura() {
		return factura;
	}

	public void setFactura(Facturacion factura) {
		this.factura = factura;
	}

	public List<Usuario> getIdents() {
		return idents;
	}

	public void setIdents(List<Usuario> idents) {
		this.idents = idents;
	}

	public boolean isSeleccionado() {
		return seleccionado;
	}

	public void setSeleccionado(boolean seleccionado) {
		this.seleccionado = seleccionado;
	}

	public Usuario getSelectedUsr() {
		return selectedUsr;
	}

	public void setSelectedUsr(Usuario selectedUsr) {
		this.selectedUsr = selectedUsr;
	}
}

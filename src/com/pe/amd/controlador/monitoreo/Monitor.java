package com.pe.amd.controlador.monitoreo;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;

import com.pe.amd.modelo.app.Programa;
import com.pe.amd.modelo.beans.Cabdocumentos;

@ManagedBean(name = "monitorBean")
@ViewScoped
public class Monitor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2251882794879084725L;
	/**
	 * 
	 */
	
	private Date fecha;
	private List<Tabla> list;
	//
	@PostConstruct
	public void init() {
		fecha = new Date();
		obtenerLista();
	}
	
	public void obtenerLista()  {
		setList(new java.util.ArrayList<>());
		InitialContext ctx;
		try {
			ctx = new InitialContext();
			DataSource ds = (DataSource)ctx.lookup("sunat");
			
			try(Connection conn = ds.getConnection()){
				Programa p = new Programa(conn,null,true);
				
				GregorianCalendar gc = new GregorianCalendar();
				gc.setTime(fecha);
				
				String fec = String.format("%04d", gc.get(GregorianCalendar.YEAR))
						+String.format("%02d", gc.get(GregorianCalendar.MONTH)+1)
						+String.format("%02d", gc.get(GregorianCalendar.DATE));

				String parametros[] = {"homologado <> 0","anulado = 0", "fechaemision = '"+fec+"'"};
				String orderBy[] = {"serieelec","CAST(numeroelec AS SIGNED)"};
				
				List<Cabdocumentos> datos = p.getDocumentos(parametros, orderBy);
				for(int i = 0 ; i < datos.size() ; i++) {
					Tabla e = new Tabla();
					Cabdocumentos doc = datos.get(i);
					e.setNumeracion_nueva(doc.getSerieelec()+"-"+doc.getNumeroelec());
					e.setCliente(doc.getNomcliente());
					e.setFecha(doc.getFechaemision());
					e.setFile(new DefaultStreamedContent(
							doc.getArchivo().getBinaryStream(),
							"text/css",
							doc.getNombre_archivo()
							));
					if(doc.getArchivo_homologado() != null)
						e.setSunat(new DefaultStreamedContent(
								doc.getArchivo_homologado().getBinaryStream(),
								doc.getNom_archivo_homologado().contains(".zip")?"application/zip":"text/xml",
								doc.getNom_archivo_homologado()
								));
					e.setTotal(Monitor.format(doc.getTotaldoc(),2));
					e.setNumeracion(doc.getSerie()+"-"+doc.getNumero());
					if(doc.getTipodocumento().equals("03"))
						e.setTipo_doc("BOLETA DE VENTA ELECTRÓNICA");
					else
						e.setTipo_doc("FACTURA ELECTRÓNICA");
					
					if(doc.getHomologado() == 1)
						e.setHomologado("Aceptado");
					else if(doc.getHomologado() == -1)
						e.setHomologado("Error Produccion");
					else
						e.setHomologado("Rechazado");
					
					e.setLink(Programa.sistema.getUrlServidor()+"birt/preview?__report=factura.rptdesign&transaccion="
						+doc.getTransaccion()+"&documento="+e.getTipo_doc()
						+"&monto="+"S/ "+Monitor.conversionTotal(STR(doc.getTotaldoc(),2))+" SOLES"
						+"&afectas="+"S/ "+Monitor.format(doc.getValventaafe(), 2)
						+"&inafectas="+"S/ "+Monitor.format(doc.getValventaina(), 2)
						+"&exoneradas="+"S/ "+Monitor.format(doc.getValventaexo(), 2)
						+"&subtotal="+"S/ "+Monitor.format(doc.getTotaldoc()-doc.getIgv(), 2)
						+"&anticipos="+"S/ "+Monitor.format(null, 2)
						+"&descuentos="+"S/ "+Monitor.format(null, 2)
						+"&isc="+"S/ "+Monitor.format(doc.getIsc(), 2)
						+"&igv="+"S/ "+Monitor.format(doc.getIgv(), 2)
						+"&total="+"S/ "+Monitor.format(doc.getTotaldoc(), 2)
						+"&__format=pdf"
					);
					list.add(e);
				}
			} 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println(e.getMessage());
			RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage("Error",e.getMessage()));
		}
		
	}
	
	public static void main(String[] args) {
		Double d1 = null;
		Double d2 = 0.4;
		Double d3 = 0.56;
		Double d4 = 0.0;
		Double d5 = 12.51;
		Double d6 = 1212545.6;
		
		System.out.println(Monitor.format(d1, 2));
		System.out.println(Monitor.format(d2, 2));
		System.out.println(Monitor.format(d3, 2));
		System.out.println(Monitor.format(d4, 2));
		System.out.println(Monitor.format(d5, 2));
		System.out.println(Monitor.format(d6, 2));
	}
	public static String format(Double temp, int decimales) {
		String number = "0.00";
		
		if(temp != null) {
			String rounded = STR(temp,decimales);
			rounded.replace(',', '.');
			String entera = rounded.substring(0,rounded.indexOf('.'));
			
			String retorno  = "";
			int k = 0;
			for(int i = entera.length()-1 ; i >= 0 ; i-- ) {
				if( k % 3 == 0 && k != 0) 
					retorno = "," + retorno;
				
				k++;
				retorno =  String.valueOf(entera.charAt(i)) + retorno;
				
			}
			retorno += rounded.substring(rounded.length()-decimales-1);
			
			number = retorno;
		}
		return number;
	}
	
	public static String STR(double temp, int decimales)
	{
		
		
		int x = (int) temp;
		
		String aux = String.valueOf(x);
		String aux2 = String.valueOf(temp);
		
		int dif = aux2.length() - aux.length() - 1;
		
		if(dif < decimales)
		{
			for(int i = 0 ; i < (decimales-dif) ; i++)
			{
				aux2 = aux2+"0";
			}
		}
		
		
		return aux2;
		
		
	}
	public static String numero(String num, int op)
	{
		
		String aux ="";
		int  n_aux1 = 0;
		int n_aux2 = 0;
		int n_aux3 = 0;
		
		int t = Integer.parseInt(num);
		
		//Obtengo la Primera Cifra
		n_aux1 = t / 100;
		t = t - (100 * n_aux1);
		
		//Obtengo la Segunda Cifra
		n_aux2 = t / 10;
		t = t - (10 * n_aux2);
		
		//Obtengo la Tercera Cifra
		n_aux3 = t / 1;
		
		//Si el numero es cero
		if(n_aux3 == 0 && n_aux2 == 0 && n_aux1 == 0 && op == 0)
			return "CERO";
		
		//Para la primera cifra
		switch(n_aux1)
		{
		case 0:
			break;
		case 1:
			if(Integer.parseInt(num) == 100)
				aux+=("CIEN");//Caso especifico 
			else
				aux+=("CIENTO ");
			break;
		case 2:
			if(n_aux2 == 0 && n_aux3 == 0)
				aux+=("DOSCIENTOS");
			else
				aux+=("DOSCIENTOS ");
			break;
		case 3:
			if(n_aux2 == 0 && n_aux3 == 0)
				aux+=("TRESCIENTOS");
			else
				aux+=("TRESCIENTOS ");
			break;
		case 4:
			if(n_aux2 == 0 && n_aux3 == 0)
				aux+=("CUATROCIENTOS");
			else
				aux+=("CUATROCIENTOS ");
			break;
		case 5:
			if(n_aux2 == 0 && n_aux3 == 0)
				aux+=("QUINIENTOS");
			else
				aux+=("QUINIENTOS ");
			break;
		case 6:
			if(n_aux2 == 0 && n_aux3 == 0)
				aux+=("SEISCIENTOS");
			else
				aux+=("SEISCIENTOS ");
			break;
		case 7:
			if(n_aux2 == 0 && n_aux3 == 0)
				aux+=("SETECIENTOS");
			else
				aux+=("SETECIENTOS ");
			break;
		case 8:
			if(n_aux2 == 0 && n_aux3 == 0)
				aux+=("OCHOCIENTOS");
			else
				aux+=("OCHOCIENTOS ");
			break;
		case 9:
			if(n_aux2 == 0 && n_aux3 == 0)
				aux+=("NOVECIENTOS");
			else
				aux+=("NOVECIENTOS ");
			break;
		}
		
		boolean I_o_Y = false;
		switch(n_aux2)
		{
		case 0:
			break;
		case 1://Para los numeros del 10 al 15
			if(n_aux3 >= 0 && n_aux3 <=5)
			{
				if(n_aux3 == 0)
					aux+=("DIEZ");
				else if(n_aux3 == 1)
					aux+=("ONCE");
				else if(n_aux3 == 2)
					aux+=("DOCE");
				else if(n_aux3 == 3)
					aux+=("TRECE");
				else if(n_aux3 == 4)
					aux+=("CATORCE");
				else if(n_aux3 == 5)
					aux+=("QUINCE");
			
				n_aux3 = 0;//Me aseguro que no quede la cifra al aire
			}
			else
			{
				aux+=("DIEC");
				I_o_Y = true;	
			}
			break;
		
		case 2:
			if(n_aux3 == 0)
				aux+=("VEINTE");
			else if(n_aux3 == 0 && op == 1)
				aux+=("VEINTE");
			else
			{
				aux+=("VEINT");
				I_o_Y = true;
			}
				
			break;
		
		case 3:
			aux+=("TREINTA");
			break;
		case 4:
			aux+=("CUARENTA");
			break;
		case 5:
			aux+=("CINCUENTA");
			break;
		case 6:
			aux+=("SESENTA");
			break;
		case 7:
			aux+=("SETENTA");
			break;
		case 8:
			aux+=("OCHENTA");
			break;
		case 9:
			aux+=("NOVENTA");
			break;
				
		}
		
		if(Integer.parseInt(num) >= 10 && n_aux3 != 0)//Si el numero es mayor a 10 y la ultima cifra no es cero
			if(I_o_Y)
				aux += "I";
			else if(n_aux2 != 0)//Solo para cuando las decenas existan
				aux+=(" Y "); 
		
		switch(n_aux3)
		{
		case 1:
			if(op == 0)
				aux+=("UNO");
			else if( op == 1 && n_aux1 == 0 && n_aux2 == 0)
				aux+= "";
			else 
				aux += "UN";
			break;
		case 2:
			aux+=("DOS");
			break;
		case 3:
			aux+=("TRES");
			break;
		case 4:
			aux+=("CUATRO");
			break;
		case 5:
			aux+=("CINCO");
			break;
		case 6:
			aux+=("SEIS");
			break;
		case 7:
			aux+=("SIETE");
			break;
		case 8:
			aux+=("OCHO");
			break;
		case 9:
			aux+=("NUEVE");
			break;
		}
		
		return aux;
	}
	
	public static String conversionTotal(String num)
	{
		String retorno="";
		
		String aux_cad;
		int aux_dato;
		
		double numero = 0;
		int entero = 0;
		
		if(Double.parseDouble(num) == 0)
		{
			return "CERO CON - 00/100";
		}
		
		ArrayList<String> cadena = new ArrayList<String>();
		
		try
		{
			numero = Double.parseDouble(num);
			
			if(numero < 0){
				numero = Math.abs(numero);
				retorno += "MENOS ";
			}
			entero = (int)numero;
			aux_cad = String.valueOf(entero);
			
			
		}
		catch(Exception e)
		{
			return null;
		}
		
		int j = 0;
		for(int i = 0 ; i < 11 ; i++)
		{
			try
			{
				if(i < ( 12-aux_cad.length()) )
					cadena.add("0");
				else
				{
					cadena.add(aux_cad.substring(j, j+1));
					j++;
				}
			}
			catch(Exception e)
			{
				cadena.add("0");
			}
		}
		cadena.add(aux_cad.substring(j));
		
		
		
		aux_cad = cadena.get(0) + cadena.get(1) + cadena.get(2);
		aux_dato = Integer.parseInt(aux_cad);
		
		
		if(aux_dato != 0)
			if(aux_dato != 1)
				retorno += (numero(aux_cad, 2) + " BILLONES  ");
			else
				retorno += (numero(aux_cad, 2) + " BILLON  ");

		aux_cad = cadena.get(3) + cadena.get(4) + cadena.get(5);
		aux_dato = Integer.parseInt(aux_cad);
		
		
		if(aux_dato != 0)
			if(aux_dato !=1)
				retorno += (numero(aux_cad, 2) + " MILLONES  ");
			else
				retorno += (numero(aux_cad, 2) + " MILLON  ");
		
		
		aux_cad = cadena.get(6) + cadena.get(7) + cadena.get(8);
		aux_dato = Integer.parseInt(aux_cad);
		
		
		
		if(aux_dato != 0)
			if(aux_dato !=1)
				retorno += (numero(aux_cad, 1) + " MIL ");
			else
				retorno += (numero(aux_cad, 1) + "MIL ");
		
		aux_cad = cadena.get(9) + cadena.get(10) + cadena.get(11);
		aux_dato = Integer.parseInt(aux_cad);
		
		if(aux_dato >= 0)
			retorno += (numero(aux_cad, 0) + " ");
		
		
		if(numero != entero)
		{
			
			double x = numero - entero;
			x = x*100;
			x = Math.round(x);
			
			int t = (int) x;
			String xs = (x<10) ? "0" + String.valueOf(t):String.valueOf(t);
			
			if(retorno.equals(""))
				retorno +="CON - " + xs+"/100";
			else
				retorno +="CON - " + xs+"/100";
			
		}
		
		else
		{
			if(retorno.equals(""))
				retorno +="CON - " + "00/100";
			else
				retorno +="CON - " + "00/100";
			
		}
		retorno.trim();
		retorno.trim();
		return retorno;
		
	}
	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public List<Tabla> getList() {
		return list;
	}

	public void setList(List<Tabla> list) {
		this.list = list;
	}
}

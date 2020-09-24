package MultiMobile.Utils;

import java.io.File;
import java.sql.Time;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.kroz.activerecord.ActiveRecordBase;
import org.kroz.activerecord.ActiveRecordException;

import br.inf.commerce.multimobilegpa.android.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import MultiMobile.DAO.CargaFila;
import MultiMobile.DAO.ClienteMultisoftware;
import MultiMobile.DAO.MotivoSaidaFila;
import MultiMobile.DAO.NotificacaoFila;
import MultiMobile.DAO.Usuario;

@SuppressLint("SimpleDateFormat")
public class Funcoes {
	
	public static String completarParaEsquerda(String value, char c, int size) {
		String result = value;

		while (result.length() < size) {
			result = c + result;
		}

		return result;
	}
		
	public static int retornaUltimoDiaMes(Date diaAtual){
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(diaAtual);
		         
		return cal.getActualMaximum( Calendar.DAY_OF_MONTH );
	}
	
	public static boolean verificaConexao(Context ctx) {
	    boolean conectado;
		ConnectivityManager conectivtyManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
	    if (conectivtyManager.getActiveNetworkInfo() != null
	            && conectivtyManager.getActiveNetworkInfo().isAvailable()
	            && conectivtyManager.getActiveNetworkInfo().isConnected()) {
	    	conectado = true;
	    } else {
	        conectado = false;
	    }
	    return conectado;
	}
		
	public static String removerAspas(String valor){
		if (!valor.isEmpty() && valor != "")
			return valor.replaceAll("\"", "");
		else
			return valor;
	}
	
	public static String removerCaracteresSincronismo(String valor){
		if (!valor.isEmpty() && valor != "")
			valor = valor.replaceAll("%", "");
		if (!valor.isEmpty() && valor != "")
			valor = valor.replaceAll("\"", "");
		if (!valor.isEmpty() && valor != "")
			valor = valor.replaceAll("&", "");
		return valor;
	}
	
	public static boolean isNullOrEmpty(String s) {
        return (s == null || s.equals("") || s.equals("null"));
    }
	
	public static String getDeviceSerial() {
		String serial = Build.ID;
		return capitalize(serial);	
	}
	
	public static String getDeviceName() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) {
			return capitalize(model);
		} else {
		    return capitalize(manufacturer) + " " + model;
		}
	}
	
	public static String getDeviceIMEI(Context ctx) {
		String imei = "";
		TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		imei = tm.getDeviceId();// IMEI No.
		
		if (isNullOrEmpty(imei))// Android Unique ID
			imei = Secure.getString(ctx.getContentResolver(), Secure.ANDROID_ID);
		
		return capitalize(imei);
	}
	
	public static String capitalize(String s) {
		if (s == null || s.length() == 0) {
			return "";
		}
		char first = s.charAt(0);
		if (Character.isUpperCase(first)) {
			return s;
		} else {
		    return Character.toUpperCase(first) + s.substring(1);
		}
	}
	
	public static double arredondar(double valor, int casas, int ceilOrFloor) {  
	    double arredondado = valor;  
	    arredondado *= (Math.pow(10, casas));  
	    if (ceilOrFloor == 0) {  
	        arredondado = Math.ceil(arredondado);             
	    } else {  
	        arredondado = Math.floor(arredondado);  
	    }  
	    arredondado /= (Math.pow(10, casas));  
	    return arredondado;  
	}
	
	public static String dateToString(Date data){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(data);
	}
	
	public static Date stringToDate(String data){
		try {
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");  
			return new java.sql.Date(format.parse(data).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
			return null; 
		}		
	}
	
	public static String PegarDataAtual(){
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy"); 
		Date date = new Date(); 
		return dateFormat.format(date);
	}
	
	public static String timeToString(Date data){
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		return sdf.format(data);
	}
	
	public static Date stringToTime(String data){
		try {
			SimpleDateFormat format = new SimpleDateFormat("HH:mm");  
			return new java.sql.Date(format.parse(data).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Time stringToTimeTime(String hora){
		try {
			SimpleDateFormat format = new SimpleDateFormat("HH:mm");  
			return new java.sql.Time(format.parse(hora).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
			return null; 
		}
	}
	
	public static String longToString(long data){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Calendar calendar = Calendar.getInstance();
	    calendar.setTimeInMillis(data);
		
		return sdf.format(calendar.getTime());
	}
	
	public static long stringToLong(String data){
		try {
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			Date date = format.parse(data);			
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public static String PegarHoraAtual(){
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss"); 
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	public static String PegarHoraAtualSemSegundos(){
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm"); 
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	public static String PegarDataHoraAtual(){
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss");
		Date date = new Date(); 
		return dateFormat.format(date);
	}
	
	public static long stringTimeToLong(String hora){
		try {
			SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
			Date date = format.parse(hora);
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public static String formatarData(int dia, int mes, int ano){
		String dataSelecionada;
		String diaSelecionado = String.valueOf(dia);
		String mesSelecionado = String.valueOf(mes + 1);
		String anoSelecionado = String.valueOf(ano);
		
		if (diaSelecionado.length() == 1)
			dataSelecionada = "0" + diaSelecionado;
		else
			dataSelecionada = diaSelecionado;
		if (mesSelecionado.length() == 1)
			dataSelecionada = dataSelecionada + "/0" + mesSelecionado;
		else
			dataSelecionada = dataSelecionada + "/" + mesSelecionado;
		
		dataSelecionada = dataSelecionada + "/" + anoSelecionado;
		return dataSelecionada;
	}
	
	public static String formatarHora(int hora, int minuto){
		String horaSelecionada;
		String horaSelecionado = String.valueOf(hora);
		String minutoSelecionado = String.valueOf(minuto);
		
		if (horaSelecionado.length() == 1)
			horaSelecionada = "0" + horaSelecionado;
		else
			horaSelecionada = horaSelecionado;
		if (minutoSelecionado.length() == 1)
			horaSelecionada = horaSelecionada + ":0" + minutoSelecionado;
		else
			horaSelecionada = horaSelecionada + ":" + minutoSelecionado;
		
		return horaSelecionada;
	}
	
	public static String somenteNumeros(String str){
		return str.replaceAll("[^0-9]", "");
	}
	
	public static String RetornaDataHoraMesAnteriorWebService(){
		SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmss"); 
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, -30);
		date = calendar.getTime();
		return dateFormat.format(date);
	}
	
	public static String ConverterDataWebServiceMobile(String dataWebService){
		Date dataConvertida = null;
		SimpleDateFormat sdf = null;
		String dataRetorno;
		try {
			sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
			dataConvertida = sdf.parse(dataWebService);
			
			sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			dataRetorno = sdf.format(dataConvertida);
			return dataRetorno;
		} catch (ParseException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public static int retornarCodigoString(String str){
		
		if (str.contains("Cod.:")){
			int tamanhoStr = str.length();
			int posicaoCod = str.indexOf("Cod.:");
			String codigo = str.substring(posicaoCod + 6, tamanhoStr);
			return Integer.parseInt(somenteNumeros(codigo));
		}else		
			return 0;
	}
	
	public static String formatarCEP(String strValor) {
		if (strValor != null && strValor.trim() != "" && !strValor.equals(null) && !strValor.equals(""))
			return FormataString(strValor, "#####-###", false);
		else
			return "";
    }
	
	public static String formatarCPFCNPJ(String strValor) {
		if (strValor != null && strValor.trim() != "" && !strValor.equals(null) && !strValor.equals("")){
			if (strValor.length() == 11)
				return FormataString(strValor, "###.###.###-##", false); 
			else
				return FormataString(strValor, "##.###.###/####-##", false);
		} else
			return "";
    }
		
	public static String formatarTelefone(String strNumero) {
		try {
			if (strNumero != null && strNumero.trim() != "" && !strNumero.equals(null) && !strNumero.equals("")){
				strNumero = strNumero.replace(" ", "0");
				if (strNumero.length() == 10)
					return FormataString(strNumero, "(##) ####-####", true); 
				else if (strNumero.length() == 11)
					return FormataString(strNumero, "(##) ####-#####", true);
				else
					return "";
			} else
				return "";
		} catch (Exception e) {
			return "";
		}
    }
	
	public static String FormataString(String valor, String mascara, boolean telefone) {
	    
        String dado = "";        	
        // remove caracteres nao numericos
        for ( int i = 0; i < valor.length(); i++ )  {
            char c = valor.charAt(i);
            if ( Character.isDigit( c ) ) { dado += c; }
        }

        int indMascara = mascara.length();
        int indCampo = dado.length();

        for ( ; indCampo > 0 && indMascara > 0; ) {
            if ( mascara.charAt( --indMascara ) == '#' ) { indCampo--; }
        }
        
        if (telefone)
        	indMascara += - 1;
        
        String saida = "";
        for ( ; indMascara < mascara.length(); indMascara++ ) {    
            saida += ( ( mascara.charAt( indMascara ) == '#' ) ? dado.charAt( indCampo++ ) : mascara.charAt( indMascara ) );
        }    
        return saida;
    }	
	
	public static String formatarValor(double valor, boolean comRS){
		NumberFormat nf = new DecimalFormat("0.00");
		if (comRS)
			return "R$ "+nf.format(valor);
		else
			return nf.format(valor);					
	}
	
	public static String formatarValorReal(double valor, boolean comRS){
		NumberFormat nf = new DecimalFormat("#,##0.00");//new DecimalFormat("0.00");
		if (comRS)
			return "R$ "+nf.format(valor);
		else
			return nf.format(valor);
	}
	
	public static String LimparDadosAplicativo(ActiveRecordBase db){
		try {
			db.delete(Usuario.class, null, null);
			db.delete(ClienteMultisoftware.class, null, null);
			db.delete(MotivoSaidaFila.class, null, null);
			db.delete(NotificacaoFila.class, null, null);
			db.delete(CargaFila.class, null, null);
			
			return "";
		} catch (ActiveRecordException e1) {
			e1.printStackTrace();
			return "Problemas ao limpar os dados do aplicativo.";
		}
	}
	
	public static String RetornaDataSemHora(String dataHora){
		if (isNullOrEmpty(dataHora))
			return "";
		return dataHora.substring(0, 10);
	}
	
	public static String RetornaHoraSemData(String dataHora){
		if (isNullOrEmpty(dataHora))
			return "";
		return dataHora.substring(11, 16);
	}
	
	//Retorna listas das tabelas	
	public static List<NotificacaoFila> retornListaNotificacaoFila(ActiveRecordBase db){
		try {
			return db.find(NotificacaoFila.class, false, null, null, null, null, "IDCOMMERCE DESC, _id DESC", "100");
		} catch (ActiveRecordException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<CargaFila> retornListaCargaFila(ActiveRecordBase db, String dataInicial, String dataFinal){
		try {
			if (!dataInicial.equals("") && !dataFinal.equals("")){
				long dataIni = stringToLong(dataInicial);
				long dataFim = stringToLong(dataFinal);
				
				return db.find(CargaFila.class, false, "DATA BETWEEN ? AND ?", new String[] {String.valueOf(dataIni), String.valueOf(dataFim)}, null, null, "DATA DESC, _id DESC", null);
			}
			else			
				return db.find(CargaFila.class, false, null, null, null, null, "DATA DESC, _id DESC", "100");
		} catch (ActiveRecordException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<String> retornaMotivoSaidaFila(ActiveRecordBase db){
		List<String> retorno = new ArrayList<String>();
		try {
			List<MotivoSaidaFila> listMotivoSaidaFila = db.find(MotivoSaidaFila.class, false, null, null, null, null, "IDCOMMERCE", null);
			if (listMotivoSaidaFila.size() > 0)
				retorno.add("Selecione");
			
			for (int i = 0; i < listMotivoSaidaFila.size(); i++) {
				retorno.add(listMotivoSaidaFila.get(i).descricao + " Cod.: " + listMotivoSaidaFila.get(i).id_commerce);
			}
			return retorno;
		} catch (ActiveRecordException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//Retorna first ou default das tabelas
	
	public static ClienteMultisoftware retornaClienteMultisoftware(ActiveRecordBase db, int codigoClienteMultisoftware){
		try {
			List<ClienteMultisoftware> retornoClienteMultisoftware = null;
			retornoClienteMultisoftware = db.find(ClienteMultisoftware.class, "IDCOMMERCE = ?", new String[] { String.valueOf(codigoClienteMultisoftware)});
			if (retornoClienteMultisoftware != null && retornoClienteMultisoftware.size() > 0)
				return retornoClienteMultisoftware.get(0);
			else
				return null;
		} catch (ActiveRecordException e) {
			e.printStackTrace();
			return null;
		}
	}
		
	public static Usuario retornaUsuario(ActiveRecordBase db, String usuario){
		try {
			List<Usuario> retornoUsuario = null;
			retornoUsuario = db.find(Usuario.class, "USUARIO = ?", new String[] { String.valueOf(usuario)});
			if (retornoUsuario != null && retornoUsuario.size() > 0)
				return retornoUsuario.get(0);
			else
				return null;
		} catch (ActiveRecordException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Usuario retornaUsuario(ActiveRecordBase db, int codigoUsuario){
		try {
			List<Usuario> retornoUsuario = null;
			retornoUsuario = db.find(Usuario.class, "IDCOMMERCE = ?", new String[] { String.valueOf(codigoUsuario)});
			if (retornoUsuario != null && retornoUsuario.size() > 0)
				return retornoUsuario.get(0);
			else
				return null;
		} catch (ActiveRecordException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Usuario retornaUsuario(ActiveRecordBase db){
		try {
			List<Usuario> retornoUsuario = null;
			retornoUsuario = db.findAll(Usuario.class);
			if (retornoUsuario != null && retornoUsuario.size() > 0)
				return retornoUsuario.get(0);
			else
				return null;
		} catch (ActiveRecordException e) {
			e.printStackTrace();
			return null;
		}
	}
		
	public static ClienteMultisoftware retornaClienteMultisoftware(ActiveRecordBase db){
		try {
			List<ClienteMultisoftware> retornoClienteMultisoftware = null;
			retornoClienteMultisoftware = db.find(ClienteMultisoftware.class, null, null);
			if (retornoClienteMultisoftware != null && retornoClienteMultisoftware.size() > 0)
				return retornoClienteMultisoftware.get(0);
			else
				return null;
		} catch (ActiveRecordException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean validaVersaoAplicativo(Context context, String version) {
		int versionPermited = Integer.parseInt(somenteNumeros(version));
		int versionApp = Integer.parseInt(somenteNumeros(context.getString(R.string.app_version)));
		
		if (versionApp >= versionPermited)
			return true;
		else
			return false;
    }
	
	public static String mensagemVersaoAplicativo(String version) {
		return "Versão do aplicativo está desatualizada, favor atualizar!\nVersão atual: " + version;
    }
	
	public static File getOutputMediaFile(String nomeArquivo, Context ctx){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this. 
	    File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
	            + "/Android/data/"
	            + ctx.getPackageName()
	            + "/Files");

	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            return null;
	        }
	    } 
	    // Create a media file name	    
	    File mediaFile;
	    String mImageName="MI_"+ nomeArquivo +".jpg";
	    mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);  
	    return mediaFile;
	}
}

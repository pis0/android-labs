package MultiMobile.Threads;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.kroz.activerecord.ActiveRecordBase;

import MultiMobile.DAO.ClienteMultisoftware;
import MultiMobile.DAO.Usuario;
import MultiMobile.Utils.Funcoes;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import br.inf.commerce.multimobilegpa.android.OcorrenciaGlogActivity;
import br.inf.commerce.multimobilegpa.android.PrincipalActivity;
import br.inf.commerce.multimobilegpa.android.R;
import curso.utils.Networking;

public class SincronizarChegadaSaida extends AsyncTask<String, Integer, String> {

	AlertDialog alertDialog;
	private Context context;
	private ActiveRecordBase db;
	String usuarioLogado;
	boolean saida = false;
	
	public SincronizarChegadaSaida(ActiveRecordBase db, Context context, String usuarioLogado) {		
		this.context = context;
		this.db = db;
		this.usuarioLogado = usuarioLogado;
	}
	
	@Override
	protected void onPreExecute() {
		alertDialog = PrincipalActivity.showDialogSincronismoPadrao("Dados de horário", "Enviando a informação, favor aguardar");
		
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(String result) {
		if (alertDialog != null)
			alertDialog.dismiss();
		
		if (!result.equalsIgnoreCase("Sucesso"))
	    	PrincipalActivity.showAlertDialogOneButtonPadrao("Dados de hor�rio", result, context);
		else if (saida)
			OcorrenciaGlogActivity.fecharActivity();

		super.onPostExecute(result);
	}
	
	@Override
	protected String doInBackground(String... params) {
		saida = Boolean.valueOf(params[3]);

		if (Funcoes.verificaConexao(context)) {
			String urlWS = context.getString(R.string.url_ws);
			String urlEnvio = urlWS + "FilaCarregamento.svc/";
			String token = context.getString(R.string.chave_ws);
			Usuario usuarioSelecionado = Funcoes.retornaUsuario(db, usuarioLogado);
			ClienteMultisoftware clienteMultisoftware = Funcoes.retornaClienteMultisoftware(db);
			
			Map<String,String> paramDados = new HashMap<String,String>();
			String resposta = null;
			
			String urlEnviarUsuario = urlEnvio + "InformarDataChegada";
			
			paramDados.put("usuario", String.valueOf(usuarioSelecionado.getId_commerce()));
			paramDados.put("empresaMultisoftware", String.valueOf(clienteMultisoftware.getId_commerce()));
			paramDados.put("token", token);
			paramDados.put("dataChegada", Funcoes.somenteNumeros(Funcoes.PegarDataHoraAtual()));
			paramDados.put("cnpjCliente", params[0]);
			paramDados.put("latitude", params[1]);
			paramDados.put("longitude", params[2]);
			
			if (saida){
				urlEnviarUsuario = urlEnvio + "InformarDataSaida";
				paramDados.put("dataSaida", Funcoes.somenteNumeros(params[4]));
				paramDados.put("senhaCliente", params[5]);
			}
			
			try {
				resposta = Networking.performPostCall(urlEnviarUsuario, paramDados);
			} catch (IOException e1) {
				e1.printStackTrace();
				return "Problemas ao conectar com o web service - N�o habilitado";
			}
			
			if (Funcoes.isNullOrEmpty(resposta))
				return "Problemas ao informar data.";
			
			JSONObject object;
			try {
				object = new JSONObject(resposta);
				if (saida)
					object = new JSONObject(object.getString("InformarDataSaidaResult"));
				else
					object = new JSONObject(object.getString("InformarDataChegadaResult"));
				
				boolean status = object.getBoolean("Status");
				String mensagemRetorno = object.getString("Mensagem");
				if (!status)
					return mensagemRetorno;
				
			} catch (Exception e) {
				return "Retorno diferente do esperado ao converter resposta do web service.";
			} catch (Throwable t) {
				return "Problemas ao converter resposta do web service.";
			}
		} else
			return "N�o h� conex�o com a internet! Favor ativar a mesma para continuar.";

		return "Sucesso";
	}
}
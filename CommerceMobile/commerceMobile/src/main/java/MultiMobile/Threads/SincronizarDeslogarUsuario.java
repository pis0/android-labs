package MultiMobile.Threads;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.kroz.activerecord.ActiveRecordBase;

import br.inf.commerce.multimobilegpa.android.PrincipalActivity;
import br.inf.commerce.multimobilegpa.android.R;

import curso.utils.Networking;

import MultiMobile.DAO.ClienteMultisoftware;
import MultiMobile.DAO.Usuario;
import MultiMobile.Utils.Funcoes;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

public class SincronizarDeslogarUsuario extends AsyncTask<String, Integer, String> {

	AlertDialog alertDialog;
	private Context context;
	private ActiveRecordBase db;
	String usuarioLogado;
	
	public SincronizarDeslogarUsuario(ActiveRecordBase db, Context context, String usuarioLogado) {		
		this.context = context;
		this.db = db;
		this.usuarioLogado = usuarioLogado;
	}
	
	@Override
	protected void onPreExecute() {
		alertDialog = PrincipalActivity.showDialogSincronismoPadrao("Deslogar Usuário", "Enviando pedido para deslogar, favor aguardar");
		
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(String result) {
		if (alertDialog != null)
			alertDialog.dismiss();
		
		if (!result.equalsIgnoreCase("Sucesso"))
	    	PrincipalActivity.showAlertDialogOneButtonPadrao("Retorno Deslogar Usuário", result, context);
		else{
			PrincipalActivity.sair = true;
			PrincipalActivity.limparDados = true;
			((PrincipalActivity) PrincipalActivity.contextPrincipalActivity).finish();
		}

		super.onPostExecute(result);
	}
	
	@Override
	protected String doInBackground(String... params) {

		if (Funcoes.verificaConexao(context)) {
			String urlWS = context.getString(R.string.url_ws);
			String urlEnvio = urlWS + "Autenticacao.svc/";
			String token = context.getString(R.string.chave_ws);
			Usuario usuarioSelecionado = Funcoes.retornaUsuario(db, usuarioLogado);
			ClienteMultisoftware clienteMultisoftware = Funcoes.retornaClienteMultisoftware(db);
			if (clienteMultisoftware == null)
				return "Nenhum Embarcador configurado para o usu�rio utilizar o aplicativo";
			
			Map<String,String> paramDadosUsuario = new HashMap<String,String>();
			String resposta = null;
			
			String urlEnviarUsuario = urlEnvio + "DeslogarUsuario";
			
			paramDadosUsuario.put("usuario", String.valueOf(usuarioSelecionado.getId_commerce()));
			paramDadosUsuario.put("empresaMultisoftware", String.valueOf(clienteMultisoftware.getId_commerce()));
			paramDadosUsuario.put("token", token);
			
			try {
				resposta = Networking.performPostCall(urlEnviarUsuario, paramDadosUsuario);
			} catch (IOException e1) {
				e1.printStackTrace();
				return "Problemas ao conectar com o web service. 1 "+e1.getMessage();
			}
			
			if (Funcoes.isNullOrEmpty(resposta))
				return "Problemas ao deslogar o usu�rio.";
			
			JSONObject jsonDadosFluxo;
			try {
				jsonDadosFluxo = new JSONObject(resposta);
				jsonDadosFluxo = new JSONObject(jsonDadosFluxo.getString("DeslogarUsuarioResult"));
				
				boolean status = jsonDadosFluxo.getBoolean("Status");
				String mensagemRetorno = jsonDadosFluxo.getString("Mensagem");
				if (!status)
					return mensagemRetorno;
				
			} catch (Exception e) {
				return "Retorno diferente do esperado ao converter resposta do web service.";
			} catch (Throwable t) {
				return "Problemas ao converter resposta do web service.";
			}
		}

		return "Sucesso";
	}
}
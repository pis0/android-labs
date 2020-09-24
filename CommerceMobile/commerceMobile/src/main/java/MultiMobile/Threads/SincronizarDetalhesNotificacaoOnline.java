package MultiMobile.Threads;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.kroz.activerecord.ActiveRecordBase;

import curso.utils.Networking;

import br.inf.commerce.multimobilegpa.android.PrincipalActivity;
import br.inf.commerce.multimobilegpa.android.R;
import MultiMobile.DAO.ClienteMultisoftware;
import MultiMobile.DAO.Usuario;
import MultiMobile.Utils.Funcoes;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class SincronizarDetalhesNotificacaoOnline extends
		AsyncTask<String, Integer, String> {
	
	ProgressDialog mProgressCarregarLista;
	private ActiveRecordBase db;
	private Context context;
	String usuarioLogado;
	String tituloNotificacao = "";
	String detalhesNotificacao = "";

	public SincronizarDetalhesNotificacaoOnline(ActiveRecordBase db, Context context, String usuarioLogado) {		
		this.db = db;
		this.context = context;
		this.usuarioLogado = usuarioLogado;
	}
	
	@Override
	protected void onPreExecute() {
		mProgressCarregarLista = new ProgressDialog(context);
    	mProgressCarregarLista.setTitle("Carregando...");
    	mProgressCarregarLista.setMessage("Por favor aguarde até o fim...");
    	mProgressCarregarLista.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    	mProgressCarregarLista.setCancelable(false);
    	mProgressCarregarLista.show();
		
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(String result) {
		mProgressCarregarLista.dismiss();
		
		if (result.equalsIgnoreCase("ObteuResposta"))
			PrincipalActivity.showAlertDialogOneButtonPadrao(tituloNotificacao, detalhesNotificacao, context);
		else
			Toast.makeText(context, result, Toast.LENGTH_LONG).show();

		super.onPostExecute(result);
	}

	@Override
	protected String doInBackground(String... params) {
		String codigoNotificacao = params[0];
		
		if (Funcoes.verificaConexao(context)) {
			String urlEnvio = context.getString(R.string.url_ws)+"FilaCarregamento.svc/ObterNotificacao";
			String token = context.getString(R.string.chave_ws);
			Usuario usuarioSelecionado = Funcoes.retornaUsuario(db, usuarioLogado);
			ClienteMultisoftware clienteMultisoftware = Funcoes.retornaClienteMultisoftware(db);
			
			Map<String,String> paramFila = new HashMap<String,String>();
			String resposta = null;
			
			paramFila.put("usuario", String.valueOf(usuarioSelecionado.getId_commerce()));
			paramFila.put("empresaMultisoftware", String.valueOf(clienteMultisoftware.getId_commerce()));
			paramFila.put("token", token);
			paramFila.put("codigoNotificacao", codigoNotificacao);
			
			try {
				resposta = Networking.performPostCall(urlEnvio, paramFila);
			} catch (IOException e1) {
				e1.printStackTrace();
				return "Problemas ao conectar com o web service. 1 "+e1.getMessage();
			}
			
			if (resposta == null)
				return "Problemas ao conectar com o web service. 2 resposta null";
			
			if (resposta.isEmpty() || resposta == "")
				return "Problemas ao obter detalhes da notificação online.";
			
			JSONObject objectFila;
			try {
				objectFila = new JSONObject(resposta);
				objectFila = new JSONObject(objectFila.getString("ObterNotificacaoResult"));
				String status = objectFila.getString("Status");
				if (status == "false")
					return objectFila.getString("Mensagem");
				else {
					objectFila = new JSONObject(objectFila.getString("Objeto"));
					
					tituloNotificacao = objectFila.getString("Assunto");
					detalhesNotificacao = "Data: " + objectFila.getString("Data")+
							"\n\n" + objectFila.getString("Mensagem");
					return "ObteuResposta";
				}
			} catch (Throwable t) {
				return "Problemas ao converter resposta do web service.";
			}
		} else
			return "Não há conexão com a internet! Favor ativar a mesma para continuar.";
	}

}
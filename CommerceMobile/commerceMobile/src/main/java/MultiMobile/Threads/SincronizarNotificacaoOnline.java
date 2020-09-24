package MultiMobile.Threads;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.kroz.activerecord.ActiveRecordBase;

import curso.utils.Networking;

import br.inf.commerce.multimobilegpa.android.PrincipalActivity;
import br.inf.commerce.multimobilegpa.android.R;
import br.inf.commerce.multimobilegpa.android.VisualizarNotificacaoOnlineActivity;

import MultiMobile.DAO.ClienteMultisoftware;
import MultiMobile.DAO.Usuario;
import MultiMobile.DTO.NotificacaoOnlineDTO;
import MultiMobile.Utils.Funcoes;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

public class SincronizarNotificacaoOnline extends
		AsyncTask<String, Integer, String> {
	
	private Context context;
	private Handler mHandler;
	private ActiveRecordBase db;
	String usuarioLogado;
	private static ProgressDialog mProgressOnline;

	public SincronizarNotificacaoOnline(ActiveRecordBase db, Context context, String usuarioLogado) {		
		this.context = context;
		this.db = db;
		this.usuarioLogado = usuarioLogado;
		this.mHandler = new Handler();
	}
	
	@Override
	protected void onPreExecute() {
		mProgressOnline = new ProgressDialog(context);
    	mProgressOnline.setTitle("Carregando Notificações Online");
    	mProgressOnline.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    	mProgressOnline.setCancelable(false);
    	mProgressOnline.show();
		
		alterarProgresso();
		super.onPreExecute();
	}
	
	protected void onProgressUpdate(Integer... values) {
		mProgressOnline.setProgress(values[0]);
	}

	@Override
	protected void onPostExecute(String result) {
		mProgressOnline.dismiss();
		
		if (!result.equalsIgnoreCase("Sucesso"))
			PrincipalActivity.showAlertDialogOneButtonPadrao("Notificação Online", result, context);
		else
			VisualizarNotificacaoOnlineActivity.atualizarListaCargasFila();
		
		super.onPostExecute(result);
	}

	@Override
	protected String doInBackground(String... params) {
		String naoLida = params[0];
		
		if (Funcoes.verificaConexao(context)) {
			String urlEnvio = context.getString(R.string.url_ws)+"FilaCarregamento.svc/";
			String token = context.getString(R.string.chave_ws);
			Usuario usuarioSelecionado = Funcoes.retornaUsuario(db, usuarioLogado);
			ClienteMultisoftware clienteMultisoftware = Funcoes.retornaClienteMultisoftware(db);
			
			Map<String,String> paramNotificacao = new HashMap<String,String>();
			String resposta = null;
			String urlEnviarFila = urlEnvio + "ObterNotificacoes";
			
			paramNotificacao.put("usuario", String.valueOf(usuarioSelecionado.getId_commerce()));
			paramNotificacao.put("empresaMultisoftware", String.valueOf(clienteMultisoftware.getId_commerce()));
			paramNotificacao.put("token", token);
			paramNotificacao.put("somenteNaoLidas", naoLida);
			
			try {
				resposta = Networking.performPostCall(urlEnviarFila, paramNotificacao);
			} catch (IOException e1) {
				e1.printStackTrace();
				return "Problemas ao conectar com o web service. 1 "+e1.getMessage();
			}
			
			if (Funcoes.isNullOrEmpty(resposta))
				return "Problemas ao obter lista das notificações.";
			
			JSONObject objectNotificacoes;
			try {
				objectNotificacoes = new JSONObject(resposta);
				objectNotificacoes = new JSONObject(objectNotificacoes.getString("ObterNotificacoesResult"));
				boolean status = objectNotificacoes.getBoolean("Status");
				if (!status)
					return objectNotificacoes.getString("Mensagem");
				else{
					JSONArray array = (JSONArray) new JSONTokener(objectNotificacoes.getString("Objeto")).nextValue();
					
					mProgressOnline.setMax(array.length());
					alterarProgresso();					
					for (int i = 0; i < array.length(); i++){
						publishProgress(i);
						objectNotificacoes = array.getJSONObject(i);							
						
						NotificacaoOnlineDTO notificacaoOnlineDTO = new NotificacaoOnlineDTO(
								objectNotificacoes.getInt("Codigo"),
								objectNotificacoes.getString("Assunto"),
								objectNotificacoes.getString("Data"));

						VisualizarNotificacaoOnlineActivity.listRelatorioNotificacaoOnline.add(notificacaoOnlineDTO);
					}
					return "Sucesso";
				}
			} catch (JSONException e) {
				e.printStackTrace();
				return "Problemas na leitura das notificações.";
			} catch (Throwable t) {
				return "Problemas ao converter resposta do web service.";
			}
		} else
			return "Não há conexão com a internet! Favor ativar a mesma para continuar.";
	}
	
	public void alterarProgresso() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							mProgressOnline.setMessage("Carregando notificações...");
						}
					});
				} catch (Exception e) {
				}
			}
		}).start();
	}
}

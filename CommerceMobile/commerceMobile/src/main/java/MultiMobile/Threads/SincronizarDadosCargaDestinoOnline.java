package MultiMobile.Threads;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.kroz.activerecord.ActiveRecordBase;

import br.inf.commerce.multimobilegpa.android.PrincipalActivity;
import br.inf.commerce.multimobilegpa.android.R;
import curso.utils.Networking;

import MultiMobile.DAO.ClienteMultisoftware;
import MultiMobile.DAO.Usuario;
import MultiMobile.DTO.DestinoCargaOnlineDTO;
import MultiMobile.Utils.Funcoes;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

public class SincronizarDadosCargaDestinoOnline extends
	AsyncTask<String, Integer, String> {
	
	private Context context;
	private Handler mHandler;
	private ActiveRecordBase db;
	String usuarioLogado;
	private static ProgressDialog mProgressOnline;

	public SincronizarDadosCargaDestinoOnline(ActiveRecordBase db, Context context, String usuarioLogado) {		
		this.context = context;
		this.db = db;
		this.usuarioLogado = usuarioLogado;
		this.mHandler = new Handler();
	}
	
	@Override
	protected void onPreExecute() {
		mProgressOnline = new ProgressDialog(context);
    	mProgressOnline.setTitle("Carregando Destinos Carga Online");
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
			PrincipalActivity.showAlertDialogOneButtonPadrao("Destinos Carga Online", result, context);
		else
			PrincipalActivity.openInformarSincronizarChegadaDialog();
		
		super.onPostExecute(result);
	}

	@Override
	protected String doInBackground(String... params) {
		
		if (Funcoes.verificaConexao(context)) {
			String urlEnvio = context.getString(R.string.url_ws)+"FilaCarregamento.svc/";
			String token = context.getString(R.string.chave_ws);
			Usuario usuarioSelecionado = Funcoes.retornaUsuario(db, usuarioLogado);
			ClienteMultisoftware clienteMultisoftware = Funcoes.retornaClienteMultisoftware(db);
			
			Map<String,String> paramDadosCarga = new HashMap<String,String>();
			String resposta = null;
			String urlEnviarFila = urlEnvio + "ObterDadosCarga";
			
			paramDadosCarga.put("usuario", String.valueOf(usuarioSelecionado.getId_commerce()));
			paramDadosCarga.put("empresaMultisoftware", String.valueOf(clienteMultisoftware.getId_commerce()));
			paramDadosCarga.put("token", token);
			
			try {
				resposta = Networking.performPostCall(urlEnviarFila, paramDadosCarga);
			} catch (IOException e1) {
				e1.printStackTrace();
				return "Problemas ao conectar com o web service. 1 "+e1.getMessage();
			}
			
			if (Funcoes.isNullOrEmpty(resposta))
				return "Problemas ao obter os dados da carga.";
			
			JSONObject object;
			try {
				object = new JSONObject(resposta);
				object = new JSONObject(object.getString("ObterDadosCargaResult"));
				boolean status = object.getBoolean("Status");
				if (!status)
					return object.getString("Mensagem");
				else{
					object = new JSONObject(object.getString("Objeto"));
					JSONArray array = (JSONArray) new JSONTokener(object.getString("Pedidos")).nextValue();
					
					mProgressOnline.setMax(array.length());
					alterarProgresso();					
					for (int i = 0; i < array.length(); i++){
						publishProgress(i);
						object = array.getJSONObject(i);
						
						DestinoCargaOnlineDTO dtoMontado = new DestinoCargaOnlineDTO(
								object.getString("CNPJCliente"),
								object.getString("NomeCliente"));

						PrincipalActivity.listDestinosCargaOnline.add(dtoMontado);						
					}
					
					return "Sucesso";					
				}
			} catch (JSONException e) {
				e.printStackTrace();
				return "Problemas na leitura dos dados da carga."; 
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
							mProgressOnline.setMessage("Carregando dados carga...");
						}
					});
				} catch (Exception e) {
				}
			}
		}).start();
	}
}
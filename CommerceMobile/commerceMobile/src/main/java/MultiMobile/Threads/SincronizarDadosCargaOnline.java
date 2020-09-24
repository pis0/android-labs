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
import MultiMobile.DAO.ClienteMultisoftware;
import MultiMobile.DAO.Usuario;
import MultiMobile.DTO.DadosCargaOnlineDTO;
import MultiMobile.Utils.Funcoes;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

public class SincronizarDadosCargaOnline extends
AsyncTask<String, Integer, String> {
	
	private Context context;
	private Handler mHandler;
	private ActiveRecordBase db;
	String usuarioLogado;
	private static ProgressDialog mProgressOnline;

	public SincronizarDadosCargaOnline(ActiveRecordBase db, Context context, String usuarioLogado) {		
		this.context = context;
		this.db = db;
		this.usuarioLogado = usuarioLogado;
		this.mHandler = new Handler();
	}
	
	@Override
	protected void onPreExecute() {
		mProgressOnline = new ProgressDialog(context);
    	mProgressOnline.setTitle("Carregando Dados Carga Online");
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
			PrincipalActivity.showAlertDialogOneButtonPadrao("Dados Carga Online", result, context);
		else
			PrincipalActivity.openVisualizarDadosCargaOnlineActivity();
		
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
			
			JSONObject objectDadosCarga;
			try {
				objectDadosCarga = new JSONObject(resposta);
				objectDadosCarga = new JSONObject(objectDadosCarga.getString("ObterDadosCargaResult"));
				String status = objectDadosCarga.getString("Status");
				if (status == "false")
					return objectDadosCarga.getString("Mensagem");
				else{
					objectDadosCarga = new JSONObject(objectDadosCarga.getString("Objeto"));
					int codigoCarga = objectDadosCarga.getInt("CodigoIntegracao");
					String numeroCarga = objectDadosCarga.getString("NumeroCargaEmbarcador");
					String cdCarga = objectDadosCarga.getString("Origem");
					String tipoCarga = objectDadosCarga.getString("TipoCarga");
					String pesoCarga = objectDadosCarga.getString("Peso");
					String saidaCarga = objectDadosCarga.getString("DataSaida");
					int situacaoCarga = objectDadosCarga.getInt("SituacaoCarga");
					
					if (situacaoCarga == 0) //Se logistica, quer dizer que esta Aguardando Enconta, assim não deixa visualizar os dados da carga
						return "Favor informar a Doca para visualizar os dados da carga";	
					
					JSONArray array = (JSONArray) new JSONTokener(objectDadosCarga.getString("Pedidos")).nextValue();
					
					mProgressOnline.setMax(array.length());
					alterarProgresso();
					for (int i = 0; i < array.length(); i++){
						publishProgress(i);
						try {
							objectDadosCarga = array.getJSONObject(i);
							
							DadosCargaOnlineDTO dadosCargaOnlineDTO = new DadosCargaOnlineDTO(
									codigoCarga,									
									numeroCarga,
									cdCarga,
									tipoCarga,
									pesoCarga,
									saidaCarga,
									objectDadosCarga.getString("NumeroPedidoEmbarcador"),
									objectDadosCarga.getString("Destino"),
									objectDadosCarga.getString("EnderecoDestino"),
									objectDadosCarga.getString("Peso"));

							PrincipalActivity.listRelatorioDadosCargaOnline.add(dadosCargaOnlineDTO);
						} catch (JSONException e) {
							e.printStackTrace();
							return "Problemas na leitura dos dados da carga."; 
						}
					}
					return "Sucesso";					
				}
			} catch (Throwable t) {
				return "Problemas ao converter resposta do web service.";
			}			
		} else
			return "N�o h� conex�o com a internet! Favor ativar a mesma para continuar.";
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

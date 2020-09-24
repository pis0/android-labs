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
import br.inf.commerce.multimobilegpa.android.VisualizarOcorrenciaGlogOnlineActivity;
import MultiMobile.DAO.ClienteMultisoftware;
import MultiMobile.DAO.Usuario;
import MultiMobile.DTO.AtendimentoOcorrenciasOnlineDTO;
import MultiMobile.DTO.DestinoCargaOnlineDTO;
import MultiMobile.DTO.MotivosAtendimentoOnlineDTO;
import MultiMobile.DTO.AtendimentoOnlineDTO;
import MultiMobile.Enums.EnumTipoMotivoAtendimentoWEB;
import MultiMobile.Utils.Funcoes;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

public class SincronizarCarregarOcorrenciaGlogOnline extends
		AsyncTask<String, Integer, String> {

	private Context context;
	private Handler mHandler;
	private ActiveRecordBase db;
	String usuarioLogado;
	private static ProgressDialog mProgressOnline;

	public SincronizarCarregarOcorrenciaGlogOnline(ActiveRecordBase db, Context context, String usuarioLogado) {		
		this.context = context;
		this.db = db;
		this.usuarioLogado = usuarioLogado;
		this.mHandler = new Handler();
	}
	
	@Override
	protected void onPreExecute() {
		mProgressOnline = new ProgressDialog(context);
    	mProgressOnline.setTitle("Carregando Ocorrências GLog Online");
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
			PrincipalActivity.showAlertDialogOneButtonPadrao("Ocorr�ncias GLog Online", result, context);
		else
			VisualizarOcorrenciaGlogOnlineActivity.atualizarListaOcorrenciasFila();
		
		super.onPostExecute(result);
	}
	
	@Override
	protected String doInBackground(String... params) {
		
		if (Funcoes.verificaConexao(context)) {
			String urlEnvio = context.getString(R.string.url_ws)+"FilaCarregamento.svc/";
			String token = context.getString(R.string.chave_ws);
			Usuario usuarioSelecionado = Funcoes.retornaUsuario(db, usuarioLogado);
			ClienteMultisoftware clienteMultisoftware = Funcoes.retornaClienteMultisoftware(db);
			
			String resposta = null;
			String url = "";
			
			//Carrega atendimentos
			url = urlEnvio + "ObterAtendimentosCarga/" + token + "/" + usuarioSelecionado.getId_commerce() + "/" + String.valueOf(clienteMultisoftware.getId_commerce());
			
			try {
				resposta = Networking.getHttpRequet(url);
			} catch (IOException e1) {
				e1.printStackTrace();
				return "Problemas ao conectar com o web service - N�o habilitado";
			}
			
			if (Funcoes.isNullOrEmpty(resposta))
				return "Problemas ao obter lista das Ocorr�ncias GLog.";
			
			JSONObject object;
			try {
				object = new JSONObject(resposta);
				object = new JSONObject(object.getString("ObterAtendimentosCargaResult"));
				boolean status = object.getBoolean("Status");
				if (!status)
					return object.getString("Mensagem");
				else{
					JSONArray array = (JSONArray) new JSONTokener(object.getString("Objeto")).nextValue();
					
					mProgressOnline.setMax(array.length());
					alterarProgresso();
					for (int i = 0; i < array.length(); i++){
						publishProgress(i);
						object = array.getJSONObject(i);
						int codigoAtendimento = object.getInt("Codigo");
						
						AtendimentoOnlineDTO dtoMontado = new AtendimentoOnlineDTO(
								codigoAtendimento,
								object.getString("DescricaoMotivo"),
								object.getString("DataCriacao"),
								object.getString("DescricaoSituacao"),
								object.getInt("Numero"),
								object.getInt("Tipo"),
								object.getInt("TipoCliente"),
								object.getString("CNPJCliente"),
								object.getString("DescricaoCliente"),
								object.getInt("Situacao"),
								object.getInt("CodigoMotivo"),
								object.getString("DataRetencaoInicio"),
								object.getString("DataRetencaoFim"),
								object.getString("Analises"),
								"",
								object.getBoolean("RetencaoBau"),
								0.00,
								0.00,
								object.getString("DataReentrega"),
								object.getString("NumeroOcorrencia"),
								object.getString("DataEntradaRaio"),
								object.getString("DataSaidaRaio"),
								object.getString("PlacaReboque"));

						VisualizarOcorrenciaGlogOnlineActivity.listOcorrenciaGlogOnline.add(dtoMontado);
						
						JSONArray arrayOcorrencias = (JSONArray) new JSONTokener(object.getString("Ocorrencias")).nextValue();
						
						for (int j = 0; j < arrayOcorrencias.length(); j++){
							object = arrayOcorrencias.getJSONObject(j);
							
							AtendimentoOcorrenciasOnlineDTO dtoOcorrenciaMontado = new AtendimentoOcorrenciasOnlineDTO(
									codigoAtendimento,
									object.getString("DescricaoSituacaoOcorrencia"),
									object.getString("DestinoOcorrencia"),
									object.getString("NumeroOcorrencia"),
									object.getString("ObservacaoOcorrencia"),
									object.getString("OrigemOcorrencia"),
									object.getString("TipoOcorrencia"),
									object.getDouble("ValorOcorrencia"));

							VisualizarOcorrenciaGlogOnlineActivity.listOcorrenciasAtendimentoOnline.add(dtoOcorrenciaMontado);						
						}
					}
				}
			}  catch (JSONException e) {
				e.printStackTrace();
				return "Problemas na leitura das ocorr�ncias."; 
			} catch (Throwable t) {
				return "Problemas ao converter resposta do web service.";
			}
			
			//Carrega motivos atendimentos
			url = urlEnvio + "ObterMotivosAtendimentos/" + token + "/" + usuarioSelecionado.getId_commerce() + "/" + String.valueOf(clienteMultisoftware.getId_commerce());
			
			try {
				resposta = Networking.getHttpRequet(url);
			} catch (IOException e1) {
				e1.printStackTrace();
				return "Problemas ao conectar com o web service - N�o habilitado";
			}
			
			if (Funcoes.isNullOrEmpty(resposta))
				return "Problemas ao obter lista dos motivos de atendimento.";
			
			try {
				object = new JSONObject(resposta);
				object = new JSONObject(object.getString("ObterMotivosAtendimentosResult"));
				boolean status = object.getBoolean("Status");
				if (!status)
					return object.getString("Mensagem");
				else{
					JSONArray array = (JSONArray) new JSONTokener(object.getString("Objeto")).nextValue();
					
					mProgressOnline.setMax(array.length());
					alterarProgresso();
					for (int i = 0; i < array.length(); i++){
						publishProgress(i);
						object = array.getJSONObject(i);
						
						if (object.getInt("Tipo") == EnumTipoMotivoAtendimentoWEB.Reentrega.getValue() || 
								object.getInt("Tipo") == EnumTipoMotivoAtendimentoWEB.Retencao.getValue()){
							MotivosAtendimentoOnlineDTO dtoMontado = new MotivosAtendimentoOnlineDTO(
									object.getInt("Codigo"),
									object.getString("Descricao"),
									object.getBoolean("ExigirFoto"),
									object.getBoolean("ExigirQrCode"),
									object.getInt("Tipo"));
	
							VisualizarOcorrenciaGlogOnlineActivity.listMotivosAtendimentoOnline.add(dtoMontado);
						}
					}
				}
			}  catch (JSONException e) {
				e.printStackTrace();
				return "Problemas na leitura dos motivos."; 
			} catch (Throwable t) {
				return "Problemas ao converter resposta do web service.";
			}
			
			//Carrega destinos cargas
			url = urlEnvio + "ObterDadosCarga";
			
			Map<String,String> paramDadosCarga = new HashMap<String,String>();
			paramDadosCarga.put("usuario", String.valueOf(usuarioSelecionado.getId_commerce()));
			paramDadosCarga.put("empresaMultisoftware", String.valueOf(clienteMultisoftware.getId_commerce()));
			paramDadosCarga.put("token", token);
			
			try {
				resposta = Networking.performPostCall(url, paramDadosCarga);
			} catch (IOException e1) {
				e1.printStackTrace();
				return "Problemas ao conectar com o web service - N�o habilitado";
			}
			
			if (Funcoes.isNullOrEmpty(resposta))
				return "Problemas ao obter os dados da carga.";
			
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

						VisualizarOcorrenciaGlogOnlineActivity.listDestinosCargaOnline.add(dtoMontado);						
					}
					return "Sucesso";
				}
			}  catch (JSONException e) {
				e.printStackTrace();
				return "Problemas na leitura das ocorr�ncias."; 
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
							mProgressOnline.setMessage("Carregando ocorr�ncias...");
						}
					});
				} catch (Exception e) {
				}
			}
		}).start();
	}
}

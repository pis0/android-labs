package MultiMobile.Threads;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.kroz.activerecord.ActiveRecordBase;

import br.inf.commerce.multimobilegpa.android.OcorrenciaGlogActivity;
import br.inf.commerce.multimobilegpa.android.R;

import curso.utils.Networking;

import MultiMobile.DAO.ClienteMultisoftware;
import MultiMobile.DAO.Usuario;
import MultiMobile.Utils.Dialogs;
import MultiMobile.Utils.Funcoes;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

public class SincronizarEnviarOcorrenciaGlogOnline extends AsyncTask<String, Integer, String> {

	AlertDialog alertDialog;
	private Context context;
	private ActiveRecordBase db;
	String usuarioLogado;
	String latitudeLocalizacao, longitudeLocalizacao, tipoMotivo, cnpjCliente, retencaoBau, placaReboque;
	
	public SincronizarEnviarOcorrenciaGlogOnline(ActiveRecordBase db, Context context, String usuarioLogado) {		
		this.context = context;
		this.db = db;
		this.usuarioLogado = usuarioLogado;
	}
	
	@Override
	protected void onPreExecute() {
		alertDialog = Dialogs.showDialogSincronismoPadrao(context, "Ocorrência GLog", "Enviando nova solicitação, favor aguardar");
		
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(String result) {
		if (alertDialog != null)
			alertDialog.dismiss();
		
		if (!result.equalsIgnoreCase("Sucesso"))
			Dialogs.showAlertDialogOneButtonPadrao(context, "Retorno da Ocorrência GLog", result);
		else
			OcorrenciaGlogActivity.fecharActivity();

		super.onPostExecute(result);
	}
	
	@Override
	protected String doInBackground(String... params) {
		latitudeLocalizacao = params[0];
		longitudeLocalizacao = params[1];
		tipoMotivo = params[2];
		cnpjCliente = params[3];
		retencaoBau = params[4];
		placaReboque = params[5];
		
		if (Funcoes.verificaConexao(context)) {
			String urlEnvio = context.getString(R.string.url_ws)+"FilaCarregamento.svc/";
			String token = context.getString(R.string.chave_ws);
			Usuario usuarioSelecionado = Funcoes.retornaUsuario(db, usuarioLogado);
			ClienteMultisoftware clienteMultisoftware = Funcoes.retornaClienteMultisoftware(db);
			
			Map<String,String> paramEnvio = new HashMap<String,String>();
			String resposta = null;
			//int codigoAtendimento = 0;
			String url = urlEnvio + "SolicitarAtendimento";
			
			paramEnvio.put("usuario", String.valueOf(usuarioSelecionado.getId_commerce()));
			paramEnvio.put("empresaMultisoftware", String.valueOf(clienteMultisoftware.getId_commerce()));
			paramEnvio.put("token", token);
			paramEnvio.put("latitude", latitudeLocalizacao);
			paramEnvio.put("longitude", longitudeLocalizacao);			
			paramEnvio.put("tipoMotivo", tipoMotivo);
			paramEnvio.put("cnpjCliente", cnpjCliente);
			paramEnvio.put("retencaoBau", retencaoBau);
			paramEnvio.put("placaReboque", placaReboque);
			
			try {
				resposta = Networking.performPostCall(url, paramEnvio);
			} catch (IOException e1) {
				e1.printStackTrace();
				return "Problemas ao conectar com o web service - Não habilitado";
			}
			
			if (Funcoes.isNullOrEmpty(resposta))
				return "Problemas ao solicitar atendimento";
			
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(resposta);
				jsonObject = new JSONObject(jsonObject.getString("SolicitarAtendimentoResult"));
				
				boolean status = jsonObject.getBoolean("Status");
				String mensagemRetorno = jsonObject.getString("Mensagem");
				if (!status)
					return mensagemRetorno;
				//else
				//	codigoAtendimento = jsonObject.getInt("Objeto");
			} catch (Exception e) {
				return "Retorno diferente do esperado ao converter resposta do web service.";
			} catch (Throwable t) {
				return "Problemas ao converter resposta do web service.";
			}
			
			/*if (leuImagem){ //Envia a imagem se foi realizada a leitura
				Map<String,String> paramImagem = new HashMap<String,String>();
				Map<String,byte[]> paramByteImagem = new HashMap<String,byte[]>();
				
				url = urlEnvio + "EnviarByteImagemAtendimento";
	
				byte[] byteArrayImagem = null;
				File pictureFile = Funcoes.getOutputMediaFile("OcorrenciaGLog", context);
			    if (pictureFile != null) {
			    	Bitmap bMap = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());
					if (bMap != null){
						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						bMap = Bitmap.createScaledBitmap(bMap, (int)(bMap.getWidth()*0.5), (int)(bMap.getHeight()*0.5), true);
						bMap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
						byteArrayImagem = stream.toByteArray();
						
						bMap.recycle();
						bMap = null;
					}
			    }
				
			    paramByteImagem.put("imagem", byteArrayImagem);
			    
			    try {
			    	resposta = Networking.enviarImagem(url, paramByteImagem);					
				} catch (IOException e1) { 
					e1.printStackTrace();
					return "Problemas ao conectar com o web service - N�o habilitado";
				}
				
				if (Funcoes.isNullOrEmpty(resposta))
					return "Problemas ao enviar imagem da ocorr�ncia glog.";
				
				try {
					jsonObject = new JSONObject(resposta);
					jsonObject = new JSONObject(jsonObject.getString("EnviarByteImagemAtendimentoResult"));
					
					boolean status = jsonObject.getBoolean("Status");
					String mensagemRetorno = jsonObject.getString("Mensagem");
					if (!status)
						return mensagemRetorno;
					else{ //Envia os parametros da imagem
						
						url = urlEnvio + "EnviarImagemAtendimento";
						paramImagem.put("usuario", String.valueOf(usuarioSelecionado.getId_commerce()));
						paramImagem.put("empresaMultisoftware", String.valueOf(clienteMultisoftware.getId_commerce()));
						paramImagem.put("tokenImagem", jsonObject.getString("Objeto"));
						paramImagem.put("token", token);
						paramImagem.put("codigoAtendimento", String.valueOf(codigoAtendimento));
						
						try {
							resposta = Networking.performPostCall(url, paramImagem);
						} catch (IOException e1) {
							e1.printStackTrace();
							return "Problemas ao conectar com o web service - N�o habilitado";
						}
						
						if (Funcoes.isNullOrEmpty(resposta))
							return "Problemas ao enviar imagem da ocorr�ncia glog 2";
						
						jsonObject = new JSONObject(resposta);
						jsonObject = new JSONObject(jsonObject.getString("EnviarImagemAtendimentoResult"));				
						status = jsonObject.getBoolean("Status");
						mensagemRetorno = jsonObject.getString("Mensagem");
						if (!status)
							return mensagemRetorno;
					}
				} catch (Exception e) {
					return "Retorno diferente do esperado ao converter resposta do web service.";
				} catch (Throwable t) {
					return "Problemas ao converter resposta do web service.";
				}
			}*/
			
		} else
			return "Não há conexão com a internet! Favor ativar a mesma e repetir o procedimento.";

		return "Sucesso";
	}
}

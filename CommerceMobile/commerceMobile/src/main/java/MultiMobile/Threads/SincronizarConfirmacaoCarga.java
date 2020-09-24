package MultiMobile.Threads;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.kroz.activerecord.ActiveRecordBase;
import org.kroz.activerecord.ActiveRecordException;
import org.kroz.activerecord.EntitiesHelper;

import curso.utils.Networking;

import br.inf.commerce.multimobilegpa.android.PrincipalActivity;
import br.inf.commerce.multimobilegpa.android.R;

import MultiMobile.DAO.CargaFila;
import MultiMobile.DAO.ClienteMultisoftware;
import MultiMobile.DAO.Usuario;
import MultiMobile.Utils.EnumStatusFila;
import MultiMobile.Utils.Funcoes;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

public class SincronizarConfirmacaoCarga extends
		AsyncTask<String, Integer, String> {
	
	AlertDialog alertDialog;
	private ActiveRecordBase db;
	private Context context;
	String usuarioLogado;
	String detalhesCarga = "";
	boolean atualizarAposRetorno = false;

	public SincronizarConfirmacaoCarga(ActiveRecordBase db, Context context, String usuarioLogado) {		
		this.db = db;
		this.context = context;
		this.usuarioLogado = usuarioLogado;
	}
	
	@Override
	protected void onPreExecute() {
		alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setTitle("Confirmação da Carga");
		alertDialog.setMessage("Enviando detalhe da confirmação, favor aguardar");
		alertDialog.setCancelable(false);
		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
		new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
		    	if (atualizarAposRetorno)
	            	PrincipalActivity.createSincronizarObterDadosFilaDialog();
		    }
		});
		
		alertDialog.show();
		final AlertDialog dialogEntrada = (AlertDialog)alertDialog;
		dialogEntrada.getButton(DialogInterface.BUTTON_NEUTRAL).setEnabled(false);
		
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(String result) {
		alertDialog.dismiss();
		
		alertDialog.setTitle("Retorno Confirma��o Carga!");
		alertDialog.setMessage(result);
		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
	    new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	            dialog.dismiss();
	        }
	    });
		
		alertDialog.show();
		final AlertDialog dialog = (AlertDialog)alertDialog;
    	dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setEnabled(true);
		
		PrincipalActivity.atualizarDadosFila();

		super.onPostExecute(result);
	}

	@Override
	protected String doInBackground(String... params) {
		String opcaoEscolhaConfirmacao = params[0];
		
		if (Funcoes.verificaConexao(context)) {
			String urlEnvio = context.getString(R.string.url_ws)+"FilaCarregamento.svc/";
			String token = context.getString(R.string.chave_ws);
			Usuario usuarioSelecionado = Funcoes.retornaUsuario(db, usuarioLogado);
			ClienteMultisoftware clienteMultisoftware = Funcoes.retornaClienteMultisoftware(db);
			
			Map<String,String> paramFila = new HashMap<String,String>();		
			String resposta = null;
			
			if (opcaoEscolhaConfirmacao.equals("Sim")){
				String urlEnviarFila = urlEnvio + "AceitarCarga";
				
				paramFila.put("usuario", String.valueOf(usuarioSelecionado.getId_commerce()));
				paramFila.put("empresaMultisoftware", String.valueOf(clienteMultisoftware.getId_commerce()));
				paramFila.put("token", token);
				
				try {
					resposta = Networking.performPostCall(urlEnviarFila, paramFila);
				} catch (IOException e1) {
					e1.printStackTrace();
					return "Problemas ao conectar com o web service. 1 "+e1.getMessage();
				}
				
				if (resposta == null)
					return "Problemas ao conectar com o web service. 2 resposta null";
				
				if (resposta.isEmpty() || resposta == "")
					return "Problemas ao aceitar a Carga.";
				
				JSONObject objectFila;
				try {
					objectFila = new JSONObject(resposta);
					objectFila = new JSONObject(objectFila.getString("AceitarCargaResult"));
					String status = objectFila.getString("Status");
					String mensagemRetorno = objectFila.getString("Mensagem");
					if (status == "false"){
						atualizarAposRetorno = true;
						return mensagemRetorno;
					}else{
						try {
							String cd = usuarioSelecionado.local_fila;
							
							usuarioSelecionado.status_fila = EnumStatusFila.EmViagem.getValue();
		                	usuarioSelecionado.posicao_fila = 1;
		                	usuarioSelecionado.save();
		                	
		                	CargaFila cargaFila = new CargaFila(cd, Funcoes.stringToLong(Funcoes.PegarDataAtual()));
				    		try {
				    			CargaFila cargaFiladb;
				    			cargaFiladb = db.newEntity(CargaFila.class);
				    			EntitiesHelper.copyFieldsWithoutID(cargaFiladb, cargaFila);
				    			cargaFiladb.save();
				    		} catch (ActiveRecordException e) {
				    			e.printStackTrace();
				    			return "Problemas ao salvar hist�rico da Carga.";
				    		}
				    		
				    		objectFila = new JSONObject(objectFila.getString("Objeto"));
				    		if (!Funcoes.isNullOrEmpty(objectFila.getString("Doca")))
				    			usuarioSelecionado.local_fila = objectFila.getString("DescricaoCentroCarregamento") + " - Doca: " + objectFila.getString("Doca");
		                	
		                	return "Carga confirmada com sucesso!";
						} catch (ActiveRecordException e) {
							e.printStackTrace();
							return "Problemas ao alterar status da Fila.";
						}
					}
				} catch (Throwable t) {
					return "Problemas ao converter resposta do web service.";
				}
			} else {
				String urlEnviarFila = urlEnvio + "RecusarCarga";
				
				paramFila.put("usuario", String.valueOf(usuarioSelecionado.getId_commerce()));
				paramFila.put("empresaMultisoftware", String.valueOf(clienteMultisoftware.getId_commerce()));
				paramFila.put("token", token);
				
				try {
					resposta = Networking.performPostCall(urlEnviarFila, paramFila);
				} catch (IOException e1) {
					e1.printStackTrace();
					return "Problemas ao conectar com o web service. 1 "+e1.getMessage();
				}
				
				if (resposta == null)
					return "Problemas ao conectar com o web service. 2 resposta null";
				
				if (resposta.isEmpty() || resposta == "")
					return "Problemas ao recusar a Carga.";
				
				JSONObject objectFila;
				try {
					objectFila = new JSONObject(resposta);
					objectFila = new JSONObject(objectFila.getString("RecusarCargaResult"));
					String status = objectFila.getString("Status");
					String mensagemRetorno = objectFila.getString("Mensagem");
					if (status == "false")
						return mensagemRetorno;
					else {
						try {
							usuarioSelecionado.status_fila = EnumStatusFila.CargaRecusada.getValue();						
		                	usuarioSelecionado.save();
		                	return "Carga recusada!";
						} catch (ActiveRecordException e) {
							e.printStackTrace();
							return "Problemas ao alterar status da Fila.";
						}
					}
				} catch (Throwable t) {
					return "Problemas ao converter resposta do web service.";
				}
			}
		} else
			return "N�o h� conex�o com a internet! Favor ativar a mesma para continuar.";
	}

}

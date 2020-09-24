package MultiMobile.Threads;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.kroz.activerecord.ActiveRecordBase;
import org.kroz.activerecord.ActiveRecordException;
import org.kroz.activerecord.EntitiesHelper;

import br.inf.commerce.multimobilegpa.android.PrincipalActivity;
import br.inf.commerce.multimobilegpa.android.R;
import curso.utils.Networking;

import MultiMobile.DAO.ClienteMultisoftware;
import MultiMobile.DAO.MotivoSaidaFila;
import MultiMobile.DAO.Usuario;
import MultiMobile.Utils.Funcoes;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

public class SincronizarAtualizarDados extends
		AsyncTask<String, Integer, String> {

	AlertDialog alertDialog;
	private ActiveRecordBase db;
	private Context context;
	String usuarioLogado;

	public SincronizarAtualizarDados(AlertDialog alertDialog,
			ActiveRecordBase db, Context context, String usuarioLogado) {
		this.alertDialog = alertDialog;
		this.db = db;
		this.context = context;
		this.usuarioLogado = usuarioLogado;
	}

	@Override
	protected void onPostExecute(String result) {
		alertDialog.dismiss();

		alertDialog.setTitle("Retorno Atualização!");
		alertDialog.setMessage(result);
		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		alertDialog.show();
		final AlertDialog dialog = (AlertDialog) alertDialog;
		dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setEnabled(true);
		
		PrincipalActivity.atualizarDadosFila();

		super.onPostExecute(result);
	}

	@Override
	protected String doInBackground(String... params) {
		if (!Funcoes.verificaConexao(context)){
			return "Favor verificar a sua conex�o com a Internet!";
		}
		
		String resposta = null;
		String ws = context.getString(R.string.url_ws)+"Autenticacao.svc/AutenticarUsuario/"+
				context.getString(R.string.chave_ws)+
				"/"+usuarioLogado+
				"/"+Funcoes.getDeviceIMEI(context);

		try {
			resposta = Networking.getHttpRequet(ws); 
		} catch (IOException e1) {
			e1.printStackTrace();
			return "Problemas ao conectar com o web service - N�o habilitado";
		}
		
		if (Funcoes.isNullOrEmpty(resposta))
			return "Problemas ao conectar com o web service - N�o configurado";
						
		JSONObject usuarioJson;
		try {
			usuarioJson = new JSONObject(resposta);
			usuarioJson = new JSONObject(usuarioJson.getString("AutenticarUsuarioResult"));
			boolean status = usuarioJson.getBoolean("Status");
			if (!status)
				return usuarioJson.getString("Mensagem");
			else if (!Funcoes.validaVersaoAplicativo(context, usuarioJson.getString("VersaoAplicativoMobile")))
				return Funcoes.mensagemVersaoAplicativo(usuarioJson.getString("VersaoAplicativoMobile"));
			else
				usuarioJson = new JSONObject(usuarioJson.getString("Objeto"));
		} catch (Throwable t) {
			return "Problemas ao converter resposta do web service.";
		}
		
		//Salva empresas e placa
		int codigoUsuario = 0;
		String nomeUsuario = "";
		String placa = "";
		String transportadora = "";
		String urlEmbarcador = "";
		String linkVideoMobile = "";
		try {
			codigoUsuario = usuarioJson.getInt("Codigo");
			nomeUsuario = usuarioJson.getString("Nome");
			
			JSONArray array = (JSONArray) new JSONTokener(usuarioJson.getString("Empresas")).nextValue();					
			for (int i = 0; i < array.length(); i++){
				JSONObject objetosJson = array.getJSONObject(i);
								
				int codigoClienteMultisoftware = objetosJson.getInt("Codigo");
				String nomeClienteMultisoftware = objetosJson.getString("Descricao");
				if (i == 0){
					placa = objetosJson.getString("Placa");
					transportadora = objetosJson.getString("Transportadora");
					urlEmbarcador = objetosJson.getString("UrlEmbarcador");
					
					if (objetosJson.has("LinkVideoMobile"))
						linkVideoMobile = objetosJson.getString("LinkVideoMobile");
				}
			
				ClienteMultisoftware clienteMultisoftware = new ClienteMultisoftware(codigoClienteMultisoftware,
						nomeClienteMultisoftware,
						codigoClienteMultisoftware);
				
				try {
					if (db.find(ClienteMultisoftware.class, "IDCOMMERCE = ?", new String[] { String.valueOf(clienteMultisoftware.id_commerce)}).isEmpty()){
						try {
							ClienteMultisoftware clienteMultisoftwaredb;
							clienteMultisoftwaredb = db.newEntity(ClienteMultisoftware.class);
							EntitiesHelper.copyFieldsWithoutID(clienteMultisoftwaredb, clienteMultisoftware);
							clienteMultisoftwaredb.save();
						} catch (ActiveRecordException e) {
							e.printStackTrace();
							return "Problemas ao inserir novos Clientes Multisoftware.";
						}
					} else {
						ClienteMultisoftware clienteMultisoftwareAlterar = db.find(ClienteMultisoftware.class, "IDCOMMERCE = ?", new String[] { String.valueOf(clienteMultisoftware.id_commerce)}).get(0);
						clienteMultisoftwareAlterar.nome = nomeClienteMultisoftware;
						clienteMultisoftwareAlterar.codigo_integracao = codigoClienteMultisoftware;
					
						clienteMultisoftwareAlterar.save();
					}
				} catch (ActiveRecordException e) {
					e.printStackTrace();
					return "Problemas ao atualizar Clientes Multisoftware.";
				}
			}
		} catch (JSONException e) {
			e.printStackTrace(); 
			return "Problemas ao converter dados da empresa.";
		}
		
		//Carrega motivos sa�da Fila
		ws = context.getString(R.string.url_ws)+"FilaCarregamento.svc/ObterMotivosRetiradaFilaCarregamento";
		
		Map<String,String> paramPost = new HashMap<String,String>();
		ClienteMultisoftware clienteMultisoftware = null;
		clienteMultisoftware = Funcoes.retornaClienteMultisoftware(db);
		if (clienteMultisoftware != null){
			paramPost.put("token", context.getString(R.string.chave_ws));
			paramPost.put("usuario", String.valueOf(codigoUsuario));
			paramPost.put("empresaMultisoftware", String.valueOf(clienteMultisoftware.getId_commerce()));
			
			try {
				resposta = Networking.performPostCall(ws, paramPost);
			} catch (IOException e1) {
				e1.printStackTrace();
				return "Problemas ao conectar com o web service - N�o habilitado";
			}
			
			if (Funcoes.isNullOrEmpty(resposta))
				return "Problemas ao conectar com o web service - N�o configurado";
							
			JSONObject motivoSaidaJson;
			try {
				motivoSaidaJson = new JSONObject(resposta);
				motivoSaidaJson = new JSONObject(motivoSaidaJson.getString("ObterMotivosRetiradaFilaCarregamentoResult"));
				boolean status = motivoSaidaJson.getBoolean("Status");
				if (!status)
					return motivoSaidaJson.getString("Mensagem");
				else{
					JSONArray array = (JSONArray) new JSONTokener(motivoSaidaJson.getString("Objeto")).nextValue();
					for (int i = 0; i < array.length(); i++){
						try {
							motivoSaidaJson = array.getJSONObject(i);
							
							int codigoMotivo = motivoSaidaJson.getInt("Codigo");
							String descricaoMotivo = motivoSaidaJson.getString("Descricao");
							
							MotivoSaidaFila motivoSaidaFila = new MotivoSaidaFila(codigoMotivo, descricaoMotivo);							
							
							if (db.find(MotivoSaidaFila.class, "IDCOMMERCE = ?", new String[] { String.valueOf(motivoSaidaFila.id_commerce)}).isEmpty()){
								try {
									MotivoSaidaFila motivoSaidaFiladb;
									motivoSaidaFiladb = db.newEntity(MotivoSaidaFila.class);
									EntitiesHelper.copyFieldsWithoutID(motivoSaidaFiladb, motivoSaidaFila);
									motivoSaidaFiladb.save();
								} catch (ActiveRecordException e) {
									e.printStackTrace();
									return "Problemas ao inserir novos Motivos de Sa�da de Fila.";
								}
							} else {
								MotivoSaidaFila motivoSaidaFilaAlterar = db.find(MotivoSaidaFila.class, "IDCOMMERCE = ?", new String[] { String.valueOf(motivoSaidaFila.id_commerce)}).get(0);
								motivoSaidaFilaAlterar.descricao = descricaoMotivo;
							
								motivoSaidaFilaAlterar.save();
							}
						} catch (ActiveRecordException e) {
							e.printStackTrace();
							return "Problemas ao atualizar Motivos de Sa�da de Fila.";							
						} catch (JSONException e) {
							e.printStackTrace();
							return "Problemas ao converter os Motivos de Sa�da de Fila."; 
						}
					}
				}
			} catch (Throwable t) {
				return "Problemas ao converter resposta do web service.";
			}
		} else
			return "Nenhum Embarcador configurado para o usu�rio Obter Motivos de Retirada da Fila";
		
		//Atualiza o usu�rio		
		try {
			if (!db.find(Usuario.class, "IDCOMMERCE = ?", new String[] { String.valueOf(codigoUsuario)}).isEmpty()){
				Usuario usuarioAlterar = db.find(Usuario.class, "IDCOMMERCE = ?", new String[] { String.valueOf(codigoUsuario)}).get(0);
				usuarioAlterar.setNome_funcionario(nomeUsuario);
				usuarioAlterar.setPlaca(placa);
				usuarioAlterar.setTransportadora(transportadora);
				usuarioAlterar.setUrl_embarcador(urlEmbarcador);
				usuarioAlterar.setLink_video(linkVideoMobile);
			
				usuarioAlterar.save();
				return "Atualizado com sucesso.";
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
			return "Problemas ao atualizar o Usu�rio.";
		}
		
		return "Problemas ao atualizar dados.";
	}
}

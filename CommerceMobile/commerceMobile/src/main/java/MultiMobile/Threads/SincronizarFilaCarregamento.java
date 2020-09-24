package MultiMobile.Threads;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.kroz.activerecord.ActiveRecordBase;
import org.kroz.activerecord.ActiveRecordException;
import org.kroz.activerecord.EntitiesHelper;

import curso.utils.Networking;

import br.inf.commerce.multimobilegpa.android.R;
import br.inf.commerce.multimobilegpa.android.PrincipalActivity;
import br.inf.commerce.multimobilegpa.android.SignalRNotificationService;
import MultiMobile.DAO.ClienteMultisoftware;
import MultiMobile.DAO.NotificacaoFila;
import MultiMobile.DAO.Usuario;
import MultiMobile.Utils.EnumSituacaoFilaCarregamentoWEB;
import MultiMobile.Utils.EnumSituacaoMotoristaFilaCarregamentoWEB;
import MultiMobile.Utils.EnumStatusFila;
import MultiMobile.Utils.Funcoes;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;

public class SincronizarFilaCarregamento extends
		AsyncTask<String, Integer, String> {
	
	AlertDialog alertDialog;
	private ActiveRecordBase db;
	private Context context;
	String usuarioLogado;
	String tipoFilaCarregamento; //Reversa = 1,    Vazio = 2
	String tipoOperacaoCarregamento; //Entrada ou Saída da Fila
	String paramSaidaFila;
	String lojaProximidade; //1 - Sim ou 0- n�o
	String latitude;
	String longitude;
	boolean atualizarAposRetorno = false;

	public SincronizarFilaCarregamento(AlertDialog alertDialog, ActiveRecordBase db, Context context, String usuarioLogado) {
		this.alertDialog = alertDialog;
		this.db = db;
		this.context = context;
		this.usuarioLogado = usuarioLogado;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(String result) {
		alertDialog.dismiss();
		
		if (!result.equals("SemAtualizacao")){
			alertDialog.setTitle("Retorno Fila!");
			alertDialog.setMessage(result);
			alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
		    new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		            dialog.dismiss();
		            if (atualizarAposRetorno)
		            	PrincipalActivity.createSincronizarObterDadosFilaDialog();
		        }
		    });
			
			alertDialog.show();
			final AlertDialog dialog = (AlertDialog)alertDialog;
	    	dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setEnabled(true);
		}
		
		PrincipalActivity.atualizarDadosFila();
		PrincipalActivity.limparProgress();

		super.onPostExecute(result);
	}

	@Override
	protected String doInBackground(String... params) {
		tipoFilaCarregamento = params[0];
		tipoOperacaoCarregamento = params[1];
		paramSaidaFila = params[2];
		lojaProximidade = params[3];
		latitude = params[4];
		longitude = params[5];
		
		if (Funcoes.verificaConexao(context)) {
			String urlEnvio = context.getString(R.string.url_ws)+"FilaCarregamento.svc/";
			String token = context.getString(R.string.chave_ws);
			Usuario usuarioSelecionado = Funcoes.retornaUsuario(db, usuarioLogado);
			ClienteMultisoftware clienteMultisoftware = Funcoes.retornaClienteMultisoftware(db);
			if (clienteMultisoftware == null)
				return "Nenhum Embarcador configurado para o usuário dar Entrada na Fila";
			
			Map<String,String> paramFila = new HashMap<String,String>();		
			String resposta = null;
			
			if (tipoOperacaoCarregamento.equals("Entrada")){
				String urlEnviarFila = urlEnvio + "EntrarFilaCarregamento";
				
				paramFila.put("usuario", String.valueOf(usuarioSelecionado.getId_commerce()));
				paramFila.put("empresaMultisoftware", String.valueOf(clienteMultisoftware.getId_commerce()));
				paramFila.put("tipoFilaCarregamento", tipoFilaCarregamento);
				paramFila.put("token", token);
				paramFila.put("latitude", latitude);
				paramFila.put("longitude", longitude);
				
				if (tipoFilaCarregamento.equals("1"))//Reversa
					paramFila.put("lojaProximidade", lojaProximidade);
				
				try {
					resposta = Networking.performPostCall(urlEnviarFila, paramFila);
				} catch (IOException e1) {
					e1.printStackTrace();
					return "Problemas ao conectar com o web service. 1 "+e1.getMessage();
				}
				
				if (resposta == null)
					return "Problemas ao conectar com o web service. 2 resposta null";
				
				if (resposta.isEmpty() || resposta == "")
					return "Problemas ao enviar a Fila.";
		
				JSONObject objectFila;
				try {
					objectFila = new JSONObject(resposta);
					objectFila = new JSONObject(objectFila.getString("EntrarFilaCarregamentoResult"));
					String status = objectFila.getString("Status");
					String mensagemRetorno = objectFila.getString("Mensagem");
					if (status == "false"){
						atualizarAposRetorno = true;
						return mensagemRetorno;
					}else {
						try {
							if (tipoFilaCarregamento.equals("1"))//Reversa
								usuarioSelecionado.status_fila = EnumStatusFila.EmReversa.getValue();
							else
								usuarioSelecionado.status_fila = EnumStatusFila.NaFila.getValue();
							
							objectFila = new JSONObject(objectFila.getString("Objeto"));
		                	usuarioSelecionado.posicao_fila = objectFila.getInt("Posicao");
		                	usuarioSelecionado.local_fila = objectFila.getString("DescricaoCentroCarregamento");
		                	
		                	usuarioSelecionado.save();
							return "Entrou com sucesso da Fila \n\nSua posição atual é: " + String.valueOf(usuarioSelecionado.posicao_fila) + "\nNo CD: " + usuarioSelecionado.local_fila;
						} catch (ActiveRecordException e) {
							e.printStackTrace();
							return "Problemas ao alterar status da Fila.";
						}
					}
				} catch (Throwable t) {
					return "Problemas ao converter resposta do web service.";
				}
			} else if (tipoOperacaoCarregamento.equals("Saída")) { //Sa�da da fila
				
				String urlEnviarFila = urlEnvio + "SairFilaCarregamento";
				
				paramFila.put("usuario", String.valueOf(usuarioSelecionado.getId_commerce()));
				paramFila.put("empresaMultisoftware", String.valueOf(clienteMultisoftware.getId_commerce()));
				paramFila.put("token", token);
				
				if (tipoFilaCarregamento.equals("1")){//Reversa
					paramFila.put("hash", paramSaidaFila);
					urlEnviarFila = urlEnvio + "SairReversa";
				} else {
					paramFila.put("motivoRetiradaFilaCarregamento", paramSaidaFila);
				}
				
				try {
					resposta = Networking.performPostCall(urlEnviarFila, paramFila);
				} catch (IOException e1) {
					e1.printStackTrace();
					return "Problemas ao conectar com o web service. 1 "+e1.getMessage();
				}
				
				if (resposta == null)
					return "Problemas ao conectar com o web service. 2 resposta null";
				
				if (resposta.isEmpty() || resposta == "")
					return "Problemas ao enviar a Fila.";
				
				JSONObject objectFila;
				try {
					objectFila = new JSONObject(resposta);
					if (tipoFilaCarregamento.equals("1"))//Reversa
						objectFila = new JSONObject(objectFila.getString("SairReversaResult"));
					else
						objectFila = new JSONObject(objectFila.getString("SairFilaCarregamentoResult"));
					String status = objectFila.getString("Status");
					String mensagemRetorno = objectFila.getString("Mensagem");
					if (status == "false")
						return mensagemRetorno;
					else {
						try {
							int situacaoFila = 0;
							if (tipoFilaCarregamento.equals("1")){//Ao sair da reversa, fica na fila normal
								usuarioSelecionado.status_fila = EnumStatusFila.NaFila.getValue();
								mensagemRetorno = "Saiu com sucesso da Reversa!\nAgora você voltou pra Fila";
								
								objectFila = new JSONObject(objectFila.getString("Objeto"));
			                	usuarioSelecionado.posicao_fila = objectFila.getInt("Posicao");
			                	usuarioSelecionado.local_fila = objectFila.getString("DescricaoCentroCarregamento");			                	
			                	situacaoFila = objectFila.getInt("Situacao");
			                	
								if (situacaoFila == EnumSituacaoFilaCarregamentoWEB.Removido.getValue() || 
										situacaoFila == EnumSituacaoFilaCarregamentoWEB.Disponivel.getValue()) //Foi removido ou conclui a carga durante o processo, volta pra dispon�vel
									situacaoFila = 0;
							} else {
								situacaoFila = EnumStatusFila.AgRemocao.getValue();
								usuarioSelecionado.status_fila = situacaoFila;
								mensagemRetorno = "Sua saída da Fila está em aprovação!\nAguarde a liberação";
							}
							
							if (situacaoFila == 0){
								usuarioSelecionado.status_fila = EnumStatusFila.Disponivel.getValue();
			                	usuarioSelecionado.posicao_fila = 0;
			                	usuarioSelecionado.local_fila = "";
			                	mensagemRetorno = "SemAtualizacao";
							}
							
		                	usuarioSelecionado.save();
		                	return mensagemRetorno;
						} catch (ActiveRecordException e) {
							e.printStackTrace();
							return "Problemas ao alterar status da Fila.";
						}
					}
				} catch (Throwable t) {
					return "Problemas ao converter resposta do web service.";
				}
			} else if (tipoOperacaoCarregamento.equals("NaFila")) { //Obtem dados da posição da fila
				
				String urlEnviarFila = urlEnvio + "ObterDadosFilaCarregamento";
				
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
					return "Problemas ao enviar a Fila.";
				
				JSONObject objectFila;
				try {
					objectFila = new JSONObject(resposta);					
					objectFila = new JSONObject(objectFila.getString("ObterDadosFilaCarregamentoResult"));
					String status = objectFila.getString("Status");
					String mensagemRetorno = objectFila.getString("Mensagem");
					if (status == "false")
						return mensagemRetorno;
					else if (!Funcoes.validaVersaoAplicativo(context, objectFila.getString("VersaoAplicativoMobile")))
						return Funcoes.mensagemVersaoAplicativo(objectFila.getString("VersaoAplicativoMobile"));
					else {
						try {
//							int statusArmazenado = usuarioSelecionado.getStatus_fila();
							int posicaoArmazenada = usuarioSelecionado.getPosicao_fila();
							String localArmazenado = usuarioSelecionado.getLocal_fila();
							mensagemRetorno = "SemAtualizacao";
							
							if (Funcoes.isNullOrEmpty(objectFila.getString("Objeto"))){ //Retorno null deixa fora da fila
								usuarioSelecionado.status_fila = EnumStatusFila.Disponivel.getValue();
			                	usuarioSelecionado.posicao_fila = 0;
			                	usuarioSelecionado.local_fila = "";
								
								usuarioSelecionado.save();
								return mensagemRetorno;
							}
							
							objectFila = new JSONObject(objectFila.getString("Objeto"));
							int situacaoFila = objectFila.getInt("Situacao");
							int situacaoMotorista = objectFila.getInt("SituacaoMotorista");
							
							if (situacaoFila == EnumSituacaoFilaCarregamentoWEB.NaFila.getValue() && 
									situacaoMotorista == EnumSituacaoMotoristaFilaCarregamentoWEB.AguardandoConfirmacao.getValue()){ //Aguardando confirma��o carga
								usuarioSelecionado.status_fila = EnumStatusFila.NaFilaAgCarga.getValue();
								
								// Gera notificação
								Intent service = new Intent(context, SignalRNotificationService.class);
								service.putExtra("Message", "Você foi alocado para uma carga! Favor confirmar.");
								context.startService(service);
								
							} else if (situacaoFila == EnumSituacaoFilaCarregamentoWEB.NaFila.getValue() && 
									situacaoMotorista == EnumSituacaoMotoristaFilaCarregamentoWEB.RecusouCarga.getValue()){ //Recusou carga
								usuarioSelecionado.status_fila = EnumStatusFila.CargaRecusada.getValue();
								
							} else if (situacaoFila == EnumSituacaoFilaCarregamentoWEB.Removido.getValue() || 
									situacaoFila == EnumSituacaoFilaCarregamentoWEB.Disponivel.getValue()){ //Foi removido ou entregou a carga
								usuarioSelecionado.status_fila = EnumStatusFila.Disponivel.getValue();
			                	usuarioSelecionado.posicao_fila = 0;
			                	usuarioSelecionado.local_fila = "";

							} else if (situacaoFila == EnumSituacaoFilaCarregamentoWEB.EmViagem.getValue()){ //Em viagem
								usuarioSelecionado.status_fila = EnumStatusFila.EmViagem.getValue();
								usuarioSelecionado.local_fila = objectFila.getString("DescricaoCentroCarregamento");
								
							} else if (situacaoFila == EnumSituacaoFilaCarregamentoWEB.EmRemocao.getValue()){ //Aguardando Remo��o
								usuarioSelecionado.status_fila = EnumStatusFila.AgRemocao.getValue();
								usuarioSelecionado.local_fila = objectFila.getString("DescricaoCentroCarregamento");
								mensagemRetorno = "Sua saída da Fila ainda não foi aprovada! Favor aguardar.";
								
							} else {
								if (objectFila.getInt("Tipo") == 1)
									usuarioSelecionado.status_fila = EnumStatusFila.EmReversa.getValue();
								else
									usuarioSelecionado.status_fila = EnumStatusFila.NaFila.getValue();
								
			                	usuarioSelecionado.posicao_fila = objectFila.getInt("Posicao");
			                	usuarioSelecionado.local_fila = objectFila.getString("DescricaoCentroCarregamento");
			                	
			                	if (posicaoArmazenada != usuarioSelecionado.posicao_fila || !localArmazenado.equals(usuarioSelecionado.local_fila)){			                		
			                		NotificacaoFila notificacaoFila = new NotificacaoFila(
			                				"Sua nova posição é: " + String.valueOf(usuarioSelecionado.posicao_fila));
									try {
										NotificacaoFila notificacaoFiladb;
										notificacaoFiladb = db.newEntity(NotificacaoFila.class);
										EntitiesHelper.copyFieldsWithoutID(notificacaoFiladb, notificacaoFila);
										notificacaoFiladb.save();
									} catch (ActiveRecordException e) {
										e.printStackTrace();
									}
			                	}
			                	
			                	if (situacaoMotorista == EnumSituacaoMotoristaFilaCarregamentoWEB.PerdeuSenha.getValue()){
			                		// Gera notifica��o
									Intent service = new Intent(context, SignalRNotificationService.class);
									service.putExtra("Message", "Você perdeu senha na fila!");
									context.startService(service);
			                	}
							}
							
		                	usuarioSelecionado.save();		                	
		                	return mensagemRetorno;
						} catch (ActiveRecordException e) {
							e.printStackTrace();
							return "Problemas ao alterar status da Fila.";
						}
					}
				} catch (Throwable t) {
					return "Problemas ao converter resposta do web service.";
				}
			} else if (tipoOperacaoCarregamento.equals("InformarDoca")) { //Envia qrcode doca
				
				String urlEnviarFila = urlEnvio + "InformarDoca";
				
				paramFila.put("usuario", String.valueOf(usuarioSelecionado.getId_commerce()));
				paramFila.put("empresaMultisoftware", String.valueOf(clienteMultisoftware.getId_commerce()));
				paramFila.put("token", token);
				paramFila.put("hash", paramSaidaFila);
				
				try {
					resposta = Networking.performPostCall(urlEnviarFila, paramFila);
				} catch (IOException e1) {
					e1.printStackTrace();
					return "Problemas ao conectar com o web service. 1 "+e1.getMessage();
				}
				
				if (resposta == null)
					return "Problemas ao conectar com o web service. 2 resposta null";
				
				if (resposta.isEmpty() || resposta == "")
					return "Problemas ao enviar a Doca.";
				
				JSONObject objectFila;
				try {
					objectFila = new JSONObject(resposta);
					objectFila = new JSONObject(objectFila.getString("InformarDocaResult"));
					String status = objectFila.getString("Status");
					String mensagemRetorno = objectFila.getString("Mensagem");
					if (status == "false")
						return mensagemRetorno;
					else
						return "Doca informada com sucesso!";
				} catch (Throwable t) {
					return "Problemas ao converter resposta do web service.";
				}
			}
		} else
			return "Não há conexão com a internet! Favor ativar a mesma para continuar.";
		
		return "Sem retorno! Favor verificar com o suporte";
	}

}

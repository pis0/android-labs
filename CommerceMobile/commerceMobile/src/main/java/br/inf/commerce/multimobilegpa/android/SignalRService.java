package br.inf.commerce.multimobilegpa.android;

import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;
import org.kroz.activerecord.ActiveRecordBase;
import org.kroz.activerecord.ActiveRecordException;

import microsoft.aspnet.signalr.client.ConnectionState;
import microsoft.aspnet.signalr.client.Credentials;
import microsoft.aspnet.signalr.client.ErrorCallback;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.StateChangedCallback;
import microsoft.aspnet.signalr.client.http.Request;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;
import microsoft.aspnet.signalr.client.transport.ClientTransport;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;
import MultiMobile.DAO.Usuario;
import MultiMobile.Utils.EnumStatusFila;
import MultiMobile.Utils.EnumTipoAlteracaoFilaCarregamentoWEB;
import MultiMobile.Utils.Funcoes;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class SignalRService extends Service {
	private HubConnection mHubConnection;
	private HubProxy mHubProxy;
	private Handler mHandler;
	private final IBinder mBinder = new LocalBinder();
	private static Context context;
	private static AlertDialog alertDialog;
	
	private ActiveRecordBase dbThread;
	int dbVersion;
	
	private Usuario usuarioLogado = null;
	String HoraAtual = "";
	boolean ExcedeuTentativaConexao = false;
	
	@Override
	public void onCreate() {
		super.onCreate();
		context = this;
		dbVersion = Integer.parseInt(context.getString(R.string.dbVersion));
		
		try {
			dbThread = ActiveRecordBase.open(context.getApplicationContext(), "multimobilegpa.db", dbVersion);
		} catch (ActiveRecordException e) {
			Log.d("SIGNALR", "Erro open bd: " + e.getMessage().toString());
			e.printStackTrace();
		}
		
		usuarioLogado = Funcoes.retornaUsuario(dbThread);
		
		mHandler = new Handler(Looper.getMainLooper()) {
		    @Override
		    public void handleMessage(Message message) {
		    	Log.d("SIGNALR", "handleMessage");
		    }
		};
		
        Log.d("SIGNALR", "onCreate Service");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("SIGNALR", "onStartCommand");
		int result = super.onStartCommand(intent, flags, startId);
		limparCampos();
		startSignalR();
		return result;
	}

	@Override
	public void onDestroy() {
		Log.e("SIGNALR", "onDestroy id - " + mHubConnection.getConnectionId());
		limparCampos();
		mHubConnection.stop();
		super.onDestroy();
	}
	
	@Override
	public void onTaskRemoved(Intent rootIntent) {
		Log.e("SIGNALR", "onTaskRemoved id - " + mHubConnection.getConnectionId());
		limparCampos();
		mHubConnection.stop();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.d("SIGNALR", "onBind");
		limparCampos();
		startSignalR();
		return mBinder;
	}

	public class LocalBinder extends Binder {
		public SignalRService getService() {
			return SignalRService.this;
		}
	}

	private void startSignalR() {
		Platform.loadPlatformComponent(new AndroidPlatformComponent());
		Log.d("SIGNALR", "startSignalR");
		
		try{
			Credentials credentials = new Credentials() {
				@Override
				public void prepareRequest(Request request) {
					request.addHeader("chave-usuario", usuarioLogado.getUsuario());
				}
			};
			
			if ((mHubConnection != null) && (mHubConnection.getState() == ConnectionState.Connected)){
				mHubConnection.getState().toString();
			} else {
			
				mHubConnection = new HubConnection(usuarioLogado.getUrl_embarcador());
				mHubConnection.setCredentials(credentials);
				mHubProxy = mHubConnection.createHubProxy("NotificacaoMobile");//FilaCarregamentoMobile
				ClientTransport clientTransport = new ServerSentEventsTransport(mHubConnection.getLogger());
				SignalRFuture<Void> signalRFuture = mHubConnection.start(clientTransport);
		
				try {
					signalRFuture.get();
					Log.d("SIGNALR", "SignalR connected with id - " + mHubConnection.getConnectionId());
					limparCampos();
					if (PrincipalActivity.active){
                		Log.d("SIGNALR", "Start sincronismo após nova conexão");
                		PrincipalActivity.createSincronizarObterDadosFilaDialog();
                	}
				} catch (InterruptedException e) {
					Log.e("SignalRService", "Start InterruptedException: " + e.toString());
				} catch (ExecutionException e) {
					Log.e("SignalRService", "Start ExecutionException: " + e.toString());
					mHubConnection.disconnect();
				}
	
				signalRFuture.onError(new ErrorCallback() {
	
			        @Override
			        public void onError(Throwable throwable) {
			            Log.e("SIGNALR", "ERROR FUTURE: " + throwable.getMessage(), throwable);
			        }
			    });
			}
		
			String nomeMetodoBroadcastCliente = "notificar";//notificarAlteracao
			mHubProxy.on(nomeMetodoBroadcastCliente,
				new SubscriptionHandler1<String>() {
					@Override
					public void run(final String message) {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								String mensagem = "";
								int tipoAlteracao = 0;
								JSONObject objectFila;
								try {
									objectFila = new JSONObject(message);
									mensagem = objectFila.getString("Mensagem");
									tipoAlteracao = objectFila.getInt("Tipo");//TipoAlteracao
								} catch (JSONException e1) {
									e1.printStackTrace();
									Log.d("SIGNALR", "Receive notification error - "+ e1.getMessage());
								}
								
								Log.d("SIGNALR", "Receive notification");
								
								// Gera notifica��o
								Intent service = new Intent(context, SignalRNotificationService.class);
								service.putExtra("Message", mensagem);
								if (tipoAlteracao == EnumTipoAlteracaoFilaCarregamentoWEB.Mensagem.getValue())
									service.putExtra("NotificacaoTipoMensagem", String.valueOf(true));
								context.startService(service);
								
								try {
									if (tipoAlteracao == EnumTipoAlteracaoFilaCarregamentoWEB.PerdeuSenha.getValue() ||
											tipoAlteracao == EnumTipoAlteracaoFilaCarregamentoWEB.CargaCancelada.getValue() ||
											tipoAlteracao == EnumTipoAlteracaoFilaCarregamentoWEB.SolicitacaoSaidaRecusada.getValue())
										usuarioLogado.status_fila = EnumStatusFila.NaFila.getValue();
									else if (tipoAlteracao == EnumTipoAlteracaoFilaCarregamentoWEB.CargaAlocada.getValue())
										usuarioLogado.status_fila = EnumStatusFila.NaFilaAgCarga.getValue();
									else if (tipoAlteracao == EnumTipoAlteracaoFilaCarregamentoWEB.SolicitacaoSaidaAceita.getValue())
										usuarioLogado.status_fila = EnumStatusFila.Disponivel.getValue();
									
									usuarioLogado.save();
									if (PrincipalActivity.active)
										PrincipalActivity.atualizarDadosFila();
								} catch (ActiveRecordException are) {
									are.printStackTrace();
									Log.d("SIGNALR", "Notifica��o: " + are.getMessage());
								} catch (Exception e) {
									e.printStackTrace();
									Log.d("SIGNALR", "Notifica��o: " + e.getMessage());
								}
							}
						});
					}
				}, String.class);				
			
			mHubConnection.closed(new Runnable() {
	            @Override
	            public void run() {
	            	Log.e("SIGNALR", "DISCONNECTED");		            	
	            	
	            	try {
		            	if ((PrincipalActivity.active) && (Funcoes.verificaConexao(context))){
			            	((Activity) PrincipalActivity.contextPrincipalActivity).runOnUiThread(new Runnable(){
			                public void run(){
			                	if (PrincipalActivity.active)
			                		if (alertDialog == null){
			                			Log.d("SIGNALR", "Start show timer connection");
			                			alertDialog = PrincipalActivity.showDialogSincronismoPadrao("Servidor", "Tentando reconectar ao servidor, favor aguardar!");
			                			Log.d("SIGNALR", "show timer connection active");
			                		}
			                }});
			            }
		            	
		            	tratarReconexao();
	            	} catch (Exception e) {
	                	Log.d("SIGNALR", "DISCONNECTED Exception - " +  e.getMessage());
	                }
	            }
	        });
			
			mHubConnection.connected(new Runnable() {
	            @Override
	            public void run() {
	                HoraAtual = "";
	                if (alertDialog != null)
	                	alertDialog.dismiss();
	                alertDialog = null;
	                
	                Log.d("SIGNALR", "CONNECTED ID - " + mHubConnection.getConnectionId());
	                
	                try {
		                if (PrincipalActivity.active){
		                	((Activity) PrincipalActivity.contextPrincipalActivity).runOnUiThread(new Runnable(){
			                public void run(){
			                	if (PrincipalActivity.active){
			                		Log.d("SIGNALR", "Start sincronismo ap�s conex�o");
			                		PrincipalActivity.createSincronizarObterDadosFilaDialog();
			                	}
			                }});
		                }
	                } catch (Exception e) {
	                	Log.d("SIGNALR", "connected exception - " +  e.getMessage());
	                }
	            }
	        });
			
			mHubConnection.reconnected(new Runnable() {
	            @Override
	            public void run() {
	            	Log.d("SIGNALR", "RECONNECTED ID." + mHubConnection.getConnectionId());
	            	
	            	try {
		                if (PrincipalActivity.active){
		                	((Activity) PrincipalActivity.contextPrincipalActivity).runOnUiThread(new Runnable(){
			                public void run(){
			                	if (PrincipalActivity.active){
			                		Log.d("SIGNALR", "Start sincronismo ap�s reconex�o");
			                		PrincipalActivity.createSincronizarObterDadosFilaDialog();
			                	}
			                }});
		                }
	                } catch (Exception e) {
	                	Log.d("SIGNALR", "connected exception - " +  e.getMessage());
	                }
	            }
	        });
			
			mHubConnection.reconnecting(new Runnable() {
	            @Override
	            public void run() {
	            	Log.d("SIGNALR", "RECONNECTING ID." + mHubConnection.getConnectionId());		            	
	            }
	        });
			
			mHubConnection.connectionSlow(new Runnable() {
	            @Override
	            public void run() {
	            	Log.d("SIGNALR", "CONNECTION SLOW." + mHubConnection.getConnectionId());		            	
	            }
	        });
			
			mHubConnection.stateChanged(new StateChangedCallback() {
				@Override
				public void stateChanged(ConnectionState oldState,
						ConnectionState newState) {
					Log.d("SIGNALR", "STATE CHANGED FROM " + oldState.toString() + " TO " + newState.toString());						
				}
	        });
			
			mHubConnection.error(new ErrorCallback() {
                @Override
                public void onError(Throwable throwable) {
                	Log.e("SIGNALR", "ERROR: ", throwable);
            }});
		
		} catch (Exception e) {
			Log.d("SIGNALR", "Exception startSignalR - " +  e.getMessage());
		}
	}
	
	private class reconnectPulling extends AsyncTask<String, Void, String> {

        @Override
		protected void onPostExecute(String result) {
        	Log.d("SIGNALR", "reconnectPulling");
        	
        	if (result.equals("Erro") && (PrincipalActivity.active)){
        		Log.d("SIGNALR", "Start close app...");
        		
        		if (!ExcedeuTentativaConexao)
        			gerarNotificacao("Erro de conex�o! Favor abrir o aplicativo novamente.");
        		
	        	PrincipalActivity.sair = true;
	        	((Activity) PrincipalActivity.contextPrincipalActivity).finish();
	        	Log.d("SIGNALR", "App closed...");
        	}
			super.onPostExecute(result);
		}

		@Override
        protected String doInBackground(String... params) {
			Log.d("SIGNALR", "doInBackground");
            for (int i = 0; i < 5; i++) {
                try {
                	if (ExcedeuTentativaConexao)
                		throw new InterruptedException();
                	
                    if ((mHubConnection != null) && (mHubConnection.getState() != ConnectionState.Connected) && (Funcoes.verificaConexao(context))) {
//                    	mHubConnection.start();
                    	startSignalR();
                        Log.d("SIGNALR", "Reconnect To Server...");
                        Thread.sleep(5000);
                    }
                } catch (InterruptedException e) {
                	Log.e("SIGNALR", "Ocorreu InterruptedException no doInBackground");
                    Thread.interrupted();
                    return "Erro";
                }
            }
            return "";
        }
    }
	
	private void tratarReconexao(){
		Log.d("SIGNALR", "tratarReconexao");
		
		if ((mHubConnection != null) && (mHubConnection.getState() == ConnectionState.Disconnected) && (Funcoes.verificaConexao(context)) && (!ExcedeuTentativaConexao)) {
        	
        	Log.d("SIGNALR", "TRY RECONNECT!");
        	if (HoraAtual == ""){ //Pega hora para validar o tempo de tentativas
        		HoraAtual = Funcoes.PegarHoraAtual();
        	}
        	
        	long horaAtual = Funcoes.stringTimeToLong(Funcoes.PegarHoraAtual());
        	long horaInicioReconnect = Funcoes.stringTimeToLong(HoraAtual);
        	
        	if ((horaAtual - horaInicioReconnect) < Integer.parseInt(context.getString(R.string.time_reconnection_signalr))){
        		Log.d("SIGNALR", "Time reconnect: " + String.valueOf(horaAtual - horaInicioReconnect));
        		new reconnectPulling().execute();
        	} else{
        		ExcedeuTentativaConexao = true;
        		Log.d("SIGNALR", "Excedeu o tempo de tentativa de conex�o ao servidor! Favor abrir o aplicativo novamente.");
        		
        		// Gera notifica��o erro conex�o
				gerarNotificacao("Excedeu o tempo de tentativa de conex�o ao servidor! Favor abrir o aplicativo novamente.");
        	}
        }
	}
	
	private void gerarNotificacao(String message){
		Intent service = new Intent(context, SignalRNotificationService.class);
		service.putExtra("Message", message);
		context.startService(service);
	}
	
	private void limparCampos(){
		ExcedeuTentativaConexao = false;
		HoraAtual = "";
		if (alertDialog != null)
        	alertDialog.dismiss();
		alertDialog = null;
	}
}
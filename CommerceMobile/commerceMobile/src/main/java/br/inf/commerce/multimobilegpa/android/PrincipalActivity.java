package br.inf.commerce.multimobilegpa.android;

import java.util.ArrayList;
import java.util.List;

import org.kroz.activerecord.ActiveRecordBase;
import org.kroz.activerecord.ActiveRecordException;

import br.inf.commerce.multimobilegpa.android.R;

import MultiMobile.DAO.Usuario;
import MultiMobile.DTO.DadosCargaOnlineDTO;
import MultiMobile.DTO.DestinoCargaOnlineDTO;
import MultiMobile.Services.GPSTracker;
import MultiMobile.Threads.SincronizarAtualizarDados;
import MultiMobile.Threads.SincronizarChegadaSaida;
import MultiMobile.Threads.SincronizarDadosCargaDestinoOnline;
import MultiMobile.Threads.SincronizarDadosCargaOnline;
import MultiMobile.Threads.SincronizarDeslogarUsuario;
import MultiMobile.Threads.SincronizarDetalhesCargaFila;
import MultiMobile.Threads.SincronizarFilaCarregamento;
import MultiMobile.Utils.Dialogs;
import MultiMobile.Utils.EnumStatusFila;
import MultiMobile.Utils.Funcoes;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PrincipalActivity extends Activity { //implements ServiceConnection
	public static boolean sair;
    public static boolean active = false;
    public static boolean limparDados = false;
	private static String usuarioLogado;
	private static Usuario usuarioSelecionado = null;
	
	private static ActiveRecordBase db;
//	private static ProgressDialog mProgressDialog;
	private static ProgressDialog mProgressCarregarLista;
	private static AlertDialog alertDialog;
	
//	public static final int DIALOG_SINCRONISMO_PROGRESS = 2;
	public static final int DIALOG_CARREGAR_LISTA_PROGRESS = 3;
	public static final int REQUEST_CODE_FILA = 7;
	public static final int REQUEST_CODE_INFORMAR_DOCA = 8;
	public static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
	
//	private static List<NotificacaoFila> listNotificacoesFila = null;
//	private static ListView lvNotificacaoFila;
	public static Context contextPrincipalActivity;
//	private ServiceConnection mConnection;
//	private SignalRService mService;
//	private boolean mBound = false;
	
	private static TextView lblPlacaFila;
	private static TextView lblMotoristaFila;
	private static TextView lblTransportadoraFila;
	private static TextView lblStatusFila;
	private static TextView lblPosicaoFila;
	private static Button btnEntradaFila;
	private static Button btnQuantidadeCargas;
	private static Button btnSaidaFila;
	private static Button btnAtualizarDadosFila;
	private static Button btnInformarDocaFila;
	private static Button btnDadosCargaFila;
	private static Button btnOcorrenciaGLog;
	private static Button btnInformarChegada;
	private static Spinner spnMotivoSaidaFila = null;
	private static Spinner spnDestinosCarga = null;
	
	private static String notificacaoTipoMensagem = "false";
	public static ArrayList<DadosCargaOnlineDTO> listRelatorioDadosCargaOnline;
	public static ArrayList<DestinoCargaOnlineDTO> listDestinosCargaOnline;
	
	@SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fila);
        contextPrincipalActivity = PrincipalActivity.this;
        
        ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ED7D31")));

        db = ((CommerceMobileApp)getApplication()).getDatabase();        
        sair = false;
        limparDados = false;
        
        Intent intent = getIntent();
		Bundle params = intent.getExtras();
		usuarioLogado = params.getString("usuarioLogado");
		notificacaoTipoMensagem = params.getString("NotificacaoTipoMensagem");
		usuarioSelecionado = Funcoes.retornaUsuario(db, usuarioLogado);
		
//		startService(new Intent(contextPrincipalActivity, SignalRService.class));//Serviço receber notificação
//		mConnection = this;
//		bindService(new Intent("SERVICO_CONEXAO"), mConnection, Context.BIND_AUTO_CREATE);
		
		try {
			if (!isServiceRunning("br.inf.commerce.multimobilegpa.SignalRService")){
				Log.d("SIGNALR","Not exists Service");
				startService(new Intent("SERVICO_CONEXAO_MOBILEGPA"));
			} else
				Log.d("SIGNALR","Exists Service");
		} catch (Exception e) {
			Toast.makeText(contextPrincipalActivity, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		
        if (savedInstanceState == null)
			getFragmentManager().beginTransaction().add(R.id.container, new PlanetFragment()).commit();
    }
	
	public boolean isServiceRunning(String servicoClassName) {
	     ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	     List<RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
	     
	     Log.d("SIGNALR","Procurando Services");
	     for (int i = 0; i < services.size(); i++) {
		     if(services.get(i).service.getClassName().compareTo(servicoClassName) == 0){
		    	 Log.d("SIGNALR","Service Nr. " + i + " class name : " + services.get(i).service.getClassName());
		         return true;
		     }
	     }
	     return false;
	 }
	
	/*public void stopService(View view){
		Log.e("SIGNALR", "stopService");
    	stopService(new Intent("SERVICO_CONEXAO"));
    	unbindService(mConnection);
    }
    
    public void startService(View view){
    	Log.e("SIGNALR", "startService");
    	startService(new Intent("SERVICO_CONEXAO"));
    }
    
    public void onServiceConnected(ComponentName className, IBinder service) {
        // Because we have bound to an explicit
        // service that is running in our own process, we can
        // cast its IBinder to a concrete class and directly access it.
    	SignalRService.LocalBinder binder = (SignalRService.LocalBinder) service;
        mService = binder.getService();
        mBound = true;
        Log.e("SIGNALR", "onServiceConnected");
    }

    // Called when the connection with the service disconnects unexpectedly
    public void onServiceDisconnected(ComponentName className) {
        Log.e("SIGNALR", "onServiceDisconnected");
        mBound = false;
    }*/
	
	@Override
    public void onStart() {
       super.onStart();
       active = true;
       
       atualizarDadosFila();       
//       bindService(new Intent(contextPrincipalActivity, SignalRService.class), mConnection, Context.BIND_AUTO_CREATE);
    }
	
	@Override
	protected void onPause() {
		super.onPause();
		active = false;
	}
	
	@Override
    public void onStop() {
       super.onStop();
       active = false;
    }
	
	/*@Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }*/
	
	/*private ServiceConnection mConnection = new ServiceConnection() {
	    // Called when the connection with the service is established
	    public void onServiceConnected(ComponentName className, IBinder service) {
	        // Because we have bound to an explicit
	        // service that is running in our own process, we can
	        // cast its IBinder to a concrete class and directly access it.
	    	SignalRService.LocalBinder binder = (SignalRService.LocalBinder) service;
	        mService = binder.getService();
	        mBound = true;
	        Log.e("SIGNALR", "onServiceConnected");
	    }

	    // Called when the connection with the service disconnects unexpectedly
	    public void onServiceDisconnected(ComponentName className) {
	        Log.e("SIGNALR", "onServiceDisconnected");
	        mBound = false;
	    }
	};*/

	//Options app
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }
    
    public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case R.id.action_exit:
        	PrincipalActivity.this.finish();        
            return true;
        case R.id.action_logout:
        	createLogoutDialog();
        	return true;
        case R.id.action_atualizar_dados:
        	createAtualizarDadosDialog();
        	return true;
        case R.id.action_link_video:
        	openUrlVideo();
        	return true;
        case R.id.action_settings:
        	Intent intent = new Intent(contextPrincipalActivity, SettingsActivity.class);
			startActivity(intent);
        	return true;        
        case R.id.action_sobre:
        	sobre();
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
	public void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_FILA){
        	if (resultCode == RESULT_OK) {
        		String contents = data.getStringExtra("SCAN_RESULT");
				sincronizarFila("Sincronizando Fila!", "Enviando solicita��o de Sa�da da Fila, favor aguardar",
						"1", "Sa�da", contents, "");
        	}
        } else if (requestCode == REQUEST_CODE_INFORMAR_DOCA){
        	if (resultCode == RESULT_OK) {
        		String contents = data.getStringExtra("SCAN_RESULT");
				sincronizarFila("Sincronizando Fila!", "Enviando informa��o da Doca, favor aguardar",
						"0", "InformarDoca", contents, "");
        	}
        }
	}
    
    public static void sincronizarFila(String title, String message, String tipoFilaCarregamento, String tipoOperacaoCarregamento,
    		String paramSaidaFila, String lojaProximidade){
    	//tipoFilaCarregamento; //Reversa = 1,    Vazio = 2, Default = 0
    	//tipoOperacaoCarregamento; //Entrada ou Sa�da da Fila
    	//lojaProximidade; //1 - Sim ou 0- n�o
    	
    	String latitudeLocalizacao = "";
		String longitudeLocalizacao = "";
    	
    	if (tipoOperacaoCarregamento.equals("Entrada")){
	    	GPSTracker gps = new GPSTracker(contextPrincipalActivity);//Busca localiza��o
	    	
	    	if(gps.canGetLocation()){
	    		latitudeLocalizacao = Double.toString(gps.getLatitude());
				longitudeLocalizacao = Double.toString(gps.getLongitude());
				
				if (gps.getLatitude() != 0 && gps.getLongitude() != 0){
			    	alertDialog = showDialogSincronismoPadrao(title, message);
					
					SincronizarFilaCarregamento sinc = new SincronizarFilaCarregamento(alertDialog, db, contextPrincipalActivity, usuarioLogado);
					sinc.execute(tipoFilaCarregamento, tipoOperacaoCarregamento, paramSaidaFila, lojaProximidade, latitudeLocalizacao, longitudeLocalizacao);
				} else
					Toast.makeText(contextPrincipalActivity, "N�o foi poss�vel obter a localiza��o! Favor aguardar e tentar novamente.", Toast.LENGTH_SHORT).show();
			} else
				gps.showSettingsAlert();
    	} else{ //Demais
    		alertDialog = showDialogSincronismoPadrao(title, message);
    		
    		SincronizarFilaCarregamento sinc = new SincronizarFilaCarregamento(alertDialog, db, contextPrincipalActivity, usuarioLogado);
			sinc.execute(tipoFilaCarregamento, tipoOperacaoCarregamento, paramSaidaFila, lojaProximidade, latitudeLocalizacao, longitudeLocalizacao);
    	}
    }
    
    @SuppressLint("InflateParams")
	public static void createSincronizarFilaDialog(){
    	if (usuarioSelecionado.getStatus_fila() == EnumStatusFila.Disponivel.getValue()){
    		
    		AlertDialog.Builder builder = new AlertDialog.Builder(contextPrincipalActivity, R.style.DialogTheme);
    		builder.setTitle("Entrada na Fila")
            		.setMessage("Voc� est� realizando reversa?")
                   .setCancelable(false)
                   .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                    	   
                    	   AlertDialog.Builder builder = new AlertDialog.Builder(contextPrincipalActivity, R.style.DialogTheme);
                   		   builder.setTitle("Realizando Reversa")
                           		  .setMessage("Voc� est� com retorno de equipamentos de loja de proximidade?")
                                  .setCancelable(false)
                                  .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                      public void onClick(DialogInterface dialog, int id) {
                                    	
              							sincronizarFila("Sincronizando Fila!", "Enviando solicita��o de Entrada na Fila, favor aguardar",
              									"1", "Entrada", "", "1");
                                      }
                                  })
                                  .setNegativeButton("N�o", new DialogInterface.OnClickListener() {
                                      public void onClick(DialogInterface dialog, int id) {
                                    	  
              							sincronizarFila("Sincronizando Fila!", "Enviando solicita��o de Entrada na Fila, favor aguardar",
              									"1", "Entrada", "", "0");
                                      }
                                  });
                   		   showAlertDialogTwoButtonPadrao(builder);                           
                       }
                   })
                   .setNegativeButton("N�o", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                    	   
                    	   sincronizarFila("Sincronizando Fila!", "Enviando solicita��o de Entrada na Fila, favor aguardar",
									"2", "Entrada", "", "");
                       }
                   })
                   .setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                    	   dialog.cancel();
                       }
                   });
    		showAlertDialogTwoButtonPadrao(builder);
            
    	} else if (usuarioSelecionado.getStatus_fila() == EnumStatusFila.NaFila.getValue() || 
    			usuarioSelecionado.getStatus_fila() == EnumStatusFila.CargaRecusada.getValue()){

			List<String> motivos;
			motivos = Funcoes.retornaMotivoSaidaFila(db);
			if (motivos.size() <= 0)
				Toast.makeText(contextPrincipalActivity, "N�o h� motivos de sa�da de fila cadastrados pelo Embarcador! Favor Sincronizar ou verificar com o mesmo.", Toast.LENGTH_LONG).show();
			else {
			    LayoutInflater inflater = ((Activity) contextPrincipalActivity).getLayoutInflater();
			    View rootView = inflater.inflate(R.layout.alert_dialog_saida_fila, null);
			    spnMotivoSaidaFila = (Spinner) rootView.findViewById(R.id.id_spn_motivo_alert_saida_fila);
			   
			    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(contextPrincipalActivity, R.layout.spinner_item, motivos);
		   		ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
		   		spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
		   		spnMotivoSaidaFila.setAdapter(spinnerArrayAdapter);
		   		spnMotivoSaidaFila.setSelection(0);
			   
			    AlertDialog.Builder builderData = new AlertDialog.Builder(contextPrincipalActivity, R.style.DialogTheme);
			    builderData.setView(rootView)
				   .setTitle("Confirma��o de Sa�da da Fila")
				   .setCancelable(false)
				   .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
				       @Override
				       public void onClick(DialogInterface dialog, int id) {
				    	   int codigoMotivo = Funcoes.retornarCodigoString(spnMotivoSaidaFila.getSelectedItem().toString());
				    	   if (codigoMotivo <= 0)
				    		   Toast.makeText(contextPrincipalActivity, "N�o foi selecionado um motivo!", Toast.LENGTH_LONG).show();
				    	   else{
								sincronizarFila("Sincronizando Fila!", "Enviando solicita��o de Sa�da da Fila, favor aguardar",
										"2", "Sa�da", String.valueOf(codigoMotivo), "");
	                 	 	}
				       }
				   })
				   .setNegativeButton("N�o", new DialogInterface.OnClickListener() {
				       public void onClick(DialogInterface dialog, int id) {
				    	   dialog.cancel();
				       }
				   });
			    showAlertDialogTwoButtonPadrao(builderData);
			}
    		
    	} else if (usuarioSelecionado.getStatus_fila() == EnumStatusFila.EmReversa.getValue()){
    		
    		AlertDialog.Builder builder = new AlertDialog.Builder(contextPrincipalActivity, R.style.DialogTheme);
    		builder.setTitle("Sair da Reversa")
	               .setMessage("Voc� realmente deseja sair da reversa?")
	               .setCancelable(false)
	               .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
							try {
	    			            Intent intent = new Intent(ACTION_SCAN);
	    			            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
	    			            ((Activity) contextPrincipalActivity).startActivityForResult(intent, REQUEST_CODE_FILA);
	    			        } catch (ActivityNotFoundException anfe) {
	    			            showDialog((Activity) contextPrincipalActivity, "Nenhum scanner encontrado", "Deseja baixar?", "Sim", "N�o").show();
	    			        }
	                   }
	               })
	               .setNegativeButton("N�o", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                        dialog.cancel();
	                   }
	               });
    		showAlertDialogTwoButtonPadrao(builder);
    	} else if (usuarioSelecionado.getStatus_fila() == EnumStatusFila.EmViagem.getValue()){
    		
    		AlertDialog.Builder builder = new AlertDialog.Builder(contextPrincipalActivity, R.style.DialogTheme);
    		builder.setTitle("Informar Doca")
	               .setMessage("Voc� realmente deseja informar a Doca?")
	               .setCancelable(false)
	               .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
							try {
	    			            Intent intent = new Intent(ACTION_SCAN);
	    			            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
	    			            ((Activity) contextPrincipalActivity).startActivityForResult(intent, REQUEST_CODE_INFORMAR_DOCA);
	    			        } catch (ActivityNotFoundException anfe) {
	    			            showDialog((Activity) contextPrincipalActivity, "Nenhum scanner encontrado", "Deseja baixar?", "Sim", "N�o").show();
	    			        }
	                   }
	               })
	               .setNegativeButton("N�o", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                        dialog.cancel();
	                   }
	               });
    		showAlertDialogTwoButtonPadrao(builder);
    	}
	}
    
    public static void createSincronizarObterDadosFilaDialog(){
    	sincronizarFila("Sincronizando Fila!", "Enviando solicita��o de consulta de dados da Fila, favor aguardar",
				"0", "NaFila", "", "");
    }
    
	@SuppressWarnings("deprecation")
	public static void createSincronizarDetalhesCargaFilaDialog(){
		if (usuarioSelecionado.getStatus_fila() == EnumStatusFila.NaFilaAgCarga.getValue()){
	    	((Activity) contextPrincipalActivity).showDialog(DIALOG_CARREGAR_LISTA_PROGRESS);

			SincronizarDetalhesCargaFila sinc = new SincronizarDetalhesCargaFila(db, contextPrincipalActivity, usuarioLogado);
	    	sinc.execute();
		}
	}
    
    final private void createAtualizarDadosDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(contextPrincipalActivity, R.style.DialogTheme);
        builder.setMessage("Voc� realmente deseja atualizar seus dados?")
               .setCancelable(false)
               .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   alertDialog = showDialogSincronismoPadrao("Atualizar Dados", "Atualizando seus dados, favor aguardar!");
               		
	               		SincronizarAtualizarDados sinc = new SincronizarAtualizarDados(alertDialog, db, contextPrincipalActivity, usuarioLogado);
	           			sinc.execute();
                   }
               })
               .setNegativeButton("N�o", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                   }
               });
        showAlertDialogTwoButtonPadrao(builder);
	}
    
    public static void openVisualizarDadosCargaOnlineActivity(){
    	Intent intent = new Intent(contextPrincipalActivity, VisualizarDadosCargaOnlineActivity.class);
		contextPrincipalActivity.startActivity(intent);
    }
    
    public static void createSincronizarChegadaDialog(){
    	listDestinosCargaOnline = new ArrayList<DestinoCargaOnlineDTO>();
    	
    	SincronizarDadosCargaDestinoOnline mAuthTask = new SincronizarDadosCargaDestinoOnline(db, contextPrincipalActivity, usuarioLogado);
		mAuthTask.execute();
    }
    public static void openInformarSincronizarChegadaDialog(){
    	if (listDestinosCargaOnline.size() == 0)
			Toast.makeText(contextPrincipalActivity, "Nenhum pedido existente para a carga!", Toast.LENGTH_LONG).show();
    	else {
		    LayoutInflater inflater = ((Activity) contextPrincipalActivity).getLayoutInflater();
		    View rootView = inflater.inflate(R.layout.alert_dialog_selecao_informacao, null);
		    spnDestinosCarga = (Spinner) rootView.findViewById(R.id.id_spn_informacao_alert_selecao_informacao);
		    TextView txtPerguntaDestinosCarga = (TextView) rootView.findViewById(R.id.id_lbl_pergunta_alert_selecao_informacao);
		    TextView txtDestinosCarga = (TextView) rootView.findViewById(R.id.id_lbl_descricao_alert_selecao_informacao);
		    txtPerguntaDestinosCarga.setText(" Voc� realmente chegou no destino?");
		    txtDestinosCarga.setText(" *Destinos:");
		   
		    ArrayAdapter<DestinoCargaOnlineDTO> arrayAdapter = new ArrayAdapter<DestinoCargaOnlineDTO>(contextPrincipalActivity, R.layout.spinner_item, listDestinosCargaOnline);
	   		ArrayAdapter<DestinoCargaOnlineDTO> spinnerArrayAdapter = arrayAdapter;
	   		spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
	   		spnDestinosCarga.setAdapter(spinnerArrayAdapter);
	   		spnDestinosCarga.setSelection(0);
		   
		    AlertDialog.Builder builderData = new AlertDialog.Builder(contextPrincipalActivity, R.style.DialogTheme);
		    builderData.setView(rootView)
			   .setTitle("Confirma��o de Chegada no Destino")
			   .setCancelable(false)
			   .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
			       @Override
			       public void onClick(DialogInterface dialog, int id) {
			    	   GPSTracker gps = new GPSTracker(contextPrincipalActivity);//Busca localiza��o
				    	
				    	if(gps.canGetLocation()){
							if (gps.getLatitude() != 0 && gps.getLongitude() != 0){
								DestinoCargaOnlineDTO destino = (DestinoCargaOnlineDTO) spnDestinosCarga.getSelectedItem();
								
								SincronizarChegadaSaida sinc = new SincronizarChegadaSaida(db, contextPrincipalActivity, usuarioLogado);        //Se � sa�da da Ocorrencia Glog
								sinc.execute(destino.getId_commerce(), Double.toString(gps.getLatitude()), Double.toString(gps.getLongitude()), String.valueOf(false));
							} else
								Toast.makeText(contextPrincipalActivity, "N�o foi poss�vel obter a localiza��o! Favor aguardar e tentar novamente.", Toast.LENGTH_SHORT).show();
						} else
							gps.showSettingsAlert();
			       }
			   })
			   .setNegativeButton("N�o", new DialogInterface.OnClickListener() {
			       public void onClick(DialogInterface dialog, int id) {
			    	   dialog.cancel();
			       }
			   });
		    showAlertDialogTwoButtonPadrao(builderData);
		}
    }
    
    //Configura��es padr�es tela
    final private void createLogoutDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(contextPrincipalActivity, R.style.DialogTheme);
        builder.setMessage("Voc� realmente deseja deslogar?")
               .setCancelable(false)
               .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   if (usuarioSelecionado.isManter_logado()){
	                	   usuarioSelecionado.setManter_logado(false);
	                	   try {
								usuarioSelecionado.save();
//								sair = true;
//			                    PrincipalActivity.this.finish();
							} catch (ActiveRecordException are) {
								are.printStackTrace();
								Toast.makeText(contextPrincipalActivity, "Problemas ao deslogar o usu�rio!", Toast.LENGTH_LONG).show();
							}
	                   }
//                	   else {
//	                	   sair = true ;
//		                   PrincipalActivity.this.finish();
//	                   }
                	   
                	   SincronizarDeslogarUsuario mAuthTask = new SincronizarDeslogarUsuario(db, contextPrincipalActivity, usuarioLogado);
                	   mAuthTask.execute();
                   }
               })
               .setNegativeButton("N�o", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                   }
               });
        showAlertDialogTwoButtonPadrao(builder);
	}
    
    @Override
	protected Dialog onCreateDialog(int id) {
	    switch (id) {
		    case DIALOG_CARREGAR_LISTA_PROGRESS:
		    	mProgressCarregarLista = new ProgressDialog(contextPrincipalActivity);
		    	mProgressCarregarLista.setTitle("Carregando...");
	        	mProgressCarregarLista.setMessage("Por favor aguarde at� o fim...");
		    	mProgressCarregarLista.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		    	mProgressCarregarLista.setCancelable(false);
		    	mProgressCarregarLista.show();
		        return mProgressCarregarLista;
		    default:
		    return null;
	    }
	}
    
    private void openUrlVideo(){
    	String url = usuarioSelecionado.getLink_video();
    	try {
	    	if (!Funcoes.isNullOrEmpty(url)){
		    	Intent intentVideo = new Intent(Intent.ACTION_VIEW);
		    	intentVideo.setData(Uri.parse(url));
				startActivity(intentVideo);
	    	} else
	    		Dialogs.showAlertDialogOneButtonPadrao(contextPrincipalActivity, "V�deo", "V�deo n�o dispon�vel, tente atualizar os dados!");
	    } catch (Exception e) {
	    	Toast.makeText(contextPrincipalActivity, e.getMessage(), Toast.LENGTH_LONG).show();
	    }
    }
    
    @Override
	public void finish() {
    	if (limparDados){
    		String limparDados = Funcoes.LimparDadosAplicativo(db);
			if (!Funcoes.isNullOrEmpty(limparDados)){
				Toast.makeText(contextPrincipalActivity, limparDados, Toast.LENGTH_LONG).show();
				return;
			}
    	}
		if (sair){
            super.finish();
            return;
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(contextPrincipalActivity, R.style.DialogTheme);
        builder.setMessage("Voc� realmente deseja sair?")
               .setCancelable(false)
               .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {                        
                       sair = true;
                       PrincipalActivity.this.finish();
                   }
               })
               .setNegativeButton("N�o", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                   }
               });
        showAlertDialogTwoButtonPadrao(builder);
	}
    
    private void sobre() {        
        showAlertDialogOneButtonPadrao("Sobre", 
        		"Vers�o: " + contextPrincipalActivity.getString(R.string.app_version) +
        		"\nAmbiente: " + contextPrincipalActivity.getString(R.string.app_ambiente) +
        		"\nDevelopment by Multisoftware",
        		contextPrincipalActivity);
	}
    
    @SuppressWarnings("deprecation")
	final public static void limparProgress() {
    	if (mProgressCarregarLista != null)
    		((Activity) contextPrincipalActivity).removeDialog(DIALOG_CARREGAR_LISTA_PROGRESS);
    }

	final public static void atualizarDadosFila() {
    	lblPlacaFila.setText(Html.fromHtml("<b>Placa: </b>" + usuarioSelecionado.getPlaca()));
		lblMotoristaFila.setText(Html.fromHtml("<b>Motorista: </b>" + usuarioSelecionado.getNome_funcionario()));
		lblTransportadoraFila.setText(Html.fromHtml("<b>Transportadora: </b>" + usuarioSelecionado.getTransportadora()));
		lblStatusFila.setText(Html.fromHtml("<b>Status Atual: </b>" + EnumStatusFila.DescricaoStatusFila(usuarioSelecionado.getStatus_fila())));
		lblPosicaoFila.setBackgroundResource(R.color.vermelho);
		
		if (usuarioSelecionado.getStatus_fila() == EnumStatusFila.NaFilaAgCarga.getValue())
			lblPosicaoFila.setText("Voc� foi alocado para uma carga!\nPara confirmar CLIQUE AQUI");
		else if (usuarioSelecionado.getStatus_fila() == EnumStatusFila.EmViagem.getValue()){
			lblPosicaoFila.setText("CD: " + usuarioSelecionado.getLocal_fila() + "\nPara confirmar a doca CLIQUE ABAIXO");
			lblPosicaoFila.setBackgroundResource(R.color.verde);
		}
		else if (usuarioSelecionado.getStatus_fila() == EnumStatusFila.CargaRecusada.getValue())
			lblPosicaoFila.setText("Voc� est� na fila da CD: " + usuarioSelecionado.getLocal_fila() + 
					" na posi��o: " + usuarioSelecionado.getPosicao_fila() + " (Entre em contato com a CD!)");
		else
			lblPosicaoFila.setText("Voc� est� na fila da CD: " + usuarioSelecionado.getLocal_fila() + " na posi��o: " + usuarioSelecionado.getPosicao_fila());
		
		btnEntradaFila.setVisibility(View.GONE);
		btnSaidaFila.setVisibility(View.GONE);
		btnAtualizarDadosFila.setVisibility(View.VISIBLE);
		lblPosicaoFila.setVisibility(View.VISIBLE);
		btnInformarDocaFila.setVisibility(View.GONE);
		btnQuantidadeCargas.setVisibility(View.VISIBLE);
		btnDadosCargaFila.setVisibility(View.GONE);
		btnOcorrenciaGLog.setVisibility(View.GONE);
		btnInformarChegada.setVisibility(View.GONE);
		
		if (usuarioSelecionado.getStatus_fila() == EnumStatusFila.Disponivel.getValue()){
			lblPosicaoFila.setVisibility(View.GONE);
			btnEntradaFila.setVisibility(View.VISIBLE);
			btnAtualizarDadosFila.setVisibility(View.GONE);
		} else if (usuarioSelecionado.getStatus_fila() == EnumStatusFila.NaFilaAgCarga.getValue())
			lblPosicaoFila.setVisibility(View.VISIBLE);
		else if	(usuarioSelecionado.getStatus_fila() == EnumStatusFila.EmViagem.getValue()){
			lblPosicaoFila.setVisibility(View.VISIBLE);
			btnInformarDocaFila.setVisibility(View.VISIBLE);
			btnDadosCargaFila.setVisibility(View.VISIBLE);
			btnQuantidadeCargas.setVisibility(View.GONE);
			btnOcorrenciaGLog.setVisibility(View.VISIBLE);
			btnInformarChegada.setVisibility(View.VISIBLE);
		} else if (usuarioSelecionado.getStatus_fila() == EnumStatusFila.AgRemocao.getValue()){

		} else
			btnSaidaFila.setVisibility(View.VISIBLE);
		
		Drawable top;
		if (usuarioSelecionado.getStatus_fila() == EnumStatusFila.EmReversa.getValue())
			top = contextPrincipalActivity.getResources().getDrawable(R.drawable.ic_button_sair_reversa);
		else
			top = contextPrincipalActivity.getResources().getDrawable(R.drawable.ic_button_sair_fila);
		btnSaidaFila.setCompoundDrawablesWithIntrinsicBounds(null, top , null, null);
		
//		CarregarListasNotificacaoFilaTask mAuthTaskFluxo = new CarregarListasNotificacaoFilaTask(contextPrincipalActivity);
//		mAuthTaskFluxo.execute();
		limparProgress();
	}

    /**
     * Fragment that appears in the "content_frame", shows a planet
     */
    public static class PlanetFragment extends Fragment {
    	
        public PlanetFragment() {
        	
        }

		@Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        	View rootView = null;
    	
    		rootView = inflater.inflate(R.layout.fragment_fila, container, false);
//    		lvNotificacaoFila = (ListView) rootView.findViewById(R.id.id_list_view_notificacao_fila);
    		lblPlacaFila = (TextView) rootView.findViewById(R.id.id_lbl_placa_fila);
    		lblMotoristaFila = (TextView) rootView.findViewById(R.id.id_lbl_motorista_fila);
    		lblTransportadoraFila = (TextView) rootView.findViewById(R.id.id_lbl_transportadora_fila);
    		lblStatusFila = (TextView) rootView.findViewById(R.id.id_lbl_status_fila);
    		lblPosicaoFila = (TextView) rootView.findViewById(R.id.id_lbl_posicao_fila);
    		
    		btnEntradaFila = (Button) rootView.findViewById(R.id.id_btn_entrada_fila);
    		btnSaidaFila = (Button) rootView.findViewById(R.id.id_btn_saida_fila);
    		btnAtualizarDadosFila = (Button) rootView.findViewById(R.id.id_btn_atualizar_dados_fila);
    		btnQuantidadeCargas = (Button) rootView.findViewById(R.id.id_btn_qtd_cargas_fila);
    		btnInformarDocaFila = (Button) rootView.findViewById(R.id.id_btn_informar_doca_fila);
    		btnDadosCargaFila = (Button) rootView.findViewById(R.id.id_btn_dados_carga_fila);
    		Button btnNotificacaoOnlineFile = (Button) rootView.findViewById(R.id.id_btn_notificacao_online_fila);
    		btnOcorrenciaGLog = (Button) rootView.findViewById(R.id.id_btn_ocorrencia_glog_online_fila);
    		btnInformarChegada = (Button) rootView.findViewById(R.id.id_btn_informar_chegada_fila);
    		
    		btnEntradaFila.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					createSincronizarFilaDialog();
				}
			});
    		
    		btnQuantidadeCargas.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(contextPrincipalActivity, VisualizarCargaFilaActivity.class);
					startActivity(intent);
				}
			});
    		
    		btnSaidaFila.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					createSincronizarFilaDialog();
				}
			});
    		
    		btnAtualizarDadosFila.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					createSincronizarObterDadosFilaDialog();
				}
			});

    		lblPosicaoFila.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					createSincronizarDetalhesCargaFilaDialog();
				}
			});
    		
    		btnInformarDocaFila.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					createSincronizarFilaDialog();
				}
			});
    		
    		btnNotificacaoOnlineFile.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(contextPrincipalActivity, VisualizarNotificacaoOnlineActivity.class);
					Bundle params = new Bundle();
					params.putString("usuarioLogado", usuarioLogado);
					intent.putExtras(params);
					startActivity(intent);
				}
			});
    		
    		btnDadosCargaFila.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					listRelatorioDadosCargaOnline = new ArrayList<DadosCargaOnlineDTO>();
					SincronizarDadosCargaOnline mAuthTask = new SincronizarDadosCargaOnline(db, contextPrincipalActivity, usuarioLogado);
					mAuthTask.execute();
				}
			});
    		
    		btnOcorrenciaGLog.setOnClickListener(new OnClickListener() {
    			@Override
				public void onClick(View v) {
					Intent intent = new Intent(contextPrincipalActivity, VisualizarOcorrenciaGlogOnlineActivity.class);
					Bundle params = new Bundle();
					params.putString("usuarioLogado", usuarioLogado);
					intent.putExtras(params);
					startActivity(intent);
				}
			});
    		
    		btnInformarChegada.setOnClickListener(new OnClickListener() {
    			@Override
				public void onClick(View v) {
    				createSincronizarChegadaDialog();
				}
			});
    		
    		atualizarDadosFila();
    		if (notificacaoTipoMensagem.equals("true"))
    			btnNotificacaoOnlineFile.callOnClick();
            return rootView;
        }
    }
    
//    public static class NotificacaoFilaAdapter extends ArrayAdapter<NotificacaoFila>{
//		
//		LayoutInflater inflater;
//
//		public NotificacaoFilaAdapter(Context context, int textViewResourceId,
//				List<NotificacaoFila> objects) {
//			super(context, textViewResourceId, objects);
//			inflater = LayoutInflater.from(context);
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			View v = convertView;
//			
//			if (v == null)
//				v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
//			
//			NotificacaoFila g = getItem(position);
//			TextView txt1 = (TextView) v.findViewById(android.R.id.text1);
//			txt1.setTextColor(Color.parseColor("#000000"));
//			
//			String msgText1 = "";
//			msgText1 = g.getDescricao();
//			txt1.setText(Html.fromHtml(msgText1));
//							
//			return v;
//		}
//	}
    
//    public static class CarregarListasNotificacaoFilaTask extends AsyncTask<String, String, String> {
//    	
//    	public CarregarListasNotificacaoFilaTask(Context context) {
//    	}
//    	
//		@Override
//		protected String doInBackground(String... params) {
//			listNotificacoesFila = Funcoes.retornListaNotificacaoFila(db);
//			
//			return "Sucesso";
//		}
//
//		@Override
//		protected void onPostExecute(String result) {
//			if (listNotificacoesFila != null && lvNotificacaoFila != null){
//				ArrayList<NotificacaoFila> array = (ArrayList<NotificacaoFila>) listNotificacoesFila;
//				
//				if(array != null)
//					lvNotificacaoFila.setAdapter(new NotificacaoFilaAdapter(contextPrincipalActivity, 0, array));
//			}
//			
//			mProgressCarregarLista.dismiss();
//		}
//	}
    
    public static void showAlertDialogTwoButtonPadrao(AlertDialog.Builder builder) {
    	AlertDialog alertDialog = builder.create();
    	alertDialog.show();
        
        Button buttonPositive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        buttonPositive.setText(Html.fromHtml("<b>SIM</b>"));
        buttonPositive.setTextColor(contextPrincipalActivity.getResources().getColor(R.color.branco));
        buttonPositive.setBackgroundColor(contextPrincipalActivity.getResources().getColor(R.color.verde));
        Button buttonNegative = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        buttonNegative.setText(Html.fromHtml("<b>N�O</b>"));
        buttonNegative.setTextColor(contextPrincipalActivity.getResources().getColor(R.color.branco));
        buttonNegative.setBackgroundColor(contextPrincipalActivity.getResources().getColor(R.color.vermelho));
        
        Button buttonNeutral = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
        buttonNeutral.setText(Html.fromHtml("<b>CANCELAR</b>"));
    }
    
    public static void showAlertDialogTwoButtonPadrao(AlertDialog alertDialog) {
    	alertDialog.show();
        
        Button buttonPositive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        buttonPositive.setText(Html.fromHtml("<b>SIM</b>"));
        buttonPositive.setTextColor(contextPrincipalActivity.getResources().getColor(R.color.branco));
        buttonPositive.setBackgroundColor(contextPrincipalActivity.getResources().getColor(R.color.verde));
        Button buttonNegative = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        buttonNegative.setText(Html.fromHtml("<b>N�O</b>"));
        buttonNegative.setTextColor(contextPrincipalActivity.getResources().getColor(R.color.branco));
        buttonNegative.setBackgroundColor(contextPrincipalActivity.getResources().getColor(R.color.vermelho));
    }
    
    public static AlertDialog showAlertDialogOneButtonPadrao(CharSequence title, CharSequence message, Context context) {
    	AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		alertDialog.setCancelable(false);
		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
		new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
		
		    }
		});
		
		alertDialog.show();
		return alertDialog;
    }
    
    public static AlertDialog showDialogSincronismoPadrao(CharSequence title, CharSequence message) {
    	AlertDialog alertDialog = new AlertDialog.Builder(contextPrincipalActivity).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		alertDialog.setCancelable(false);
		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
		new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
		
		    }
		});
		
		alertDialog.show();
		final AlertDialog dialogEntrada = (AlertDialog)alertDialog;
		dialogEntrada.getButton(DialogInterface.BUTTON_NEUTRAL).setEnabled(false);

		return alertDialog;
    }

    //alert dialog for downloadDialog
    public static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setCancelable(false);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
//                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
            	Uri uri = Uri.parse("market://search?q=pname:" + "com.srowen.bs.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }
}
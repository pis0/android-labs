package br.inf.commerce.multimobilegpa.android;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.kroz.activerecord.ActiveRecordBase;
import org.kroz.activerecord.ActiveRecordException;
import org.kroz.activerecord.EntitiesHelper;

import curso.utils.Networking;

import br.inf.commerce.multimobilegpa.android.R;

import MultiMobile.DAO.ClienteMultisoftware;
import MultiMobile.DAO.MotivoSaidaFila;
import MultiMobile.DAO.Usuario;
import MultiMobile.Utils.Funcoes;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {
	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;
	ActiveRecordBase db;
	private String usuarioLogado;
	private static Context contextLoginActivity;

	// Values for email and password at the time of the login attempt.
	private String mUsuario;
	private boolean mManterLogado;
	private String notificacaoTipoMensagem = "false";

	// UI references.
	private EditText mUsuarioView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	private CheckBox mManterLogadoView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		contextLoginActivity = LoginActivity.this;
		db = ((CommerceMobileApp)getApplication()).getDatabase();

		// Set up the login form.
		mUsuario = "";
		mUsuarioView = (EditText) findViewById(R.id.usuario);		
		mManterLogadoView = (CheckBox) findViewById(R.id.checkbox_manter_logado);

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
		
		ActionBar bar = getActionBar();
		if(bar != null) bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ED7D31")));
		
		Intent intent = getIntent();
		Bundle params = intent.getExtras();
		notificacaoTipoMensagem = params.getString("NotificacaoTipoMensagem");

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
		
		List<Usuario> listUsuarios;
		try {
			listUsuarios = db.findAll(Usuario.class);
		
			for (int i = 0; i < listUsuarios.size(); i++) {
				if (listUsuarios.get(i).manter_logado == true)
				{
					usuarioLogado = listUsuarios.get(i).usuario;
					Intent intentPrincipalActivity = new Intent(LoginActivity.this, PrincipalActivity.class);
					
					Bundle paramsPrincipalActivity = new Bundle();
					paramsPrincipalActivity.putString("usuarioLogado", usuarioLogado);
					paramsPrincipalActivity.putString("NotificacaoTipoMensagem", notificacaoTipoMensagem);
					intentPrincipalActivity.putExtras(paramsPrincipalActivity);
					startActivity(intentPrincipalActivity);
					
					finish();
				}
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}	

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mUsuarioView.setError(null);

		// Store values at the time of the login attempt.
		mUsuario = mUsuarioView.getText().toString();
		mManterLogado = mManterLogadoView.isChecked();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid email address.
		if (TextUtils.isEmpty(mUsuario)) {
			mUsuarioView.setError(getString(R.string.error_field_required));
			focusView = mUsuarioView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	
	public class UserLoginTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			List<Usuario> listUsuarios;
			Usuario usuarioSelecionado = null;
			
			try {
				//Verifica se o usuário já está salvo
				listUsuarios = db.findAll(Usuario.class);
				for (int i = 0; i < listUsuarios.size(); i++) {
					Usuario usuarioListado = listUsuarios.get(i);
					if (usuarioListado.getUsuario().equals(mUsuario))
					{
						if (mManterLogado){
							usuarioSelecionado = usuarioListado;
							usuarioSelecionado.setManter_logado(mManterLogado);
							usuarioSelecionado.save();
						}
						usuarioLogado = usuarioListado.getUsuario();
						return "Sucesso";
					}
				}
				
				if (usuarioLogado == null){
					if (!Funcoes.verificaConexao(getApplicationContext())){
						return "Favor verificar a sua conex�o com a Internet!";
					}
					
					String urlWS = contextLoginActivity.getString(R.string.url_ws);
					String urlEnvio = urlWS + "Autenticacao.svc/AutenticarUsuario/";
					String token = contextLoginActivity.getString(R.string.chave_ws);
					String imeiDevice = Funcoes.getDeviceIMEI(contextLoginActivity);
					String resposta = null;
					
					if (Funcoes.isNullOrEmpty(imeiDevice))
						return "N�o foi poss�vel obter a identifica��o do aparelho!";
					
					String ws = urlEnvio + token + "/" + mUsuario + "/" + imeiDevice;
	
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
						else if (!Funcoes.validaVersaoAplicativo(contextLoginActivity, usuarioJson.getString("VersaoAplicativoMobile")))
							return Funcoes.mensagemVersaoAplicativo(usuarioJson.getString("VersaoAplicativoMobile"));
						else
							usuarioJson = new JSONObject(usuarioJson.getString("Objeto"));
					} catch (Throwable t) {
						return "Problemas ao converter resposta do web service.";
					}
					
					String limparDados = Funcoes.LimparDadosAplicativo(db);//ADICIONAR AS NOVAS TABELAS CRIADAS NESSA FUN��O
					if (!Funcoes.isNullOrEmpty(limparDados))
						return limparDados;
					
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
					ws = getString(R.string.url_ws)+"FilaCarregamento.svc/ObterMotivosRetiradaFilaCarregamento";
					
					Map<String,String> paramPost = new HashMap<String,String>();
					ClienteMultisoftware clienteMultisoftware = null;
					clienteMultisoftware = Funcoes.retornaClienteMultisoftware(db);
					if (clienteMultisoftware != null){
						paramPost.put("token", getString(R.string.chave_ws));
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
					
					//Salva o usu�rio
					Usuario usuario = new Usuario(codigoUsuario, mUsuario, imeiDevice, mManterLogado, nomeUsuario, placa, 1, 0, "", transportadora, 
							urlEmbarcador, linkVideoMobile);
					
					try {
						Usuario usuariodb;
						usuariodb = db.newEntity(Usuario.class);
						EntitiesHelper.copyFieldsWithoutID(usuariodb, usuario);
						usuariodb.save();
						usuarioLogado = mUsuario;
						return "Sucesso";
					} catch (ActiveRecordException e) {
						e.printStackTrace(); 
						return "Problemas ao inserir o usu�rio."; 
					}
				}
			} catch (ActiveRecordException e) {
				e.printStackTrace();
				return "Falha! Verifique a vers�o do sistema.";
			}

			return "Usu�rio ou Senha inv�lidos!";
		}

		@Override
		protected void onPostExecute(String result) {
			mAuthTask = null;
			showProgress(false);

			if (result.equalsIgnoreCase("Sucesso")) {
				Intent intent = new Intent(LoginActivity.this, PrincipalActivity.class);
				
				Bundle params = new Bundle();
				params.putString("usuarioLogado", usuarioLogado);
				params.putString("NotificacaoTipoMensagem", notificacaoTipoMensagem);
				intent.putExtras(params);
				startActivity(intent);
				
				finish();
			} else {
				Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}

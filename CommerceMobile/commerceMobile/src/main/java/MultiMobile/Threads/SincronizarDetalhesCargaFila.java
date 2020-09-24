package MultiMobile.Threads;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.kroz.activerecord.ActiveRecordBase;

import curso.utils.Networking;

import br.inf.commerce.multimobilegpa.android.PrincipalActivity;
import br.inf.commerce.multimobilegpa.android.R;
import MultiMobile.DAO.ClienteMultisoftware;
import MultiMobile.DAO.Usuario;
import MultiMobile.Utils.Funcoes;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

public class SincronizarDetalhesCargaFila extends
		AsyncTask<String, Integer, String> {
	
	AlertDialog alertDialog;
	private ActiveRecordBase db;
	private Context context;
	String usuarioLogado;
	String detalhesCarga = "";

	public SincronizarDetalhesCargaFila(ActiveRecordBase db, Context context, String usuarioLogado) {		
		this.db = db;
		this.context = context;
		this.usuarioLogado = usuarioLogado;
	}
	
	@Override
	protected void onPreExecute() {
		alertDialog = new AlertDialog.Builder(context, R.style.DialogTheme).create();
		alertDialog.setTitle("Confirmação da Carga");
		alertDialog.setCancelable(false);
		
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(String result) {		
		if (result.equalsIgnoreCase("ObteuResposta")) {
			alertDialog.setMessage(detalhesCarga + "\n\nDeseja confirmar?");
			alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Sim",
			new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int which) {
			    	dialog.dismiss();
			    	SincronizarConfirmacaoCarga sinc = new SincronizarConfirmacaoCarga(db, context, usuarioLogado);	                	
			    	sinc.execute("Sim");
			    }
			});
			alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Não",
			new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int which) {
			    	dialog.dismiss();
			    	SincronizarConfirmacaoCarga sinc = new SincronizarConfirmacaoCarga(db, context, usuarioLogado);	                	
			    	sinc.execute("N�o");
			    }
			});
			alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancelar",
					new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog, int which) {
					    	dialog.dismiss();
					    }
					});
			
			PrincipalActivity.showAlertDialogTwoButtonPadrao(alertDialog);
		} else
			Toast.makeText(context, result, Toast.LENGTH_LONG).show();

		PrincipalActivity.limparProgress();
		super.onPostExecute(result);
	}

	@Override
	protected String doInBackground(String... params) {

		if (Funcoes.verificaConexao(context)) {
			String urlEnvio = context.getString(R.string.url_ws)+"FilaCarregamento.svc/ObterDetalhesCarga";
			String token = context.getString(R.string.chave_ws);
			Usuario usuarioSelecionado = Funcoes.retornaUsuario(db, usuarioLogado);
			ClienteMultisoftware clienteMultisoftware = Funcoes.retornaClienteMultisoftware(db);
			
			Map<String,String> paramFila = new HashMap<String,String>();
			String resposta = null;
			
			paramFila.put("usuario", String.valueOf(usuarioSelecionado.getId_commerce()));
			paramFila.put("empresaMultisoftware", String.valueOf(clienteMultisoftware.getId_commerce()));
			paramFila.put("token", token);
			
			try {
				resposta = Networking.performPostCall(urlEnvio, paramFila);
			} catch (IOException e1) {
				e1.printStackTrace();
				return "Problemas ao conectar com o web service. 1 "+e1.getMessage();
			}
			
			if (resposta == null)
				return "Problemas ao conectar com o web service. 2 resposta null";
			
			if (resposta.isEmpty() || resposta == "")
				return "Problemas ao obter detalhes da carga.";
			
			JSONObject objectFila;
			try {
				objectFila = new JSONObject(resposta);
				objectFila = new JSONObject(objectFila.getString("ObterDetalhesCargaResult"));
				String status = objectFila.getString("Status");
				if (status == "false")
					return objectFila.getString("Mensagem");
				else {
					detalhesCarga = objectFila.getString("Objeto");
					return "ObteuResposta";
				}
			} catch (Throwable t) {
				return "Problemas ao converter resposta do web service.";
			}
		} else
			return "Não há conexão com a internet! Favor ativar a mesma para continuar.";
	}

}
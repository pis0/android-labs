package br.inf.commerce.multimobilegpa.android;

import java.util.ArrayList;
import java.util.List;

import org.kroz.activerecord.ActiveRecordBase;

import MultiMobile.DTO.NotificacaoOnlineDTO;
import MultiMobile.Threads.SincronizarDetalhesNotificacaoOnline;
import MultiMobile.Threads.SincronizarNotificacaoOnline;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class VisualizarNotificacaoOnlineActivity extends Activity {
	
	private static ActiveRecordBase db;
	private static Context contextNotificacaoOnlineActivity;
	private static ListView lvNotificacaoOnlineFila;
	private static TextView txtTotalNotificacaoOnline;
	public static ArrayList<NotificacaoOnlineDTO> listRelatorioNotificacaoOnline;
	
	private static String usuarioLogado;
	private static RadioGroup rgStatus;
    private static RadioButton rdoNaoLida, rdoTodas; 

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_notificacao_online_fila);
		contextNotificacaoOnlineActivity = VisualizarNotificacaoOnlineActivity.this;
		db = ((CommerceMobileApp) getApplication()).getDatabase();
		
		if(listRelatorioNotificacaoOnline != null)
			listRelatorioNotificacaoOnline.clear();
		
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ED7D31")));
		bar.setHomeAsUpIndicator(R.drawable.ic_button_left_arrow);
		bar.setDisplayHomeAsUpEnabled(true);
		
		Intent intent = getIntent();
		Bundle params = intent.getExtras();
		usuarioLogado = params.getString("usuarioLogado");

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override 
	public boolean onNavigateUp(){ 
	     finish(); 
	     return true; 
	}
	
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_notificacao_online_fila,
					container, false);
			
			lvNotificacaoOnlineFila = (ListView) rootView.findViewById(R.id.id_list_view_notificacao_online_fila);
			txtTotalNotificacaoOnline = (TextView) rootView.findViewById(R.id.id_lbl_total_notificacao_online_fila);
			
			rgStatus = (RadioGroup) rootView.findViewById(R.id.id_rg_btn_notificacao_online_fila);
			rdoNaoLida = (RadioButton) rootView.findViewById(R.id.id_rdo_nao_lida_notificacao_online_fila);
			rdoTodas = (RadioButton) rootView.findViewById(R.id.id_rdo_todas_notificacao_online_fila);
			
			Button btnPesquisarNotificacoesOnline = (Button) rootView.findViewById(R.id.id_btn_filtrar_notificacao_online_fila);
						
			btnPesquisarNotificacoesOnline.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					listRelatorioNotificacaoOnline = new ArrayList<NotificacaoOnlineDTO>();
					String statusNaoLida = "";
					
					int selectedId = rgStatus.getCheckedRadioButtonId();
					if(selectedId == rdoTodas.getId())
						statusNaoLida = "false";
					else
						statusNaoLida = "true";
					
					SincronizarNotificacaoOnline mAuthTask = new SincronizarNotificacaoOnline(db, contextNotificacaoOnlineActivity, usuarioLogado);
					mAuthTask.execute(statusNaoLida);
				}
			});
			
			lvNotificacaoOnlineFila.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					try {
						NotificacaoOnlineDTO notificacao = (NotificacaoOnlineDTO) parent.getItemAtPosition(position);
						if (notificacao != null){
							SincronizarDetalhesNotificacaoOnline mAuthTask = new SincronizarDetalhesNotificacaoOnline(db, contextNotificacaoOnlineActivity, usuarioLogado);
							mAuthTask.execute(String.valueOf(notificacao.id_commerce));
						}
					} catch (Exception e) {
						Toast.makeText(contextNotificacaoOnlineActivity, "Nenhuma notificação encontrada!", Toast.LENGTH_LONG).show();
					}
				}
			});

			btnPesquisarNotificacoesOnline.callOnClick();
			return rootView;
		}
	}
	
	final public static void atualizarListaCargasFila() {
		lvNotificacaoOnlineFila.setAdapter(new NotificacaoOnlineAdapter(contextNotificacaoOnlineActivity, 0, listRelatorioNotificacaoOnline));		
		txtTotalNotificacaoOnline.setText("Total: " + lvNotificacaoOnlineFila.getAdapter().getCount() + " notificações");
	}
	
	public static class NotificacaoOnlineAdapter extends ArrayAdapter<NotificacaoOnlineDTO>{
		
		LayoutInflater inflater;

		public NotificacaoOnlineAdapter(Context context, int textViewResourceId,
				List<NotificacaoOnlineDTO> objects) {	
			super(context, textViewResourceId, objects);
			inflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			
			if (v == null)
				v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
			
			NotificacaoOnlineDTO g = getItem(position);
			TextView txt1 = (TextView) v.findViewById(android.R.id.text1);
			txt1.setTextColor(Color.parseColor("#000000"));
						
			String msgText1 = g.getData() + " - " + g.getAssunto();
			txt1.setText(msgText1);

			return v;
		}
	}
	
}
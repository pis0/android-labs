package br.inf.commerce.multimobilegpa.android;

import java.util.ArrayList;
import java.util.List;

import org.kroz.activerecord.ActiveRecordBase;

import MultiMobile.DTO.AtendimentoOcorrenciasOnlineDTO;
import MultiMobile.DTO.DestinoCargaOnlineDTO;
import MultiMobile.DTO.MotivosAtendimentoOnlineDTO;
import MultiMobile.DTO.AtendimentoOnlineDTO;
import MultiMobile.Threads.SincronizarCarregarOcorrenciaGlogOnline;
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
import android.widget.TextView;
import android.widget.Toast;

public class VisualizarOcorrenciaGlogOnlineActivity extends Activity {

	private static ActiveRecordBase db;
	private static Context contextOcorrenciaGlogOnlineActivity;
	private static ListView lvDadosOcorrenciaOnlineFila;
	private static TextView txtTotalDadosOcorrenciaOnline;
	public static ArrayList<AtendimentoOnlineDTO> listOcorrenciaGlogOnline;
	public static ArrayList<MotivosAtendimentoOnlineDTO> listMotivosAtendimentoOnline;
	public static ArrayList<DestinoCargaOnlineDTO> listDestinosCargaOnline;
	public static ArrayList<AtendimentoOcorrenciasOnlineDTO> listOcorrenciasAtendimentoOnline;
	
	private static String usuarioLogado;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_lista_ocorrencia_glog_online_fila);
		contextOcorrenciaGlogOnlineActivity = VisualizarOcorrenciaGlogOnlineActivity.this;
		db = ((CommerceMobileApp) getApplication()).getDatabase();
		
		if(listOcorrenciaGlogOnline != null)
			listOcorrenciaGlogOnline.clear();
		if(listMotivosAtendimentoOnline != null)
			listMotivosAtendimentoOnline.clear();
		if(listDestinosCargaOnline != null)
			listDestinosCargaOnline.clear();
		if(listOcorrenciasAtendimentoOnline != null)
			listOcorrenciasAtendimentoOnline.clear();
		
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
	
	@Override
    public void onStart() {
       super.onStart();
       
       sincronizarListaOcorrenciasFila();
    }
	
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_lista_ocorrencia_glog_online_fila,
					container, false);
			
			lvDadosOcorrenciaOnlineFila = (ListView) rootView.findViewById(R.id.id_list_view_ocorrencia_glog_online_fila);
			txtTotalDadosOcorrenciaOnline = (TextView) rootView.findViewById(R.id.id_lbl_total_ocorrencia_glog_online_fila);
			Button btnNovaSolicitacaoOcorrencia = (Button) rootView.findViewById(R.id.id_btn_nova_solicitacao_ocorrencia_glog_online_fila);
			
			lvDadosOcorrenciaOnlineFila.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					if (listDestinosCargaOnline.size() == 0)
    					Toast.makeText(contextOcorrenciaGlogOnlineActivity, "Nenhum pedido existente para a carga!", Toast.LENGTH_LONG).show();
    				else if (listMotivosAtendimentoOnline.size() == 0)
    					Toast.makeText(contextOcorrenciaGlogOnlineActivity, "Nenhum motivo de atendimento existente!", Toast.LENGTH_LONG).show();
    				else{
						try {
							AtendimentoOnlineDTO atendimento = (AtendimentoOnlineDTO) parent.getItemAtPosition(position);
							if (atendimento != null){
								Intent intent = new Intent(contextOcorrenciaGlogOnlineActivity, OcorrenciaGlogActivity.class);
								Bundle params = new Bundle();
								params.putString("usuarioLogado", usuarioLogado);
								params.putInt("codigoAtendimento", atendimento.getId_commerce());
								intent.putExtras(params);
								startActivity(intent);
							}
						} catch (Exception e) {
							Toast.makeText(contextOcorrenciaGlogOnlineActivity, "Nenhuma notificação encontrada!", Toast.LENGTH_LONG).show();
						}
    				}
				}
			});
			
			btnNovaSolicitacaoOcorrencia.setOnClickListener(new OnClickListener() {
				
    			@Override
				public void onClick(View v) {
    				
    				if (listDestinosCargaOnline.size() == 0)
    					Toast.makeText(contextOcorrenciaGlogOnlineActivity, "Nenhum pedido existente para a carga!", Toast.LENGTH_LONG).show();
    				else if (listMotivosAtendimentoOnline.size() == 0)
    					Toast.makeText(contextOcorrenciaGlogOnlineActivity, "Nenhum motivo de atendimento existente!", Toast.LENGTH_LONG).show();
    				else{    				
						Intent intent = new Intent(contextOcorrenciaGlogOnlineActivity, OcorrenciaGlogActivity.class);
						Bundle params = new Bundle();
						params.putString("usuarioLogado", usuarioLogado);
						intent.putExtras(params);
						startActivity(intent);
    				}
				}
			});
			
			return rootView;
		}
	}
	
	final private static void sincronizarListaOcorrenciasFila() {
		listOcorrenciaGlogOnline = new ArrayList<AtendimentoOnlineDTO>();
		listMotivosAtendimentoOnline = new ArrayList<MotivosAtendimentoOnlineDTO>();
		listDestinosCargaOnline = new ArrayList<DestinoCargaOnlineDTO>();
		listOcorrenciasAtendimentoOnline = new ArrayList<AtendimentoOcorrenciasOnlineDTO>();
		
		SincronizarCarregarOcorrenciaGlogOnline mAuthTask = new SincronizarCarregarOcorrenciaGlogOnline(db, contextOcorrenciaGlogOnlineActivity, usuarioLogado);
		mAuthTask.execute();
	}
	
	final public static void atualizarListaOcorrenciasFila() {
		lvDadosOcorrenciaOnlineFila.setAdapter(new DadosOcorrenciaGlogOnlineAdapter(contextOcorrenciaGlogOnlineActivity, 0, listOcorrenciaGlogOnline));
		txtTotalDadosOcorrenciaOnline.setText("Total: " + lvDadosOcorrenciaOnlineFila.getAdapter().getCount() + " ocorr�ncias");
	}
	
	private static class DadosOcorrenciaGlogOnlineAdapter extends ArrayAdapter<AtendimentoOnlineDTO>{
		
		LayoutInflater inflater;

		public DadosOcorrenciaGlogOnlineAdapter(Context context, int textViewResourceId,
				List<AtendimentoOnlineDTO> objects) {
			super(context, textViewResourceId, objects);
			inflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			
			if (v == null)
				v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
			
			AtendimentoOnlineDTO g = getItem(position);
			TextView txt1 = (TextView) v.findViewById(android.R.id.text1);
			txt1.setTextColor(Color.parseColor("#000000"));

			String msgText1 = "N�mero: " + g.getNumero() + 
					"\nMotivo: " + g.getDescricao_motivo() + 
					"\nData: " + g.getData() + 
					"\nSitua��o: " + g.getDescricao_situacao();
			txt1.setText(msgText1);

			return v;
		}
	}
}

package br.inf.commerce.multimobilegpa.android;

import java.util.List;

import org.kroz.activerecord.ActiveRecordBase;

import MultiMobile.DTO.DadosCargaOnlineDTO;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class VisualizarDadosCargaOnlineActivity extends Activity {

	private static ActiveRecordBase db;
	private static Context contextDadosCargaOnlineActivity;
	private static ListView lvDadosCargaOnlineFila;
	private static TextView txtTotalDadosCargaOnline;

	private static TextView lblCD;
	private static TextView lblNumero;
	private static TextView lblTipo;
	private static TextView lblPeso;
	private static TextView lblSaida;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_dados_carga_online_fila);
		contextDadosCargaOnlineActivity = VisualizarDadosCargaOnlineActivity.this;
		db = ((CommerceMobileApp) getApplication()).getDatabase();
		
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ED7D31")));
		bar.setHomeAsUpIndicator(R.drawable.ic_button_left_arrow);
		bar.setDisplayHomeAsUpEnabled(true);

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
			View rootView = inflater.inflate(R.layout.fragment_dados_carga_online_fila,
					container, false);
			
			lvDadosCargaOnlineFila = (ListView) rootView.findViewById(R.id.id_list_view_dados_carga_online_fila);
			txtTotalDadosCargaOnline = (TextView) rootView.findViewById(R.id.id_lbl_total_dados_carga_online_fila);
			
			lblCD = (TextView) rootView.findViewById(R.id.id_lbl_cd_dados_carga_online_fila);
    		lblNumero = (TextView) rootView.findViewById(R.id.id_lbl_numero_dados_carga_online_fila);
    		lblTipo = (TextView) rootView.findViewById(R.id.id_lbl_tipo_dados_carga_online_fila);
    		lblPeso = (TextView) rootView.findViewById(R.id.id_lbl_peso_dados_carga_online_fila);
    		lblSaida = (TextView) rootView.findViewById(R.id.id_lbl_saida_dados_carga_online_fila);			

    		atualizarListaCargasFila();
			return rootView;
		}
	}
	
	final public static void atualizarListaCargasFila() {
		lvDadosCargaOnlineFila.setAdapter(new DadosCargaOnlineAdapter(contextDadosCargaOnlineActivity, 0, PrincipalActivity.listRelatorioDadosCargaOnline));		
		txtTotalDadosCargaOnline.setText("Total: " + lvDadosCargaOnlineFila.getAdapter().getCount() + " pedidos");
		
		if (PrincipalActivity.listRelatorioDadosCargaOnline.size() > 0){
			lblCD.setText(Html.fromHtml("<b>CD: </b>" + PrincipalActivity.listRelatorioDadosCargaOnline.get(0).getCd_carga()));
			lblNumero.setText(Html.fromHtml("<b>Carga: </b>" + PrincipalActivity.listRelatorioDadosCargaOnline.get(0).getNumero_carga()));
			lblTipo.setText(Html.fromHtml("<b>Tipo da Carga: </b>" + PrincipalActivity.listRelatorioDadosCargaOnline.get(0).getTipo_carga()));
			lblPeso.setText(Html.fromHtml("<b>Peso: </b>" + PrincipalActivity.listRelatorioDadosCargaOnline.get(0).getPeso_carga()));
			lblSaida.setText(Html.fromHtml("<b>Saída: </b>" + PrincipalActivity.listRelatorioDadosCargaOnline.get(0).getSaida_carga()));
		}
	}
	
	public static class DadosCargaOnlineAdapter extends ArrayAdapter<DadosCargaOnlineDTO>{
		
		LayoutInflater inflater;

		public DadosCargaOnlineAdapter(Context context, int textViewResourceId,
				List<DadosCargaOnlineDTO> objects) {	
			super(context, textViewResourceId, objects);
			inflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			
			if (v == null)
				v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
			
			DadosCargaOnlineDTO g = getItem(position);
			TextView txt1 = (TextView) v.findViewById(android.R.id.text1);
			txt1.setTextColor(Color.parseColor("#000000"));
			txt1.setTextSize(14);

			String msgText1 = "Destino: " + g.getDestino_pedido() + 
					"\nEndere�o: " + g.getEndereco_destino_pedido() + 
					"\nPeso: " + g.getPeso_pedido();
			txt1.setText(msgText1);

			return v;
		}
	}
}

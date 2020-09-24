package br.inf.commerce.multimobilegpa.android;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.kroz.activerecord.ActiveRecordBase;

import MultiMobile.DAO.CargaFila;
import MultiMobile.Utils.Funcoes;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class VisualizarCargaFilaActivity extends Activity {

	private static ActiveRecordBase db;
	private static Context contextVisualizarActivity;
	private static ProgressDialog mProgressCarregarLista = null;
	private static List<CargaFila> listCargasFila = null;
	private static ListView lvCargasFila;
	private static TextView txtTotalCarga;
	
	public static final int DIALOG_CARREGAR_LISTA_PROGRESS = 3;
	public static final int DATE_PICKER_ID_Data_Inicial = 4;
	public static final int DATE_PICKER_ID_Data_Final = 5;
	
	private static Button btnDataInicial;
	private static Button btnDataFinal;
	
	private static int year;
    private static int month;
    private static int day;
    private static int yearInicial;
    private static int monthInicial;
    private static int dayInicial;
    private static String dataInicialSelecionada = "";
	private static String dataFinalSelecionada = "";

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_carga_fila);
		contextVisualizarActivity = VisualizarCargaFilaActivity.this;
		db = ((CommerceMobileApp) getApplication()).getDatabase();
		
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ED7D31")));
		bar.setHomeAsUpIndicator(R.drawable.ic_button_left_arrow);
		bar.setDisplayHomeAsUpEnabled(true);
		
		listCargasFila = new ArrayList<CargaFila>();
		
		final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        yearInicial = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        monthInicial = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        dayInicial = c.get(Calendar.DAY_OF_MONTH);
        
        dataInicialSelecionada = "";
    	dataFinalSelecionada = "";    	
    	
    	CarregarListaCargasFilaTask mAuthTask = new CarregarListaCargasFilaTask(contextVisualizarActivity);
		mAuthTask.execute(dataInicialSelecionada, dataFinalSelecionada);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override 
	public boolean onNavigateUp(){ 
	     finish(); 
	     return true; 
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
	    switch (id) {
		    case DATE_PICKER_ID_Data_Inicial:
	    		return new DatePickerDialog(contextVisualizarActivity, pickerListenerDataInicial, yearInicial, monthInicial, dayInicial);
	    	case DATE_PICKER_ID_Data_Final:
	    		return new DatePickerDialog(contextVisualizarActivity, pickerListenerDataFinal, year, month, day);
		    case DIALOG_CARREGAR_LISTA_PROGRESS:
		    	mProgressCarregarLista = new ProgressDialog(contextVisualizarActivity);
		    	mProgressCarregarLista.setTitle("Carregando Lista de Cargas...");
	        	mProgressCarregarLista.setMessage("Por favor aguarde até o fim...");
		    	mProgressCarregarLista.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		    	mProgressCarregarLista.setCancelable(false);
		    	mProgressCarregarLista.show();
		        return mProgressCarregarLista;
		    default:
		    return null;
	    }
	}
	
	private DatePickerDialog.OnDateSetListener pickerListenerDataInicial = new DatePickerDialog.OnDateSetListener() {
		 
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                int selectedMonth, int selectedDay) {
             
        	yearInicial  = selectedYear;
        	monthInicial = selectedMonth;
        	dayInicial   = selectedDay;
            
        	dataInicialSelecionada = String.valueOf(new StringBuilder().append(dayInicial)
                    .append("/").append(monthInicial + 1).append("/").append(yearInicial)
                    .append(" "));
            
            btnDataInicial.setText(Funcoes.dateToString(Funcoes.stringToDate(dataInicialSelecionada)));
     
        }
    };
    
    private DatePickerDialog.OnDateSetListener pickerListenerDataFinal = new DatePickerDialog.OnDateSetListener() {
		 
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                int selectedMonth, int selectedDay) {
             
            year  = selectedYear;
            month = selectedMonth;
            day   = selectedDay;
            
            dataFinalSelecionada = String.valueOf(new StringBuilder().append(day)
                    .append("/").append(month + 1).append("/").append(year)
                    .append(" "));
            
            btnDataFinal.setText(Funcoes.dateToString(Funcoes.stringToDate(dataFinalSelecionada)));
     
        }
    };
	
	final private static void atualizarListaCargasFila() {
		if (listCargasFila != null && lvCargasFila != null){
			ArrayList<CargaFila> array = (ArrayList<CargaFila>) listCargasFila;
			
			if(array != null)
				lvCargasFila.setAdapter(new CargaFilaAdapter(contextVisualizarActivity, 0, array));
			
			txtTotalCarga.setText("Total: " + lvCargasFila.getAdapter().getCount() + " cargas");
		}
	}

	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_carga_fila,
					container, false);
			
			lvCargasFila = (ListView) rootView.findViewById(R.id.id_list_view_cargas_fila);
			txtTotalCarga = (TextView) rootView.findViewById(R.id.id_lbl_total_carga_fila);
			
			Button btnFiltrar = (Button) rootView.findViewById(R.id.id_btn_filtro_carga_fila);
			btnDataInicial = (Button) rootView.findViewById(R.id.id_btn_data_inicial_filtro_carga_fila);
			btnDataFinal = (Button) rootView.findViewById(R.id.id_btn_data_final_filtro_carga_fila);
			Button btnPesquisarCargas = (Button) rootView.findViewById(R.id.id_btn_filtrar_carga_fila);			
			
			final LinearLayout llDescFiltros = (LinearLayout) rootView.findViewById(R.id.id_ll_lbl_filtro_carga_fila);
			final LinearLayout llFiltros = (LinearLayout) rootView.findViewById(R.id.id_ll_btn_filtro_carga_fila);
			final LinearLayout llBtnFiltrar = (LinearLayout) rootView.findViewById(R.id.id_ll_btn_filtrar_carga_fila);
			
			btnFiltrar.setOnClickListener(new OnClickListener() {					
				@Override
				public void onClick(View v) {
					if (llBtnFiltrar.getVisibility() == View.GONE){
						llBtnFiltrar.setVisibility(View.VISIBLE);
						llFiltros.setVisibility(View.VISIBLE);
						llDescFiltros.setVisibility(View.VISIBLE);
					}
					else{
						llBtnFiltrar.setVisibility(View.GONE);
						llFiltros.setVisibility(View.GONE);
						llDescFiltros.setVisibility(View.GONE);
					}
				}
			});
			
			btnDataInicial.setOnClickListener(new OnClickListener() {
				
				@SuppressWarnings("deprecation")
				@Override
				public void onClick(View v) {
					getActivity().showDialog(DATE_PICKER_ID_Data_Inicial);						
				}
			});
			
			btnDataFinal.setOnClickListener(new OnClickListener() {
				
				@SuppressWarnings("deprecation")
				@Override
				public void onClick(View v) {
					getActivity().showDialog(DATE_PICKER_ID_Data_Final);						
				}
			});
			
			btnPesquisarCargas.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {					
					CarregarListaCargasFilaTask mAuthTask = new CarregarListaCargasFilaTask(contextVisualizarActivity);
					mAuthTask.execute(dataInicialSelecionada, dataFinalSelecionada);
				}
			});

			return rootView;
		}
	}
	
	public static class CargaFilaAdapter extends ArrayAdapter<CargaFila>{
		
		LayoutInflater inflater;

		public CargaFilaAdapter(Context context, int textViewResourceId,
				List<CargaFila> objects) {	
			super(context, textViewResourceId, objects);
			inflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			
			if (v == null)
				v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
			
			CargaFila g = getItem(position);
			TextView txt1 = (TextView) v.findViewById(android.R.id.text1);
			txt1.setTextColor(Color.parseColor("#000000"));
						
			String msgText1 = "Data: " + Funcoes.longToString(g.getData());
			msgText1 += " - CD: " + g.getDescricao();
			txt1.setText(msgText1);

			return v;
		}
	}
	
	public static class CarregarListaCargasFilaTask extends AsyncTask<String, Integer, String> {
		
		public CarregarListaCargasFilaTask(Context context) {
    	}
		
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			((Activity) contextVisualizarActivity).showDialog(DIALOG_CARREGAR_LISTA_PROGRESS);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {	
			String dataInicial = params[0];
			String dataFinal = params[1];
			
			listCargasFila = Funcoes.retornListaCargaFila(db, dataInicial, dataFinal);
			
			return "Sucesso";
		}

		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(String result) {
			atualizarListaCargasFila();
			
			((Activity) contextVisualizarActivity).removeDialog(DIALOG_CARREGAR_LISTA_PROGRESS);
		}
	}

}

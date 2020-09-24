package br.inf.commerce.multimobilegpa.android;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kroz.activerecord.ActiveRecordBase;

import MultiMobile.DTO.AtendimentoOcorrenciasOnlineDTO;
import MultiMobile.DTO.DestinoCargaOnlineDTO;
import MultiMobile.DTO.AtendimentoOnlineDTO;
import MultiMobile.Enums.EnumSituacaoChamadoWEB;
import MultiMobile.Enums.EnumTipoMotivoAtendimentoWEB;
import MultiMobile.Services.GPSTracker;
import MultiMobile.Threads.SincronizarChegadaSaida;
import MultiMobile.Threads.SincronizarEnviarOcorrenciaGlogOnline;
import MultiMobile.Utils.Funcoes;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class OcorrenciaGlogActivity extends Activity {

	private static ActiveRecordBase db;
	private static Context contextOcorrenciaGlogActivity;	
	private static String usuarioLogado;
	private static int codigoAtendimento = 0;
	
	//private static final int REQUEST_QRCODE = 2;
	//private static final int REQUEST_IMAGEM = 3;
	
//	static Date dataInicial;
	static Date dataFinal;
//	static Time horaInicial;
	static Time horaFinal;
	//private static Button btnTempoTotal;
	//private static Button btnQrcode;
	//private static Button btnImagem;
	//private static Spinner spnMotivo;
	private static Spinner spnDestino;
	//private static TextView lblDestino;
	
	private static int tipoOcorrenciaSelecionada = 0;
	//private static int tipoRetencaoSelecionada = 0;
	//private static String qrCodeLeitura = "";
	//private static boolean leuImagem = false;
	//private static MotivosAtendimentoOnlineDTO motivoSelecionado = null;
	private static DestinoCargaOnlineDTO destinoSelecionado = null;
	public static AtendimentoOnlineDTO ocorrenciaGlog = null;
	public static ArrayList<AtendimentoOcorrenciasOnlineDTO> listOcorrenciasAtendimento = new ArrayList<AtendimentoOcorrenciasOnlineDTO>();
	//private static ListView lvOcorrenciasAtendimentoFila;
	//private static  ArrayAdapter<MotivosAtendimentoOnlineDTO> spinnerArrayAdapterMotivo = null;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_ocorrencia_glog_fila);
		contextOcorrenciaGlogActivity = OcorrenciaGlogActivity.this;
		db = ((CommerceMobileApp) getApplication()).getDatabase();
		
		if(listOcorrenciasAtendimento != null)
			listOcorrenciasAtendimento.clear();
				
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ED7D31")));
		bar.setHomeAsUpIndicator(R.drawable.ic_button_left_arrow);
		bar.setDisplayHomeAsUpEnabled(true);
		
		Intent intent = getIntent();
		Bundle params = intent.getExtras();
		usuarioLogado = params.getString("usuarioLogado");
		codigoAtendimento = params.getInt("codigoAtendimento");

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new PlanetFragment()).commit();
		}
	}

	@Override 
	public boolean onNavigateUp(){
		finish();
		return true;
	}
	
	public static void fecharActivity(){
		((Activity) contextOcorrenciaGlogActivity).finish();
	}
	
    /**
     * Fragment that appears in the "content_frame", shows a planet
     */
    public static class PlanetFragment extends Fragment {
    	
        public PlanetFragment() {
        	
        }
        
        /*@Override
    	public void onActivityResult(int requestCode, int resultCode,
    			Intent data) {
    		super.onActivityResult(requestCode, resultCode, data);
    		
    		try{
    			if (requestCode == REQUEST_QRCODE){
    	        	if (resultCode == RESULT_OK) {
    	        		String contents = data.getStringExtra("SCAN_RESULT");
    	        		qrCodeLeitura = contents;
    	        	} else
    	        		Toast.makeText(contextOcorrenciaGlogActivity, "Leitura QRCode cancelada", Toast.LENGTH_LONG).show();
    	        } else if (requestCode == REQUEST_IMAGEM){
    	        	if (resultCode == RESULT_OK) {
    	        		leuImagem = true;
    	        	} else
    	        		Toast.makeText(contextOcorrenciaGlogActivity, "Leitura Imagem cancelada", Toast.LENGTH_LONG).show();
    	        }
    		
    		} catch (Exception e) {
    			Toast.makeText(contextOcorrenciaGlogActivity, e.getMessage(), Toast.LENGTH_LONG).show();
    		}
    	}
        
        final private void capturarImagemOcorrenciaGlog() {
    		Uri outputFileUri = Uri.fromFile(Funcoes.getOutputMediaFile("OcorrenciaGLog", contextOcorrenciaGlogActivity));
    		Intent intentImagem = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
    		intentImagem.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
    		if (Build.VERSION.SDK_INT <= 21)
    			intentImagem.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    		startActivityForResult(intentImagem, REQUEST_IMAGEM);
    	}*/

		@Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        	View rootView = null;
    		rootView = inflater.inflate(R.layout.fragment_ocorrencia_glog_fila, container, false);
    		
    		//Campos da tela
    		Spinner spnTipo = (Spinner) rootView.findViewById(R.id.id_spn_tipo_ocorrencia_glog_fila);
    		//final Spinner spnTipoRetencao = (Spinner) rootView.findViewById(R.id.id_spn_tipo_retencao_ocorrencia_glog_fila);
    		//final TextView lblTipoRetencao = (TextView) rootView.findViewById(R.id.id_lbl_tipo_retencao_ocorrencia_glog_fila);
    		spnDestino = (Spinner) rootView.findViewById(R.id.id_spn_destino_ocorrencia_glog_fila);
    		//lblDestino = (TextView) rootView.findViewById(R.id.id_lbl_destino_ocorrencia_glog_fila);
    		//spnMotivo = (Spinner) rootView.findViewById(R.id.id_spn_motivo_ocorrencia_glog_fila);
    		
    		final Button btnDataInicial = (Button) rootView.findViewById(R.id.id_btn_data_inicial_ocorrencia_glog_fila);
    		final Button btnDataFinal = (Button) rootView.findViewById(R.id.id_btn_data_final_ocorrencia_glog_fila);
    		final Button btnHoraInicial = (Button) rootView.findViewById(R.id.id_btn_hora_inicial_ocorrencia_glog_fila);
    		final Button btnHoraFinal = (Button) rootView.findViewById(R.id.id_btn_hora_final_ocorrencia_glog_fila);
    		//TextView lblTempoTotal = (TextView) rootView.findViewById(R.id.id_lbl_tempo_ocorrencia_glog_fila);
    		//btnTempoTotal = (Button) rootView.findViewById(R.id.id_btn_tempo_ocorrencia_glog_fila);
    		
    		final Button btnDataReentrega = (Button) rootView.findViewById(R.id.id_btn_data_reentrega_ocorrencia_glog_fila);
    		final Button btnHoraReentrega = (Button) rootView.findViewById(R.id.id_btn_hora_reentrega_ocorrencia_glog_fila);
    		
    		final LinearLayout llLblDatas = (LinearLayout) rootView.findViewById(R.id.id_ll_lbl_datas_ocorrencia_glog_fila);
    		final LinearLayout llBtnDatas = (LinearLayout) rootView.findViewById(R.id.id_ll_btn_datas_ocorrencia_glog_fila);
    		final LinearLayout llLblHoras = (LinearLayout) rootView.findViewById(R.id.id_ll_lbl_horas_ocorrencia_glog_fila);
    		final LinearLayout llBtnHoras = (LinearLayout) rootView.findViewById(R.id.id_ll_btn_horas_ocorrencia_glog_fila);
    		final LinearLayout llLblDataReentrega = (LinearLayout) rootView.findViewById(R.id.id_ll_lbl_data_reentrega_ocorrencia_glog_fila);
    		final LinearLayout llBtnDataReentrega = (LinearLayout) rootView.findViewById(R.id.id_ll_btn_data_reentrega_ocorrencia_glog_fila);
    		
    		//btnQrcode = (Button) rootView.findViewById(R.id.id_btn_qrcode_ocorrencia_glog_online_fila);
    		//btnImagem = (Button) rootView.findViewById(R.id.id_btn_imagem_ocorrencia_glog_online_fila);
    		Button btnGerarSolicitacaoOcorrencia = (Button) rootView.findViewById(R.id.id_btn_gerar_solicitacao_ocorrencia_glog_online_fila);
    		
    		final CheckBox chkRetencao = (CheckBox) rootView.findViewById(R.id.id_chk_retencao_ocorrencia_glog_fila);
    		final EditText txtPlaca = (EditText) rootView.findViewById(R.id.id_txt_placa_ocorrencia_glog_fila);
    		final LinearLayout llLblPlaca = (LinearLayout) rootView.findViewById(R.id.id_ll_lbl_placa_ocorrencia_glog_fila);
    		final LinearLayout llTxtPlaca = (LinearLayout) rootView.findViewById(R.id.id_ll_txt_placa_ocorrencia_glog_fila);
    		
    		llLblPlaca.setVisibility(View.GONE);
    		llTxtPlaca.setVisibility(View.GONE);
    		
    		//Campos para visualização do retorno
//    		final LinearLayout llLblRetorno = (LinearLayout) rootView.findViewById(R.id.id_ll_lbl_retorno_atendimento_glog_fila);
//    		final LinearLayout llTxtRetorno = (LinearLayout) rootView.findViewById(R.id.id_ll_txt_retorno_atendimento_glog_fila);
//    		final LinearLayout llLvOcorrencias = (LinearLayout) rootView.findViewById(R.id.id_ll_lv_ocorrencias_atendimento_glog_fila);
    		final LinearLayout llButtonsTela = (LinearLayout) rootView.findViewById(R.id.id_ll_buttons_ocorrencia_glog_online_fila);
//    		final EditText textRetorno = (EditText) rootView.findViewById(R.id.id_txt_retorno_atendimento_glog_fila);
//    		lvOcorrenciasAtendimentoFila = (ListView) rootView.findViewById(R.id.id_list_view_ocorrencias_atendimento_glog_fila);
    		final LinearLayout llLblMensagemStatus = (LinearLayout) rootView.findViewById(R.id.id_ll_lbl_mensagem_status_atendimento_glog_fila);
    		final TextView lblMensagemStatus = (TextView) rootView.findViewById(R.id.id_lbl_mensagem_status_atendimento_glog_fila);
    		final LinearLayout llBtnImagemStatus = (LinearLayout) rootView.findViewById(R.id.id_ll_btn_imagem_status_atendimento_glog_fila);
    		Button btnImagemStatus = (Button) rootView.findViewById(R.id.id_btn_imagem_status_atendimento_glog_fila);
    		Button btnInformarSaida = (Button) rootView.findViewById(R.id.id_btn_informar_saida_atendimento_glog_fila);
    		
    		final EditText txtSenha = (EditText) rootView.findViewById(R.id.id_txt_senha_ocorrencia_glog_fila);
    		final LinearLayout llLblSenha = (LinearLayout) rootView.findViewById(R.id.id_ll_lbl_senha_ocorrencia_glog_fila);
    		final LinearLayout llTxtSenha = (LinearLayout) rootView.findViewById(R.id.id_ll_txt_senha_ocorrencia_glog_fila);
    		
//    		llLblRetorno.setVisibility(View.GONE);
//    		llTxtRetorno.setVisibility(View.GONE);
//    		llLvOcorrencias.setVisibility(View.GONE);
    		llLblDatas.setVisibility(View.GONE);
			llBtnDatas.setVisibility(View.GONE);
			llLblHoras.setVisibility(View.GONE);
			llBtnHoras.setVisibility(View.GONE);
    		llLblDataReentrega.setVisibility(View.GONE);
			llBtnDataReentrega.setVisibility(View.GONE);
			llLblMensagemStatus.setVisibility(View.GONE);
			llBtnImagemStatus.setVisibility(View.GONE);
			btnInformarSaida.setVisibility(View.GONE);
			llLblSenha.setVisibility(View.GONE);
			llTxtSenha.setVisibility(View.GONE);
    		
    		//Monta os valores nos campos
    		List<String> tipoOcorrencia = new ArrayList<String>();
			tipoOcorrencia.add("Reten��o");
			tipoOcorrencia.add("Reentrega");
			
			ArrayAdapter<String> spinnerArrayAdapterTipo = new ArrayAdapter<String>(contextOcorrenciaGlogActivity, R.layout.spinner_item, tipoOcorrencia);
			spinnerArrayAdapterTipo.setDropDownViewResource(R.layout.spinner_item);
			spnTipo.setAdapter(spinnerArrayAdapterTipo);
			
			spnTipo.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					tipoOcorrenciaSelecionada = position;
					if (position == 1){//Reentrega
//						spnTipoRetencao.setVisibility(View.GONE);
//						lblTipoRetencao.setVisibility(View.GONE);
						chkRetencao.setVisibility(View.GONE);
						chkRetencao.setChecked(false);
//						llLblDatas.setVisibility(View.GONE);
//						llBtnDatas.setVisibility(View.GONE);
//						llLblHoras.setVisibility(View.GONE);
//						llBtnHoras.setVisibility(View.GONE);
					}
					else{//Reten��o
//						spnTipoRetencao.setVisibility(View.VISIBLE);
//						lblTipoRetencao.setVisibility(View.VISIBLE);
						chkRetencao.setVisibility(View.VISIBLE);
//						llLblDatas.setVisibility(View.VISIBLE);
//						llBtnDatas.setVisibility(View.VISIBLE);
//						llLblHoras.setVisibility(View.VISIBLE);
//						llBtnHoras.setVisibility(View.VISIBLE);
					}
					//atualizarVisibilidadeSelecaoDestino();
					//atualizarMotivoPorTipo();
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					
				}
			});
    		
			/*List<String> tipoRetencao = new ArrayList<String>();
			tipoRetencao.add("Origem");
			tipoRetencao.add("Destino");
			
			ArrayAdapter<String> spinnerArrayAdapterRetencao = new ArrayAdapter<String>(contextOcorrenciaGlogActivity, R.layout.spinner_item, tipoRetencao);
			spinnerArrayAdapterRetencao.setDropDownViewResource(R.layout.spinner_item);
			spnTipoRetencao.setAdapter(spinnerArrayAdapterRetencao);
			
			spnTipoRetencao.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					tipoRetencaoSelecionada = position;
					atualizarVisibilidadeSelecaoDestino();
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					
				}
			});*/
			
	   		ArrayAdapter<DestinoCargaOnlineDTO> spinnerArrayAdapterDestino = new ArrayAdapter<DestinoCargaOnlineDTO>(contextOcorrenciaGlogActivity, R.layout.spinner_item, VisualizarOcorrenciaGlogOnlineActivity.listDestinosCargaOnline);
	   		spinnerArrayAdapterDestino.setDropDownViewResource(R.layout.spinner_item);
	   		spnDestino.setAdapter(spinnerArrayAdapterDestino);
	   		
	   		/*atualizarMotivoPorTipo();
	   		
	   		spnMotivo.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					atualizarVisibilidadeMotivo();
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					
				}
			});

    		dataInicial = Funcoes.stringToDate(Funcoes.PegarDataAtual());
    		dataFinal = Funcoes.stringToDate(Funcoes.PegarDataAtual());
    		btnDataInicial.setText(Funcoes.PegarDataAtual());
    		btnDataFinal.setText(Funcoes.PegarDataAtual());
    		
    		btnDataInicial.setOnClickListener(new View.OnClickListener() {
    			
	            @Override
	            public void onClick(View view) {
	            	
	                @SuppressWarnings("deprecation")
					DatePickerDialog datePickerDialog = new DatePickerDialog(contextOcorrenciaGlogActivity,
	                        new DatePickerDialog.OnDateSetListener() {
	                            @Override
	                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
	                            	btnDataInicial.setText(Funcoes.formatarData(day, month, year));
	                            	dataInicial = Funcoes.stringToDate(btnDataInicial.getText().toString());
	                            	//calcularTempoTotal();
	                            }
	                        }, dataInicial.getYear() + 1900, dataInicial.getMonth(), dataInicial.getDate());
	                datePickerDialog.show();
	            }
	        });*/
			
			btnDataFinal.setOnClickListener(new View.OnClickListener() {
				
	            @Override
	            public void onClick(View view) {
	            	
	                @SuppressWarnings("deprecation")
					DatePickerDialog datePickerDialog = new DatePickerDialog(contextOcorrenciaGlogActivity,
	                        new DatePickerDialog.OnDateSetListener() {
	                            @Override
	                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
	                            	btnDataFinal.setText(Funcoes.formatarData(day, month, year));
	                            	dataFinal = Funcoes.stringToDate(btnDataFinal.getText().toString());
	                            	//calcularTempoTotal();
	                            }
	                        }, dataFinal.getYear() + 1900, dataFinal.getMonth(), dataFinal.getDate());
	                datePickerDialog.show();
	            }
			});
			
			/*horaInicial = Funcoes.stringToTimeTime(Funcoes.PegarHoraAtual());
    		horaFinal = Funcoes.stringToTimeTime(Funcoes.PegarHoraAtual());
    		btnHoraInicial.setText(Funcoes.PegarHoraAtualSemSegundos());
    		btnHoraFinal.setText(Funcoes.PegarHoraAtualSemSegundos());
    		
			btnHoraInicial.setOnClickListener(new View.OnClickListener() {
    			
	            @Override
	            public void onClick(View view) {
	            	
	                @SuppressWarnings("deprecation")
	                TimePickerDialog timePickerDialog = new TimePickerDialog(contextOcorrenciaGlogActivity,
	                        new TimePickerDialog.OnTimeSetListener() {
								
								@Override
								public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
									btnHoraInicial.setText(Funcoes.formatarHora(hourOfDay, minute));
									horaInicial = Funcoes.stringToTimeTime(btnHoraInicial.getText().toString());
									//calcularTempoTotal();
								}
							}, horaInicial.getHours(), horaInicial.getMinutes(), DateFormat.is24HourFormat(getActivity()));
	                timePickerDialog.show();
	            }
	        });*/
    		
			btnHoraFinal.setOnClickListener(new View.OnClickListener() {
    			
	            @Override
	            public void onClick(View view) {
	            	
	                @SuppressWarnings("deprecation")
	                TimePickerDialog timePickerDialog = new TimePickerDialog(contextOcorrenciaGlogActivity,
	                        new TimePickerDialog.OnTimeSetListener() {
								
								@Override
								public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
									btnHoraFinal.setText(Funcoes.formatarHora(hourOfDay, minute));
									horaFinal = Funcoes.stringToTimeTime(btnHoraFinal.getText().toString());
									//calcularTempoTotal();
								}
							}, horaFinal.getHours(), horaFinal.getMinutes(), DateFormat.is24HourFormat(getActivity()));
	                timePickerDialog.show();
	            }
	        });
			
			/*btnDataReentrega.setText(Funcoes.PegarDataAtual());
			btnHoraReentrega.setText(Funcoes.PegarHoraAtualSemSegundos());
			
			btnDataReentrega.setOnClickListener(new View.OnClickListener() {
    			
	            @Override
	            public void onClick(View view) {
	            	
	                @SuppressWarnings("deprecation")
					DatePickerDialog datePickerDialog = new DatePickerDialog(contextOcorrenciaGlogActivity,
	                        new DatePickerDialog.OnDateSetListener() {
	                            @Override
	                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
	                            	btnDataReentrega.setText(Funcoes.formatarData(day, month, year));
	                            	dataInicial = Funcoes.stringToDate(btnDataReentrega.getText().toString());
	                            }
	                        }, dataInicial.getYear() + 1900, dataInicial.getMonth(), dataInicial.getDate());
	                datePickerDialog.show();
	            }
	        });
			
			btnHoraReentrega.setOnClickListener(new View.OnClickListener() {
    			
	            @Override
	            public void onClick(View view) {
	            	
	                @SuppressWarnings("deprecation")
	                TimePickerDialog timePickerDialog = new TimePickerDialog(contextOcorrenciaGlogActivity,
	                        new TimePickerDialog.OnTimeSetListener() {
								
								@Override
								public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
									btnHoraReentrega.setText(Funcoes.formatarHora(hourOfDay, minute));
									horaInicial = Funcoes.stringToTimeTime(btnHoraReentrega.getText().toString());
									calcularTempoTotal();
								}
							}, horaInicial.getHours(), horaInicial.getMinutes(), DateFormat.is24HourFormat(getActivity()));
	                timePickerDialog.show();
	            }
	        });
    		
			btnQrcode.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
			            //start the scanning activity from the com.google.zxing.client.android.SCAN intent
			            Intent intent = new Intent(PrincipalActivity.ACTION_SCAN);
			            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
			            startActivityForResult(intent, REQUEST_QRCODE);
			        } catch (ActivityNotFoundException anfe) {
			        	PrincipalActivity.showDialog(getActivity(), "Nenhum scanner encontrado", "Deseja baixar?", "Sim", "N�o").show();
			        } catch (Exception e) {
			        	PrincipalActivity.showDialog(getActivity(), "Nenhum scanner encontrado", "Deseja baixar?", "Sim", "N�o").show();
			        }
				}
			});
			
			btnImagem.setOnClickListener(new OnClickListener() {
    			@Override
				public void onClick(View v) {
    				capturarImagemOcorrenciaGlog();
				}
			});*/
			
			chkRetencao.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked){
						llLblPlaca.setVisibility(View.VISIBLE);
			    		llTxtPlaca.setVisibility(View.VISIBLE);
					} else {
						llLblPlaca.setVisibility(View.GONE);
			    		llTxtPlaca.setVisibility(View.GONE);
			    		txtPlaca.setText("");
					}
				}
			});
			
			btnGerarSolicitacaoOcorrencia.setOnClickListener(new OnClickListener() {
    			@Override
				public void onClick(View v) {
    				/*if (motivoSelecionado.isExige_qrcode() && Funcoes.isNullOrEmpty(qrCodeLeitura))
    					Toast.makeText(contextOcorrenciaGlogActivity, "Favor efetuar a leitura do QR Code!", Toast.LENGTH_LONG).show();
    				else if (motivoSelecionado.isExige_foto() && !leuImagem)
    					Toast.makeText(contextOcorrenciaGlogActivity, "Favor tirar a foto!", Toast.LENGTH_LONG).show();    				 
    				else if (tipoOcorrenciaSelecionada == 0 && dataInicial.compareTo(dataFinal) > 0)
    					Toast.makeText(contextOcorrenciaGlogActivity, "Data inicial maior que a final!", Toast.LENGTH_LONG).show();*/
    				if (chkRetencao.isChecked() && Funcoes.isNullOrEmpty(txtPlaca.getText().toString()))
    					Toast.makeText(contextOcorrenciaGlogActivity, "Favor informar a Placa do Reboque!", Toast.LENGTH_LONG).show();
    				else{
    					GPSTracker gps = new GPSTracker(contextOcorrenciaGlogActivity);
						if(gps.canGetLocation()){
							if (gps.getLatitude() != 0 && gps.getLongitude() != 0){
								destinoSelecionado = (DestinoCargaOnlineDTO) spnDestino.getSelectedItem();
								
								int tipoMotivo = 0;
								if (tipoOcorrenciaSelecionada == 0)
									tipoMotivo = EnumTipoMotivoAtendimentoWEB.Retencao.getValue();
								else if (tipoOcorrenciaSelecionada == 1)
									tipoMotivo = EnumTipoMotivoAtendimentoWEB.Reentrega.getValue();
								
//								int tipoCliente = 0;
//								if (tipoRetencaoSelecionada == 0)//Origem
//									tipoCliente = EnumTipoTomadorWEB.Remetente.getValue();
//								else if (tipoRetencaoSelecionada == 1)//Destino
//									tipoCliente = EnumTipoTomadorWEB.Destinatario.getValue();
								
//								String dataHoraInicial = btnDataInicial.getText().toString() + horaInicial.toString();
//								String dataHoraFinal = btnDataFinal.getText().toString() + horaFinal.toString();
								
								createSincronizarDadosOcorrenciaGlogOnlineDialog(gps.getLatitude(), gps.getLongitude(), tipoMotivo,
										destinoSelecionado.getId_commerce(), chkRetencao.isChecked(), txtPlaca.getText().toString());
								
							} else
					        	   Toast.makeText(contextOcorrenciaGlogActivity, "N�o foi poss�vel obter a localiza��o! Favor tentar novamente.", Toast.LENGTH_SHORT).show();
						} else
		            		   gps.showSettingsAlert();
    				}
				}
			});
			
			btnInformarSaida.setOnClickListener(new OnClickListener() {
    			@Override
				public void onClick(View v) {
    				if (Funcoes.isNullOrEmpty(txtSenha.getText().toString()))
    					Toast.makeText(contextOcorrenciaGlogActivity, "Favor informar a Senha da Loja!", Toast.LENGTH_LONG).show();
    				else {
    					GPSTracker gps = new GPSTracker(contextOcorrenciaGlogActivity);
						if(gps.canGetLocation()){
							if (gps.getLatitude() != 0 && gps.getLongitude() != 0){
								String dataHoraFinal = btnDataFinal.getText().toString() + horaFinal.toString();
								try {
									SincronizarChegadaSaida sinc = new SincronizarChegadaSaida(db, contextOcorrenciaGlogActivity, usuarioLogado);
									sinc.execute(destinoSelecionado.getId_commerce(), Double.toString(gps.getLatitude()), Double.toString(gps.getLongitude()), String.valueOf(true),
											dataHoraFinal, txtSenha.getText().toString());
								} catch (Exception e) {
									Toast.makeText(contextOcorrenciaGlogActivity, e.getMessage(), Toast.LENGTH_LONG).show();
								}
							} else
					        	   Toast.makeText(contextOcorrenciaGlogActivity, "N�o foi poss�vel obter a localiza��o! Favor tentar novamente.", Toast.LENGTH_SHORT).show();
						} else
		            		   gps.showSettingsAlert();
    				}
				}
			});
			
			if (codigoAtendimento > 0){ //Visualiza atendimento j� gerado
				List<AtendimentoOnlineDTO> list = VisualizarOcorrenciaGlogOnlineActivity.listOcorrenciaGlogOnline;
				for(int i=0; i < list.size(); i++){
					AtendimentoOnlineDTO atendimento = list.get(i);
					if (codigoAtendimento == atendimento.getId_commerce())
						ocorrenciaGlog = atendimento;
				}
				
				/*ArrayList<AtendimentoOcorrenciasOnlineDTO> listOcorrencias = VisualizarOcorrenciaGlogOnlineActivity.listOcorrenciasAtendimentoOnline;
				for(int i=0; i < listOcorrencias.size(); i++){
					AtendimentoOcorrenciasOnlineDTO ocorrencia = listOcorrencias.get(i);
					if (codigoAtendimento == ocorrencia.getId_atendimento())
						listOcorrenciasAtendimento.add(ocorrencia);
				}
				
				ArrayList<MotivosAtendimentoOnlineDTO> listMotivos = VisualizarOcorrenciaGlogOnlineActivity.listMotivosAtendimentoOnline;
				for(int i=0; i < listMotivos.size(); i++){
					MotivosAtendimentoOnlineDTO motivo = listMotivos.get(i);
					if (ocorrenciaGlog.getCodigo_motivo() == motivo.getId_commerce())
						motivoSelecionado = motivo;
				}*/
				
				ArrayList<DestinoCargaOnlineDTO> listDestino = VisualizarOcorrenciaGlogOnlineActivity.listDestinosCargaOnline;
				for(int i=0; i < listDestino.size(); i++){
					DestinoCargaOnlineDTO destino = listDestino.get(i);
					if (ocorrenciaGlog.getCnpj_cliente().equals(destino.getId_commerce()))
						destinoSelecionado = destino;
				}
				
				//preencherSpinnerMotivoCompleto();
				
				if(ocorrenciaGlog.getTipo() == EnumTipoMotivoAtendimentoWEB.Reentrega.getValue()){
					spnTipo.setSelection(1);
					llLblDataReentrega.setVisibility(View.VISIBLE);
					llBtnDataReentrega.setVisibility(View.VISIBLE);
				} else {
					llLblDatas.setVisibility(View.VISIBLE);
					llBtnDatas.setVisibility(View.VISIBLE);
					llLblHoras.setVisibility(View.VISIBLE);
					llBtnHoras.setVisibility(View.VISIBLE);
				}
				//if(ocorrenciaGlog.getTipo_cliente() == EnumTipoTomadorWEB.Destinatario.getValue())
				//	spnTipoRetencao.setSelection(1);

				spnDestino.setSelection(spinnerArrayAdapterDestino.getPosition(destinoSelecionado));
	   			//spnMotivo.setSelection(spinnerArrayAdapterMotivo.getPosition(motivoSelecionado));
				
				chkRetencao.setChecked(ocorrenciaGlog.isRetencao_bau());
				btnDataInicial.setText(Funcoes.RetornaDataSemHora(ocorrenciaGlog.getData_entrada_raio()));
				btnDataFinal.setText(Funcoes.RetornaDataSemHora(ocorrenciaGlog.getData_saida_raio()));
				btnDataReentrega.setText(Funcoes.RetornaDataSemHora(ocorrenciaGlog.getData_reentrega()));
				btnHoraInicial.setText(Funcoes.RetornaHoraSemData(ocorrenciaGlog.getData_entrada_raio()));
				btnHoraFinal.setText(Funcoes.RetornaHoraSemData(ocorrenciaGlog.getData_saida_raio()));
				btnHoraReentrega.setText(Funcoes.RetornaHoraSemData(ocorrenciaGlog.getData_reentrega()));
				txtPlaca.setText(ocorrenciaGlog.getPlaca_reboque());
//				textRetorno.setText(ocorrenciaGlog.getAnalises());
				
				spnTipo.setEnabled(false);
				//spnTipoRetencao.setEnabled(false);
				spnDestino.setEnabled(false);
				//spnMotivo.setEnabled(false);
				chkRetencao.setEnabled(false);
				btnDataInicial.setEnabled(false);
				btnDataFinal.setEnabled(false);
				btnDataReentrega.setEnabled(false);
				btnHoraInicial.setEnabled(false);
				btnHoraFinal.setEnabled(false);
				btnHoraReentrega.setEnabled(false);
				txtPlaca.setEnabled(false);
//				textRetorno.setEnabled(false);
				
				//lblTempoTotal.setVisibility(View.GONE);
				//btnTempoTotal.setVisibility(View.GONE);
				llButtonsTela.setVisibility(View.GONE);
				llLblMensagemStatus.setVisibility(View.VISIBLE);
				llBtnImagemStatus.setVisibility(View.VISIBLE);
//				llLblRetorno.setVisibility(View.VISIBLE);
//	    		llTxtRetorno.setVisibility(View.VISIBLE);
//	    		llLvOcorrencias.setVisibility(View.VISIBLE);
//	    		
//	    		lvOcorrenciasAtendimentoFila.setAdapter(new OcorrenciasAtendimentoAdapter(contextOcorrenciaGlogActivity, 0, listOcorrenciasAtendimento));
								
				if (ocorrenciaGlog.getCodigo_situacao() == EnumSituacaoChamadoWEB.Aberto.getValue()){
					Drawable top = getResources().getDrawable(R.drawable.imagem_atencao);
					btnImagemStatus.setCompoundDrawablesWithIntrinsicBounds(null, top , null, null);
					lblMensagemStatus.setText("Aguarde na loja, ocorr�ncia em tratativa");
					
					if (!ocorrenciaGlog.isRetencao_bau() && Funcoes.isNullOrEmpty(ocorrenciaGlog.getData_saida_raio()) 
							&& ocorrenciaGlog.getTipo() == EnumTipoMotivoAtendimentoWEB.Retencao.getValue()){//Habilita bot�o para sa�da, e permite editar a data final
						dataFinal = Funcoes.stringToDate(Funcoes.PegarDataAtual());
			    		btnDataFinal.setText(Funcoes.PegarDataAtual());
						horaFinal = Funcoes.stringToTimeTime(Funcoes.PegarHoraAtual());
			    		btnHoraFinal.setText(Funcoes.PegarHoraAtualSemSegundos());
			    		btnDataFinal.setEnabled(true);
			    		btnHoraFinal.setEnabled(true);
			    		
			    		btnInformarSaida.setVisibility(View.VISIBLE);
						llLblSenha.setVisibility(View.VISIBLE);
						llTxtSenha.setVisibility(View.VISIBLE);
						lblMensagemStatus.setText("Preencha a Data/Hora Fim e a Senha para sair da loja");
					}
					
				} else if (ocorrenciaGlog.getCodigo_situacao() == EnumSituacaoChamadoWEB.Finalizado.getValue()){
					Drawable top = getResources().getDrawable(R.drawable.imagem_aprovado);
					btnImagemStatus.setCompoundDrawablesWithIntrinsicBounds(null, top , null, null);
					if (ocorrenciaGlog.getTipo() == EnumTipoMotivoAtendimentoWEB.Reentrega.getValue()){
						lblMensagemStatus.setText("Reentrega autorizada." +
								"\nFavor retornar ao CD de origem e aguardar instru��es." +
								"\nOcorr�ncia: " + ocorrenciaGlog.getNumero_ocorrencia());
					} else {
						lblMensagemStatus.setText("Autorizado Desatrelar." +
								"\nOcorr�ncia: " + ocorrenciaGlog.getNumero_ocorrencia());
					}
				} else if (ocorrenciaGlog.getCodigo_situacao() == EnumSituacaoChamadoWEB.Cancelada.getValue()){
					Drawable top = getResources().getDrawable(R.drawable.imagem_reprovado);
					btnImagemStatus.setCompoundDrawablesWithIntrinsicBounds(null, top , null, null);
					lblMensagemStatus.setText("Aguarde!" +
							"\nSer� descarregado.");
				} else {
					llLblMensagemStatus.setVisibility(View.GONE);
					llBtnImagemStatus.setVisibility(View.GONE);
				}
			}
			
            return rootView;
        }
    }
    
    @SuppressLint("DefaultLocale")
	final private static void createSincronizarDadosOcorrenciaGlogOnlineDialog(double latitude, double longitude, int tipoMotivo,
    		String cnpjCliente, boolean retencaoBau, String placaReboque){
		SincronizarEnviarOcorrenciaGlogOnline mAuthTask = new SincronizarEnviarOcorrenciaGlogOnline(db, contextOcorrenciaGlogActivity, usuarioLogado);
		
		mAuthTask.execute(String.valueOf(latitude), String.valueOf(longitude), String.valueOf(tipoMotivo),
				cnpjCliente, String.valueOf(retencaoBau), placaReboque.toUpperCase());
	}
	
	/*@SuppressLint("SimpleDateFormat")
	private static void calcularTempoTotal(){
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		try {
			Date inicio = format.parse(Funcoes.dateToString(dataInicial) + " " + horaInicial.toString());
			Date fim = format.parse(Funcoes.dateToString(dataFinal) + " " + horaFinal.toString());
		
			long diffMinutes = 0;
			long diffHours = 0;
			long diff = fim.getTime() - inicio.getTime();
			
			if (diff > 0){
				diffMinutes = diff / (60 * 1000) % 60;
				diffHours = diff / (60 * 60 * 1000);
			}
			
			String tempo = Funcoes.formatarHora((int)diffHours, (int)diffMinutes);
			
			btnTempoTotal.setText(tempo);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
	/*private static void preencherSpinnerMotivoCompleto(){
		spnMotivo.setAdapter(null);
		
		spinnerArrayAdapterMotivo = new ArrayAdapter<MotivosAtendimentoOnlineDTO>(contextOcorrenciaGlogActivity, R.layout.spinner_item, VisualizarOcorrenciaGlogOnlineActivity.listMotivosAtendimentoOnline);
   		spinnerArrayAdapterMotivo.setDropDownViewResource(R.layout.spinner_item);
   		spnMotivo.setAdapter(spinnerArrayAdapterMotivo);
	}
	
	private static void atualizarMotivoPorTipo(){
		spnMotivo.setAdapter(null);
		
		ArrayList<MotivosAtendimentoOnlineDTO> listMotivosAtualizados = new ArrayList<MotivosAtendimentoOnlineDTO>();
		ArrayList<MotivosAtendimentoOnlineDTO> listMotivos = VisualizarOcorrenciaGlogOnlineActivity.listMotivosAtendimentoOnline;
		for(int i=0; i < listMotivos.size(); i++){
			MotivosAtendimentoOnlineDTO motivo = listMotivos.get(i);
			if (motivo.getTipo() == EnumTipoMotivoAtendimentoWEB.Reentrega.getValue() && tipoOcorrenciaSelecionada == 1)
				listMotivosAtualizados.add(motivo);
			else if (motivo.getTipo() == EnumTipoMotivoAtendimentoWEB.Retencao.getValue() && tipoOcorrenciaSelecionada == 0)
				listMotivosAtualizados.add(motivo);
		}
		
		spinnerArrayAdapterMotivo = new ArrayAdapter<MotivosAtendimentoOnlineDTO>(contextOcorrenciaGlogActivity, R.layout.spinner_item, listMotivosAtualizados);
   		spinnerArrayAdapterMotivo.setDropDownViewResource(R.layout.spinner_item);
   		spnMotivo.setAdapter(spinnerArrayAdapterMotivo);
   		
   		atualizarVisibilidadeMotivo();
	}
	
	private static void atualizarVisibilidadeMotivo(){
		motivoSelecionado = (MotivosAtendimentoOnlineDTO) spnMotivo.getSelectedItem();
		
		btnQrcode.setVisibility(View.GONE);
		btnImagem.setVisibility(View.GONE);
		
		if (motivoSelecionado != null && motivoSelecionado.isExige_qrcode())
			btnQrcode.setVisibility(View.VISIBLE);
		if (motivoSelecionado != null && motivoSelecionado.isExige_foto())
			btnImagem.setVisibility(View.VISIBLE);
	}*/
	
	/*private static void atualizarVisibilidadeSelecaoDestino(){
		lblDestino.setVisibility(View.GONE);
		spnDestino.setVisibility(View.GONE);
		
		if (tipoOcorrenciaSelecionada == 1 || tipoRetencaoSelecionada == 1){
			lblDestino.setVisibility(View.VISIBLE);
			spnDestino.setVisibility(View.VISIBLE);
		}
	}*/
	
	/*private static class OcorrenciasAtendimentoAdapter extends ArrayAdapter<AtendimentoOcorrenciasOnlineDTO>{
		
		LayoutInflater inflater;

		public OcorrenciasAtendimentoAdapter(Context context, int textViewResourceId,
				List<AtendimentoOcorrenciasOnlineDTO> objects) {
			super(context, textViewResourceId, objects);
			inflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			
			if (v == null)
				v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
			
			AtendimentoOcorrenciasOnlineDTO g = getItem(position);
			TextView txt1 = (TextView) v.findViewById(android.R.id.text1);
			txt1.setTextColor(Color.parseColor("#000000"));

			String msgText1 = "N�mero: " + g.getNumero_ocorrencia() + 
					"\nTipo: " + g.getTipo_ocorrencia() + 
					"\nSitua��o: " + g.getDescricao_situacao_ocorrencia() + 
					"\nValor: " + Funcoes.formatarValor(g.getValor_ocorrencia(), false);
			txt1.setText(msgText1);

			return v;
		}
	}*/
}

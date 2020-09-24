package br.inf.commerce.multimobilegpa.android;

import br.inf.commerce.multimobilegpa.android.R;
import MultiMobile.Utils.Funcoes;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends AppCompatActivity {
	String notificacaoTipoMensagem = "false";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
                
        Intent intent = getIntent();
		Bundle params = intent.getExtras();
		if (params != null && !Funcoes.isNullOrEmpty(params.getString("NotificacaoTipoMensagem")))
			notificacaoTipoMensagem = params.getString("NotificacaoTipoMensagem");			
        
        new Handler().postDelayed(new Runnable() {
			
			public void run() {
				Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
				Bundle params = new Bundle();
				params.putString("NotificacaoTipoMensagem", notificacaoTipoMensagem);
				intent.putExtras(params);
				startActivity(intent);
				finish();
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			}
		}, 3000);
	}
}
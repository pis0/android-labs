package br.inf.commerce.multimobilegpa.android;

import MultiMobile.Utils.Funcoes;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

public class SignalRNotificationService extends IntentService {

	public SignalRNotificationService() {
		super("NotificacaoService");
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onHandleIntent(Intent intent) {
		//Open app onclick notification
		Intent notificationIntent = new Intent(getApplicationContext(), SplashActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		if (!Funcoes.isNullOrEmpty(intent.getStringExtra("NotificacaoTipoMensagem")))
			notificationIntent.putExtra("NotificacaoTipoMensagem", intent.getStringExtra("NotificacaoTipoMensagem"));
	    PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	    
	    //Carrega som definido
	    SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
	    String strRingtonePreference = preference.getString("ringtone", "DEFAULT_SOUND");
		
	    //Gera a notificação
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		builder.setSmallIcon(R.drawable.notificacao)
		  		.setAutoCancel(true)
				.setDefaults(builder.getNotification().defaults)// | Notification.DEFAULT_SOUND)
				.setLights(0xFF0000FF, 500, 1000)
				.setVibrate(new long[] { 100, 250, 100, 500 })
				.setContentTitle("Multi Mobile GPA")
				.setStyle(new NotificationCompat.BigTextStyle().bigText("Fila: " + intent.getStringExtra("Message")))
	    		.setContentIntent(pendingIntent)
	    		.setSound(Uri.parse(strRingtonePreference));
	    
	    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(0, builder.build());
	}
}
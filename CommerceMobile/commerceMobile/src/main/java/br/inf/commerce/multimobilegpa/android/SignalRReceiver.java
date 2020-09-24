package br.inf.commerce.multimobilegpa.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SignalRReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().contentEquals(Intent.ACTION_BOOT_COMPLETED)
				|| intent.getAction().contentEquals(
						android.net.ConnectivityManager.CONNECTIVITY_ACTION)) {

//			Intent pushIntent = new Intent(context, SignalRService.class);
//			context.startService(pushIntent);
			
			Log.d("SIGNALR", "onReceive SignalRReceiver");
			context.startService(new Intent("SERVICO_CONEXAO_MOBILEGPA"));
		}
	}
}

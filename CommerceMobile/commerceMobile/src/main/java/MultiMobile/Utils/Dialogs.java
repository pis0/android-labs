package MultiMobile.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Dialogs {
	
	public static boolean ExibirConfirmacao(Context context, String title, String message,
            String buttonPositive){
		return ExibirConfirmacao(context, title, message, buttonPositive, null, "", null, "", null);
	}
	
	public static boolean ExibirConfirmacao(Context context, String title, String message,
            String buttonPositive, final Runnable callbackPositive){
		return ExibirConfirmacao(context, title, message, buttonPositive, callbackPositive, "", null, "", null);
	}
	
	public static boolean ExibirConfirmacao(Context context, String title, String message,
            String buttonPositive, final Runnable callbackPositive, 
            String buttonNegative){
		return ExibirConfirmacao(context, title, message, buttonPositive, callbackPositive, buttonNegative, null, "", null);
	}
	
	public static boolean ExibirConfirmacao(Context context, String title, String message,
            String buttonPositive, final Runnable callbackPositive, 
            String buttonNegative, final Runnable callbackNegative){
		return ExibirConfirmacao(context, title, message, buttonPositive, callbackPositive, buttonNegative, callbackNegative, "", null);
	}
	
	public static boolean ExibirConfirmacao(Context context, String title, String message,
            String buttonPositive, final Runnable callbackPositive, 
            String buttonNegative, final Runnable callbackNegative,
            String buttonNeutral){
		return ExibirConfirmacao(context, title, message, buttonPositive, callbackPositive, buttonNegative, callbackNegative, buttonNeutral, null);
	}

	public static boolean ExibirConfirmacao(Context context, String title, String message,
			String buttonPositive, final Runnable callbackPositive, 
            String buttonNegative, final Runnable callbackNegative, 
            String buttonNeutral, final Runnable callbackNeutral) {
		
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, buttonPositive,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int buttonId) {
                    	callbackPositive.run();
                    }
                });
        
        if (!Funcoes.isNullOrEmpty(buttonNegative))
        	dialog.setButton(DialogInterface.BUTTON_NEGATIVE, buttonNegative,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int buttonId) {
                    	if (callbackNegative != null)
                    		callbackNegative.run();
                    }
                });
        
        if (!Funcoes.isNullOrEmpty(buttonNeutral))
        	dialog.setButton(DialogInterface.BUTTON_NEUTRAL, buttonNeutral,
	            new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int buttonId) {
	                	if (callbackNeutral != null)
	                		callbackNeutral.run();
	                }
	            });
        
        //dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.show();
        return true;
	}
	
	public static AlertDialog showDialogSincronismoPadrao(Context context, CharSequence title, CharSequence message) {
    	AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		alertDialog.setCancelable(false);
		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
		new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
		
		    }
		});
		
		alertDialog.show();
		final AlertDialog dialogEntrada = (AlertDialog)alertDialog;
		dialogEntrada.getButton(DialogInterface.BUTTON_NEUTRAL).setEnabled(false);

		return alertDialog;
    }
	
	public static AlertDialog showAlertDialogOneButtonPadrao(Context context, CharSequence title, CharSequence message) {
    	AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		alertDialog.setCancelable(false);
		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
		new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
		
		    }
		});
		
		alertDialog.show();
		return alertDialog;
    }
}
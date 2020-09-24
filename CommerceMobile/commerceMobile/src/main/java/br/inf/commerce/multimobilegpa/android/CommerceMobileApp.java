package br.inf.commerce.multimobilegpa.android;

import org.kroz.activerecord.ActiveRecordBase;
import org.kroz.activerecord.ActiveRecordException;
import org.kroz.activerecord.Database;
import org.kroz.activerecord.DatabaseBuilder;

import br.inf.commerce.multimobilegpa.android.R;

import MultiMobile.DAO.CargaFila;
import MultiMobile.DAO.ClienteMultisoftware;
import MultiMobile.DAO.MotivoSaidaFila;
import MultiMobile.DAO.NotificacaoFila;
import MultiMobile.DAO.Usuario;
import android.app.Application;
import android.util.Log;

public class CommerceMobileApp extends Application {

	public ActiveRecordBase mDatabase;
	private boolean carregarListaDepoisSincronismo = false;
	public int dbVersion;

	@Override
	public void onCreate() {
		super.onCreate();
		dbVersion = Integer.parseInt(this.getString(R.string.dbVersion));

		Log.d("DBMultiMobile", "Iniciando APP Multi Mobile");
		DatabaseBuilder builder = new DatabaseBuilder("multimobilegpa.db");
		builder.addClass(Usuario.class);
		builder.addClass(ClienteMultisoftware.class);
		builder.addClass(MotivoSaidaFila.class);
		builder.addClass(NotificacaoFila.class);
		builder.addClass(CargaFila.class);

		Database.setBuilder(builder);

		try {
			mDatabase = ActiveRecordBase
					.open(this, "multimobilegpa.db", dbVersion);
		} catch (ActiveRecordException e) {
			Log.d("Erro", e.getMessage().toString());
			e.printStackTrace();
		}
	}

	public ActiveRecordBase getDatabase() {
		return mDatabase;
	}

	public boolean getCarregarListaDepoisSincronismo() {
		return this.carregarListaDepoisSincronismo;
	}

	public void setCarregarListaDepoisSincronismoe(boolean v) {
		this.carregarListaDepoisSincronismo = v;
	}
}

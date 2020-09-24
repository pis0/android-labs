package MultiMobile.DAO;

import org.kroz.activerecord.ActiveRecordBase;

public class NotificacaoFila extends ActiveRecordBase {
	public int id_commerce;
	public String descricao;
	public String data;

	@Override
	public String toString() {
		return "";
	}

	public NotificacaoFila() {

	}

	public NotificacaoFila(String descricao) {
		super();
		this.descricao = descricao;
	}

	public NotificacaoFila(int id_commerce, String descricao, String data) {
		super();
		this.id_commerce = id_commerce;
		this.descricao = descricao;
		this.data = data;
	}

	public int getId_commerce() {
		return id_commerce;
	}

	public void setId_commerce(int id_commerce) {
		this.id_commerce = id_commerce;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}

package MultiMobile.DAO;

import org.kroz.activerecord.ActiveRecordBase;

public class CargaFila extends ActiveRecordBase {
	public String descricao;
	public long data;

	@Override
	public String toString() {
		return "";
	}

	public CargaFila() {

	}

	public CargaFila(String descricao, long data) {
		super();
		this.descricao = descricao;
		this.data = data;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public long getData() {
		return data;
	}

	public void setData(long data) {
		this.data = data;
	}

}

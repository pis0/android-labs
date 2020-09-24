package MultiMobile.DAO;

import org.kroz.activerecord.ActiveRecordBase;

public class MotivoSaidaFila extends ActiveRecordBase {
	public int id_commerce;
	public String descricao;

	@Override
	public String toString() {
		return "";
	}

	public MotivoSaidaFila() {

	}

	public MotivoSaidaFila(int id_commerce, String descricao) {
		super();
		this.id_commerce = id_commerce;
		this.descricao = descricao;
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
}
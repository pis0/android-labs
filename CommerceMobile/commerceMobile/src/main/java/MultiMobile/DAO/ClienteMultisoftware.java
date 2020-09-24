package MultiMobile.DAO;

import org.kroz.activerecord.ActiveRecordBase;

public class ClienteMultisoftware extends ActiveRecordBase {
	public int id_commerce;
	public String nome;
	public int codigo_integracao;

	@Override
	public String toString() {
		return "";
	}

	public ClienteMultisoftware() {

	}

	public ClienteMultisoftware(int id_commerce, String nome,
			int codigo_integracao) {
		super();
		this.id_commerce = id_commerce;
		this.nome = nome;
		this.codigo_integracao = codigo_integracao;
	}

	public int getId_commerce() {
		return id_commerce;
	}

	public void setId_commerce(int id_commerce) {
		this.id_commerce = id_commerce;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getCodigo_integracao() {
		return codigo_integracao;
	}

	public void setCodigo_integracao(int codigo_integracao) {
		this.codigo_integracao = codigo_integracao;
	}
}

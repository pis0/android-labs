package MultiMobile.DTO;

public class DestinoCargaOnlineDTO {
	public String id_commerce;
	public String descricao;

	@Override
	public String toString() {
		return getDescricao();
	}

	public DestinoCargaOnlineDTO() {

	}

	public DestinoCargaOnlineDTO(String id_commerce, String descricao) {
		super();
		this.id_commerce = id_commerce;
		this.descricao = descricao;
	}

	public String getId_commerce() {
		return id_commerce;
	}

	public void setId_commerce(String id_commerce) {
		this.id_commerce = id_commerce;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	
}

package MultiMobile.DTO;

public class MotivosAtendimentoOnlineDTO {
	public int id_commerce;
	public String descricao;
	public boolean exige_foto;
	public boolean exige_qrcode;
	public int tipo;

	@Override
	public String toString() {
		return getDescricao();
	}

	public MotivosAtendimentoOnlineDTO() {

	}

	public MotivosAtendimentoOnlineDTO(int id_commerce, String descricao,
			boolean exige_foto, boolean exige_qrcode, int tipo) {
		super();
		this.id_commerce = id_commerce;
		this.descricao = descricao;
		this.exige_foto = exige_foto;
		this.exige_qrcode = exige_qrcode;
		this.tipo = tipo;
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

	public boolean isExige_foto() {
		return exige_foto;
	}

	public void setExige_foto(boolean exige_foto) {
		this.exige_foto = exige_foto;
	}

	public boolean isExige_qrcode() {
		return exige_qrcode;
	}

	public void setExige_qrcode(boolean exige_qrcode) {
		this.exige_qrcode = exige_qrcode;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

}

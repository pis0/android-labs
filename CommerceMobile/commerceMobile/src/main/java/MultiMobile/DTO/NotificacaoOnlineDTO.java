package MultiMobile.DTO;

public class NotificacaoOnlineDTO {
	public int id_commerce;
	public String assunto;
	public String data;

	@Override
	public String toString() {
		return "";
	}

	public NotificacaoOnlineDTO() {

	}

	public NotificacaoOnlineDTO(int id_commerce, String assunto, String data) {
		super();
		this.id_commerce = id_commerce;
		this.assunto = assunto;
		this.data = data;
	}

	public int getId_commerce() {
		return id_commerce;
	}

	public void setId_commerce(int id_commerce) {
		this.id_commerce = id_commerce;
	}

	public String getAssunto() {
		return assunto;
	}

	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}

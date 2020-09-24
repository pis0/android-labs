package MultiMobile.DTO;

public class DadosCargaOnlineDTO {

	public int id_commerce;
	public String numero_carga;
	public String cd_carga;
	public String tipo_carga;
	public String peso_carga;
	public String saida_carga;

	public String numero_pedido;
	public String destino_pedido;
	public String endereco_destino_pedido;
	public String peso_pedido;

	@Override
	public String toString() {
		return "";
	}

	public DadosCargaOnlineDTO() {

	}

	public DadosCargaOnlineDTO(int id_commerce, String numero_carga,
			String cd_carga, String tipo_carga, String peso_carga,
			String saida_carga, String numero_pedido, String destino_pedido,
			String endereco_destino_pedido, String peso_pedido) {
		super();
		this.id_commerce = id_commerce;
		this.numero_carga = numero_carga;
		this.cd_carga = cd_carga;
		this.tipo_carga = tipo_carga;
		this.peso_carga = peso_carga;
		this.saida_carga = saida_carga;

		this.numero_pedido = numero_pedido;
		this.destino_pedido = destino_pedido;
		this.endereco_destino_pedido = endereco_destino_pedido;
		this.peso_pedido = peso_pedido;
	}

	public int getId_commerce() {
		return id_commerce;
	}

	public void setId_commerce(int id_commerce) {
		this.id_commerce = id_commerce;
	}

	public String getNumero_carga() {
		return numero_carga;
	}

	public void setNumero_carga(String numero_carga) {
		this.numero_carga = numero_carga;
	}

	public String getCd_carga() {
		return cd_carga;
	}

	public void setCd_carga(String cd_carga) {
		this.cd_carga = cd_carga;
	}

	public String getTipo_carga() {
		return tipo_carga;
	}

	public void setTipo_carga(String tipo_carga) {
		this.tipo_carga = tipo_carga;
	}

	public String getPeso_carga() {
		return peso_carga;
	}

	public void setPeso_carga(String peso_carga) {
		this.peso_carga = peso_carga;
	}

	public String getSaida_carga() {
		return saida_carga;
	}

	public void setSaida_carga(String saida_carga) {
		this.saida_carga = saida_carga;
	}

	public String getNumero_pedido() {
		return numero_pedido;
	}

	public void setNumero_pedido(String numero_pedido) {
		this.numero_pedido = numero_pedido;
	}

	public String getDestino_pedido() {
		return destino_pedido;
	}

	public void setDestino_pedido(String destino_pedido) {
		this.destino_pedido = destino_pedido;
	}

	public String getEndereco_destino_pedido() {
		return endereco_destino_pedido;
	}

	public void setEndereco_destino_pedido(String endereco_destino_pedido) {
		this.endereco_destino_pedido = endereco_destino_pedido;
	}

	public String getPeso_pedido() {
		return peso_pedido;
	}

	public void setPeso_pedido(String peso_pedido) {
		this.peso_pedido = peso_pedido;
	}

}

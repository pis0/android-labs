package MultiMobile.DTO;

public class AtendimentoOnlineDTO {
	private int id_commerce;
	private String descricao_motivo;
	private String data;
	private String descricao_situacao;
	private int numero;
	private int tipo;
	private int tipo_cliente;
	private String cnpj_cliente;
	private String descricao_cliente;
	private int codigo_situacao;
	private int codigo_motivo;
	private String data_retencao_inicial;
	private String data_retencao_final;
	private String analises;
	private String qrcode;
	private boolean retencao_bau;
	private double latitude;
	private double longitude;
	private String data_reentrega;
	private String numero_ocorrencia;
	private String data_entrada_raio;
	private String data_saida_raio;
	private String placa_reboque;

	@Override
	public String toString() {
		return "";
	}

	public AtendimentoOnlineDTO() {

	}

	public AtendimentoOnlineDTO(int id_commerce, String descricao_motivo,
			String data, String descricao_situacao, int numero, int tipo,
			int tipo_cliente, String cnpj_cliente, String descricao_cliente,
			int codigo_situacao, int codigo_motivo,
			String data_retencao_inicial, String data_retencao_final,
			String analises, String qrcode, boolean retencao_bau,
			double latitude, double longitude, String data_reentrega,
			String numero_ocorrencia, String data_entrada_raio,
			String data_saida_raio, String placa_reboque) {
		super();
		this.id_commerce = id_commerce;
		this.descricao_motivo = descricao_motivo;
		this.data = data;
		this.descricao_situacao = descricao_situacao;
		this.numero = numero;
		this.tipo = tipo;
		this.tipo_cliente = tipo_cliente;
		this.cnpj_cliente = cnpj_cliente;
		this.descricao_cliente = descricao_cliente;
		this.codigo_situacao = codigo_situacao;
		this.codigo_motivo = codigo_motivo;
		this.data_retencao_inicial = data_retencao_inicial;
		this.data_retencao_final = data_retencao_final;
		this.analises = analises;
		this.qrcode = qrcode;
		this.retencao_bau = retencao_bau;
		this.latitude = latitude;
		this.longitude = longitude;
		this.data_reentrega = data_reentrega;
		this.numero_ocorrencia = numero_ocorrencia;
		this.data_entrada_raio = data_entrada_raio;
		this.data_saida_raio = data_saida_raio;
		this.placa_reboque = placa_reboque;
	}

	public int getId_commerce() {
		return id_commerce;
	}

	public void setId_commerce(int id_commerce) {
		this.id_commerce = id_commerce;
	}

	public String getDescricao_motivo() {
		return descricao_motivo;
	}

	public void setDescricao_motivo(String descricao_motivo) {
		this.descricao_motivo = descricao_motivo;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getDescricao_situacao() {
		return descricao_situacao;
	}

	public void setDescricao_situacao(String descricao_situacao) {
		this.descricao_situacao = descricao_situacao;
	}

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	public int getTipo_cliente() {
		return tipo_cliente;
	}

	public void setTipo_cliente(int tipo_cliente) {
		this.tipo_cliente = tipo_cliente;
	}

	public String getCnpj_cliente() {
		return cnpj_cliente;
	}

	public void setCnpj_cliente(String cnpj_cliente) {
		this.cnpj_cliente = cnpj_cliente;
	}

	public String getDescricao_cliente() {
		return descricao_cliente;
	}

	public void setDescricao_cliente(String descricao_cliente) {
		this.descricao_cliente = descricao_cliente;
	}

	public int getCodigo_situacao() {
		return codigo_situacao;
	}

	public void setCodigo_situacao(int codigo_situacao) {
		this.codigo_situacao = codigo_situacao;
	}

	public int getCodigo_motivo() {
		return codigo_motivo;
	}

	public void setCodigo_motivo(int codigo_motivo) {
		this.codigo_motivo = codigo_motivo;
	}

	public String getData_retencao_inicial() {
		return data_retencao_inicial;
	}

	public void setData_retencao_inicial(String data_retencao_inicial) {
		this.data_retencao_inicial = data_retencao_inicial;
	}

	public String getData_retencao_final() {
		return data_retencao_final;
	}

	public void setData_retencao_final(String data_retencao_final) {
		this.data_retencao_final = data_retencao_final;
	}

	public String getAnalises() {
		return analises;
	}

	public void setAnalises(String analises) {
		this.analises = analises;
	}

	public String getQrcode() {
		return qrcode;
	}

	public void setQrcode(String qrcode) {
		this.qrcode = qrcode;
	}

	public boolean isRetencao_bau() {
		return retencao_bau;
	}

	public void setRetencao_bau(boolean retencao_bau) {
		this.retencao_bau = retencao_bau;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getData_reentrega() {
		return data_reentrega;
	}

	public void setData_reentrega(String data_reentrega) {
		this.data_reentrega = data_reentrega;
	}

	public String getNumero_ocorrencia() {
		return numero_ocorrencia;
	}

	public void setNumero_ocorrencia(String numero_ocorrencia) {
		this.numero_ocorrencia = numero_ocorrencia;
	}

	public String getData_entrada_raio() {
		return data_entrada_raio;
	}

	public void setData_entrada_raio(String data_entrada_raio) {
		this.data_entrada_raio = data_entrada_raio;
	}

	public String getData_saida_raio() {
		return data_saida_raio;
	}

	public void setData_saida_raio(String data_saida_raio) {
		this.data_saida_raio = data_saida_raio;
	}

	public String getPlaca_reboque() {
		return placa_reboque;
	}

	public void setPlaca_reboque(String placa_reboque) {
		this.placa_reboque = placa_reboque;
	}

}

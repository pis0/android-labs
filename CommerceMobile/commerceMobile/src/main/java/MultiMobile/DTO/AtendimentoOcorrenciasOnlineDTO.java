package MultiMobile.DTO;

public class AtendimentoOcorrenciasOnlineDTO {
	private int id_atendimento;
	private String descricao_situacao_ocorrencia;
	private String destino_ocorrencia;
	private String numero_ocorrencia;
	private String observacao_ocorrencia;
	private String origem_ocorrencia;
	private String tipo_ocorrencia;
	private double valor_ocorrencia;

	@Override
	public String toString() {
		return "";
	}

	public AtendimentoOcorrenciasOnlineDTO() {

	}

	public AtendimentoOcorrenciasOnlineDTO(int id_atendimento,
			String descricao_situacao_ocorrencia, String destino_ocorrencia,
			String numero_ocorrencia, String observacao_ocorrencia,
			String origem_ocorrencia, String tipo_ocorrencia,
			double valor_ocorrencia) {
		super();
		this.id_atendimento = id_atendimento;
		this.descricao_situacao_ocorrencia = descricao_situacao_ocorrencia;
		this.destino_ocorrencia = destino_ocorrencia;
		this.numero_ocorrencia = numero_ocorrencia;
		this.observacao_ocorrencia = observacao_ocorrencia;
		this.origem_ocorrencia = origem_ocorrencia;
		this.tipo_ocorrencia = tipo_ocorrencia;
		this.valor_ocorrencia = valor_ocorrencia;
	}

	public int getId_atendimento() {
		return id_atendimento;
	}

	public void setId_atendimento(int id_atendimento) {
		this.id_atendimento = id_atendimento;
	}

	public String getDescricao_situacao_ocorrencia() {
		return descricao_situacao_ocorrencia;
	}

	public void setDescricao_situacao_ocorrencia(
			String descricao_situacao_ocorrencia) {
		this.descricao_situacao_ocorrencia = descricao_situacao_ocorrencia;
	}

	public String getDestino_ocorrencia() {
		return destino_ocorrencia;
	}

	public void setDestino_ocorrencia(String destino_ocorrencia) {
		this.destino_ocorrencia = destino_ocorrencia;
	}

	public String getNumero_ocorrencia() {
		return numero_ocorrencia;
	}

	public void setNumero_ocorrencia(String numero_ocorrencia) {
		this.numero_ocorrencia = numero_ocorrencia;
	}

	public String getObservacao_ocorrencia() {
		return observacao_ocorrencia;
	}

	public void setObservacao_ocorrencia(String observacao_ocorrencia) {
		this.observacao_ocorrencia = observacao_ocorrencia;
	}

	public String getOrigem_ocorrencia() {
		return origem_ocorrencia;
	}

	public void setOrigem_ocorrencia(String origem_ocorrencia) {
		this.origem_ocorrencia = origem_ocorrencia;
	}

	public String getTipo_ocorrencia() {
		return tipo_ocorrencia;
	}

	public void setTipo_ocorrencia(String tipo_ocorrencia) {
		this.tipo_ocorrencia = tipo_ocorrencia;
	}

	public double getValor_ocorrencia() {
		return valor_ocorrencia;
	}

	public void setValor_ocorrencia(double valor_ocorrencia) {
		this.valor_ocorrencia = valor_ocorrencia;
	}

}

package MultiMobile.Utils;

public enum EnumTipoAlteracaoFilaCarregamentoWEB {
	CargaAlocada(1), PerdeuSenha(2), CargaCancelada(3), Mensagem(4), SolicitacaoSaidaAceita(
			5), SolicitacaoSaidaRecusada(6);

	private final int value;

	private EnumTipoAlteracaoFilaCarregamentoWEB(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}

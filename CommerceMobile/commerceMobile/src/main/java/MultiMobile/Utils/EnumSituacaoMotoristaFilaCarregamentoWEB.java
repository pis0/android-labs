package MultiMobile.Utils;

public enum EnumSituacaoMotoristaFilaCarregamentoWEB {
	AguardandoCarga(1), AguardandoConfirmacao(2), PerdeuSenha(3), RecusouCarga(
			4), CargaCancelada(5);

	private final int value;

	private EnumSituacaoMotoristaFilaCarregamentoWEB(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}

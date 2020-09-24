package MultiMobile.Enums;

public enum EnumTipoMotivoAtendimentoWEB {
	Atendimento(0), Devolucao(1), Reentrega(2), Retencao(3);

	private final int value;

	private EnumTipoMotivoAtendimentoWEB(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}

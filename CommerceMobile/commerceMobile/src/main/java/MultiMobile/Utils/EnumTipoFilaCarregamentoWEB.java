package MultiMobile.Utils;

public enum EnumTipoFilaCarregamentoWEB {
	Reversa(1), Vazio(2);

	private final int value;

	private EnumTipoFilaCarregamentoWEB(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}

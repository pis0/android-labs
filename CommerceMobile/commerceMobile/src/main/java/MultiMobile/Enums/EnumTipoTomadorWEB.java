package MultiMobile.Enums;

public enum EnumTipoTomadorWEB {
	Remetente(0), Expedidor(1), Recebedor(2), Destinatario(3), Outros(4), Intermediario(
			5);

	private final int value;

	private EnumTipoTomadorWEB(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}

package MultiMobile.Utils;

public enum EnumSituacaoFilaCarregamentoWEB {
	NaFila(1), EmTransicao(2), Removido(3), EmViagem(4), Disponivel(5), EmRemocao(6);

	private final int value;

	private EnumSituacaoFilaCarregamentoWEB(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
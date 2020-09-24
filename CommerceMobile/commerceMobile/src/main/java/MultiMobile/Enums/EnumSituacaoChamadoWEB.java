package MultiMobile.Enums;

public enum EnumSituacaoChamadoWEB {
	Aberto(1), Finalizado(2), SemRegra(3), LiberadaOcorrencia(4), Cancelada(5), LiberadaValePallet(
			6);

	private final int value;

	private EnumSituacaoChamadoWEB(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}

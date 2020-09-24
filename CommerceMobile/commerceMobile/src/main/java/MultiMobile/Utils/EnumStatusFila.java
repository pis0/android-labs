package MultiMobile.Utils;

public enum EnumStatusFila {
	Disponivel(1), NaFila(2), EmReversa(3), NaFilaAgCarga(4), EmViagem(5), CargaRecusada(6), AgRemocao(7);

	private final int value;

	private EnumStatusFila(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static String DescricaoStatusFila(int status) {
		switch (status) {
		case 1:
			return "Disponível";
		case 2:
			return "Na Fila";
		case 3:
			return "Em Reversa";
		case 4:
			return "Na Fila - Ag. Carga";
		case 5:
			return "Em Viagem";
		case 6:
			return "Carga Recusada";
		case 7:
			return "Aguardando Remoção";
		default:
			return "";
		}
	}
}
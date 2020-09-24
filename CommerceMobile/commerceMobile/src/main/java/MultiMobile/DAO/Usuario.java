package MultiMobile.DAO;

import org.kroz.activerecord.ActiveRecordBase;

public class Usuario extends ActiveRecordBase {
	public int id_commerce;
	public String usuario;
	public String senha;
	public boolean manter_logado;
	public String dia;
	public String mes;
	public String ano;
	public String hora;
	public String minuto;
	public String segundo;

	public String nome_funcionario;
	public String placa;
	public int status_fila;
	public int posicao_fila;
	public String local_fila;
	public String transportadora;
	public String url_embarcador;
	public String link_video;

	@Override
	public String toString() {
		return "";
	}

	public Usuario() {

	}

	public Usuario(int id_commerce, String usuario, String senha,
			boolean manter_logado, String nome_funcionario, String placa,
			int status_fila, int posicao_fila, String local_fila,
			String transportadora, String url_embarcador, String link_video) {
		super();
		this.id_commerce = id_commerce;
		this.usuario = usuario;
		this.senha = senha;
		this.manter_logado = manter_logado;

		this.nome_funcionario = nome_funcionario;
		this.placa = placa;
		this.status_fila = status_fila;
		this.posicao_fila = posicao_fila;
		this.local_fila = local_fila;
		this.transportadora = transportadora;
		this.url_embarcador = url_embarcador;
		this.link_video = link_video;
	}

	public int getId_commerce() {
		return id_commerce;
	}

	public void setId_commerce(int id_commerce) {
		this.id_commerce = id_commerce;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public boolean isManter_logado() {
		return manter_logado;
	}

	public void setManter_logado(boolean manter_logado) {
		this.manter_logado = manter_logado;
	}

	public String getDia() {
		return dia;
	}

	public void setDia(String dia) {
		this.dia = dia;
	}

	public String getMes() {
		return mes;
	}

	public void setMes(String mes) {
		this.mes = mes;
	}

	public String getAno() {
		return ano;
	}

	public void setAno(String ano) {
		this.ano = ano;
	}

	public String getHora() {
		return hora;
	}

	public void setHora(String hora) {
		this.hora = hora;
	}

	public String getMinuto() {
		return minuto;
	}

	public void setMinuto(String minuto) {
		this.minuto = minuto;
	}

	public String getSegundo() {
		return segundo;
	}

	public void setSegundo(String segundo) {
		this.segundo = segundo;
	}

	public String getNome_funcionario() {
		return nome_funcionario;
	}

	public void setNome_funcionario(String nome_funcionario) {
		this.nome_funcionario = nome_funcionario;
	}

	public String getPlaca() {
		return placa;
	}

	public void setPlaca(String placa) {
		this.placa = placa;
	}

	public int getStatus_fila() {
		return status_fila;
	}

	public void setStatus_fila(int status_fila) {
		this.status_fila = status_fila;
	}

	public int getPosicao_fila() {
		return posicao_fila;
	}

	public void setPosicao_fila(int posicao_fila) {
		this.posicao_fila = posicao_fila;
	}

	public String getLocal_fila() {
		return local_fila;
	}

	public void setLocal_fila(String local_fila) {
		this.local_fila = local_fila;
	}

	public String getTransportadora() {
		return transportadora;
	}

	public void setTransportadora(String transportadora) {
		this.transportadora = transportadora;
	}

	public String getUrl_embarcador() {
		return url_embarcador;
	}

	public void setUrl_embarcador(String url_embarcador) {
		this.url_embarcador = url_embarcador;
	}

	public String getLink_video() {
		return link_video;
	}

	public void setLink_video(String link_video) {
		this.link_video = link_video;
	}
}

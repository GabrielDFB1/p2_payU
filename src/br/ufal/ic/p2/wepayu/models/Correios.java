package br.ufal.ic.p2.wepayu.models;

public class Correios extends MetodoPagamento {
	@Override
	public String getNome() {
		return "correios";
	}

	@Override
	public String descricao(Empregado empregado) {
		return "Correios, " + empregado.getEndereco();
	}
}

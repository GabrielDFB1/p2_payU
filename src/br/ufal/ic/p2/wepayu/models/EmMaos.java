package br.ufal.ic.p2.wepayu.models;

public class EmMaos extends MetodoPagamento {
	@Override
	public String getNome() {
		return "emMaos";
	}

	@Override
	public String descricao(Empregado empregado) {
		return "Em maos";
	}
}

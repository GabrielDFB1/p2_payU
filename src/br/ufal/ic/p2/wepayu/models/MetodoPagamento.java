package br.ufal.ic.p2.wepayu.models;

public abstract class MetodoPagamento {
	public abstract String getNome();

	public abstract String descricao(Empregado empregado);
}

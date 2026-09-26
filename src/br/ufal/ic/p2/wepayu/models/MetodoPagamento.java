package br.ufal.ic.p2.wepayu.models;

public abstract class MetodoPagamento {
    public abstract String nome();

    public abstract String descricao(String endereco);
}

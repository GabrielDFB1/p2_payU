package br.ufal.ic.p2.wepayu.models;

public class Correios extends MetodoPagamento {
    @Override
    public String nome() {
        return "correios";
    }

    @Override
    public String descricao(String endereco) {
        return "Correios, " + endereco;
    }
}

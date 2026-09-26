package br.ufal.ic.p2.wepayu.models;

public class EmMaos extends MetodoPagamento {
    @Override
    public String nome() {
        return "emMaos";
    }

    @Override
    public String descricao(String endereco) {
        return "Em maos";
    }
}

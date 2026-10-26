package br.ufal.ic.p2.wepayu.models;

import java.util.List;

import br.ufal.ic.p2.wepayu.Exception.AtributoInvalidoEmpregadoException;
import br.ufal.ic.p2.wepayu.models.CartaoDePonto;

public class Horista extends Empregado{
	private List<CartaoDePonto> cartao;
	
	public Horista() {
		super();
	}
	public Horista(String nome, String endereco, String salario)
			throws AtributoInvalidoEmpregadoException {
		super(nome, endereco, salario);
	}
	
	@Override
	public String getTipo() {
		return "horista";
	}

}

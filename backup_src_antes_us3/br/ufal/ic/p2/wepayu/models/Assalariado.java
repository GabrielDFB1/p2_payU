package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.AtributoInvalidoEmpregadoException;

public class Assalariado extends Empregado {

	public Assalariado() {
		super();
	}
	public Assalariado(String nome, String endereco, String salario) throws AtributoInvalidoEmpregadoException {
		super(nome, endereco, salario);
	}
	
	@Override
	public String getTipo() {
		return "assalariado";
	}
}

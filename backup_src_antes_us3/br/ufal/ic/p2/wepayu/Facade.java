package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.models.Folha;

public class Facade {
	private Folha folhaPagamento = new Folha();
	
	
	
	public void zerarSistema() {
		this.folhaPagamento.zerar();
	}
	
	public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws Exception{
		return this.folhaPagamento.criarEmpregado(nome, endereco, tipo, salario);
	}
	
	public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws Exception {
		return this.folhaPagamento.criarEmpregado(nome, endereco, tipo, salario, comissao);
	}
	
	public String getAtributoEmpregado(String emp, String atributo) throws Exception {
		return this.folhaPagamento.getAtributoEmpregado(emp, atributo);
	}
	
	public String getEmpregadoPorNome(String nome, String indice) throws Exception{
		return this.folhaPagamento.getEmpregadoPorNome(nome, Integer.parseInt(indice));
	}
	
	public void removerEmpregado(String id) throws Exception {
		this.folhaPagamento.removerEmpregado(id);
	}
	public void encerrarSistema() throws Exception {
		this.folhaPagamento.salvar();
	}
	
	
}

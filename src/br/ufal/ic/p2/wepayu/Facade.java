package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.models.Folha;

public class Facade {
	private Folha folhaPagamento = new Folha();

	public void zerarSistema() throws Exception {
		folhaPagamento.zerar();
	}

	public void encerrarSistema() throws Exception {
		folhaPagamento.encerrarSistema();
	}

	public void undo() throws Exception {
		folhaPagamento.undo();
	}

	public void redo() throws Exception {
		folhaPagamento.redo();
	}

	public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws Exception {
		return folhaPagamento.criarEmpregado(nome, endereco, tipo, salario);
	}

	public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao)
			throws Exception {
		return folhaPagamento.criarEmpregado(nome, endereco, tipo, salario, comissao);
	}

	public void removerEmpregado(String id) throws Exception {
		folhaPagamento.removerEmpregado(id);
	}

	public String getAtributoEmpregado(String emp, String atributo) throws Exception {
		return folhaPagamento.getAtributoEmpregado(emp, atributo);
	}

	public String getEmpregadoPorNome(String nome, String indice) throws Exception {
		return folhaPagamento.getEmpregadoPorNome(nome, indice);
	}

	public String getNumeroDeEmpregados() {
		return folhaPagamento.getNumeroDeEmpregados();
	}

	public void alteraEmpregado(String emp, String atributo, String valor) throws Exception {
		folhaPagamento.alteraEmpregado(emp, atributo, valor);
	}

	public void alteraEmpregado(String emp, String atributo, String valor, String comissaoOuSalario)
			throws Exception {
		folhaPagamento.alteraEmpregado(emp, atributo, valor, comissaoOuSalario);
	}

	public void alteraEmpregado(String emp, String atributo, String valor, String idSindicato,
			String taxaSindical) throws Exception {
		folhaPagamento.alteraEmpregado(emp, atributo, valor, idSindicato, taxaSindical);
	}

	public void alteraEmpregado(String emp, String atributo, String valor, String banco, String agencia,
			String contaCorrente) throws Exception {
		folhaPagamento.alteraEmpregado(emp, atributo, valor, banco, agencia, contaCorrente);
	}

	public void lancaCartao(String emp, String data, String horas) throws Exception {
		folhaPagamento.lancaCartao(emp, data, horas);
	}

	public String getHorasNormaisTrabalhadas(String emp, String dataInicial, String dataFinal) throws Exception {
		return folhaPagamento.getHorasNormaisTrabalhadas(emp, dataInicial, dataFinal);
	}

	public String getHorasExtrasTrabalhadas(String emp, String dataInicial, String dataFinal) throws Exception {
		return folhaPagamento.getHorasExtrasTrabalhadas(emp, dataInicial, dataFinal);
	}

	public void lancaVenda(String emp, String data, String valor) throws Exception {
		folhaPagamento.lancaVenda(emp, data, valor);
	}

	public String getVendasRealizadas(String emp, String dataInicial, String dataFinal) throws Exception {
		return folhaPagamento.getVendasRealizadas(emp, dataInicial, dataFinal);
	}

	public void lancaTaxaServico(String membro, String data, String valor) throws Exception {
		folhaPagamento.lancaTaxaServico(membro, data, valor);
	}

	public String getTaxasServico(String emp, String dataInicial, String dataFinal) throws Exception {
		return folhaPagamento.getTaxasServico(emp, dataInicial, dataFinal);
	}

	public String totalFolha(String data) throws Exception {
		return folhaPagamento.totalFolha(data);
	}

	public void rodaFolha(String data, String saida) throws Exception {
		folhaPagamento.rodaFolha(data, saida);
	}

	public void criarAgendaDePagamentos(String descricao) throws Exception {
		folhaPagamento.criarAgendaDePagamentos(descricao);
	}
}

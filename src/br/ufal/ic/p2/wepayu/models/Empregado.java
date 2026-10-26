package br.ufal.ic.p2.wepayu.models;

import java.time.LocalDate;
import java.util.Locale;

import br.ufal.ic.p2.wepayu.Exception.AtributoInvalidoEmpregadoException;
import br.ufal.ic.p2.wepayu.Exception.TipoEmpregadoInvalidoException;
import br.ufal.ic.p2.wepayu.Exception.ValorInvalidoException;
import br.ufal.ic.p2.wepayu.Exception.WePayUException;
import br.ufal.ic.p2.wepayu.utils.Intervalo;
import br.ufal.ic.p2.wepayu.utils.NumeroUtil;

public abstract class Empregado {
	private String nome;
	private String endereco;
	private double salario;

	private MembroSindicato membroSindicato;
	private MetodoPagamento metodoPagamento = new EmMaos();
	private String agendaPagamento;

	public Empregado() {
	}

	public Empregado(String nome, String endereco, String salario) throws WePayUException {
		this.nome = validarNome(nome);
		this.endereco = validarEndereco(endereco);
		this.salario = converterSalario(salario);
		this.agendaPagamento = getAgendaPadrao();
	}

	public abstract String getTipo();

	public abstract String getAgendaPadrao();

	public abstract Contracheque calcularPagamento(LocalDate data, AgendaPagamento agenda);

	public static String validarNome(String nome) throws AtributoInvalidoEmpregadoException {
		if (nome == null || nome.isBlank()) {
			throw new AtributoInvalidoEmpregadoException("Nome nao pode ser nulo.");
		}
		return nome;
	}

	public static String validarEndereco(String endereco) throws AtributoInvalidoEmpregadoException {
		if (endereco == null || endereco.isBlank()) {
			throw new AtributoInvalidoEmpregadoException("Endereco nao pode ser nulo.");
		}
		return endereco;
	}

	public static double converterSalario(String salario) throws ValorInvalidoException {
		return NumeroUtil.converterNaoNegativo(salario, "Salario nao pode ser nulo.",
				"Salario deve ser numerico.", "Salario deve ser nao-negativo.");
	}

	public String getAtributo(String atributo) throws WePayUException {
		switch (atributo) {
		case "nome":
			return getNome();
		case "endereco":
			return getEndereco();
		case "tipo":
			return getTipo();
		case "salario":
			return getSalarioFormatado();
		case "sindicalizado":
			return getSindicalizadoFormatado();
		case "comissao":
			throw new TipoEmpregadoInvalidoException("Empregado nao eh comissionado.");
		case "metodoPagamento":
			return metodoPagamento.getNome();
		case "banco":
			return getBanco().getBanco();
		case "agencia":
			return getBanco().getAgencia();
		case "contaCorrente":
			return getBanco().getContaCorrente();
		case "idSindicato":
			return getMembroOuErro().getIdMembro();
		case "taxaSindical":
			return NumeroUtil.formatarDinheiro(getMembroOuErro().getTaxaSindical());
		case "agendaPagamento":
			return agendaPagamento;
		default:
			throw new AtributoInvalidoEmpregadoException("Atributo nao existe.");
		}
	}

	public void alterarAtributo(String atributo, String valor) throws WePayUException {
		switch (atributo) {
		case "nome":
			setNome(validarNome(valor));
			break;
		case "endereco":
			setEndereco(validarEndereco(valor));
			break;
		case "salario":
			setSalario(converterSalario(valor));
			break;
		case "comissao":
			throw new TipoEmpregadoInvalidoException("Empregado nao eh comissionado.");
		case "metodoPagamento":
			if ("emMaos".equals(valor)) {
				setMetodoPagamento(new EmMaos());
			} else if ("correios".equals(valor)) {
				setMetodoPagamento(new Correios());
			} else {
				throw new ValorInvalidoException("Metodo de pagamento invalido.");
			}
			break;
		default:
			throw new AtributoInvalidoEmpregadoException("Atributo nao existe.");
		}
	}

	public void copiarDadosDe(Empregado outro) {
		this.nome = outro.nome;
		this.endereco = outro.endereco;
		this.salario = outro.salario;
		this.membroSindicato = outro.membroSindicato;
		this.metodoPagamento = outro.metodoPagamento;
	}

	public void lancaCartao(String data, String horas) throws WePayUException {
		throw new TipoEmpregadoInvalidoException("Empregado nao eh horista.");
	}

	public String getHorasNormaisTrabalhadas(String dataInicial, String dataFinal) throws WePayUException {
		throw new TipoEmpregadoInvalidoException("Empregado nao eh horista.");
	}

	public String getHorasExtrasTrabalhadas(String dataInicial, String dataFinal) throws WePayUException {
		throw new TipoEmpregadoInvalidoException("Empregado nao eh horista.");
	}

	public void lancaVenda(String data, String valor) throws WePayUException {
		throw new TipoEmpregadoInvalidoException("Empregado nao eh comissionado.");
	}

	public String getVendasRealizadas(String dataInicial, String dataFinal) throws WePayUException {
		throw new TipoEmpregadoInvalidoException("Empregado nao eh comissionado.");
	}

	public String getTaxasServico(String dataInicial, String dataFinal) throws WePayUException {
		MembroSindicato membro = getMembroOuErro();
		return NumeroUtil.formatarDinheiro(membro.totalTaxasServico(Intervalo.de(dataInicial, dataFinal)));
	}

	public boolean isSindicalizado() {
		return membroSindicato != null;
	}

	private MembroSindicato getMembroOuErro() throws TipoEmpregadoInvalidoException {
		if (membroSindicato == null) {
			throw new TipoEmpregadoInvalidoException("Empregado nao eh sindicalizado.");
		}
		return membroSindicato;
	}

	private Banco getBanco() throws TipoEmpregadoInvalidoException {
		if (!(metodoPagamento instanceof Banco)) {
			throw new TipoEmpregadoInvalidoException("Empregado nao recebe em banco.");
		}
		return (Banco) metodoPagamento;
	}

	public String getSalarioFormatado() {
		return String.format(Locale.forLanguageTag("pt-BR"), "%.2f", salario);
	}

	public String getSindicalizadoFormatado() {
		return String.valueOf(isSindicalizado());
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public double getSalario() {
		return salario;
	}

	public void setSalario(double salario) {
		this.salario = salario;
	}

	public MembroSindicato getMembroSindicato() {
		return membroSindicato;
	}

	public void setMembroSindicato(MembroSindicato membroSindicato) {
		this.membroSindicato = membroSindicato;
	}

	public MetodoPagamento getMetodoPagamento() {
		return metodoPagamento;
	}

	public void setMetodoPagamento(MetodoPagamento metodoPagamento) {
		this.metodoPagamento = metodoPagamento;
	}

	public String getAgendaPagamento() {
		return agendaPagamento;
	}

	public void setAgendaPagamento(String agendaPagamento) {
		this.agendaPagamento = agendaPagamento;
	}
}

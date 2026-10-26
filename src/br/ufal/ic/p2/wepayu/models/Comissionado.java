package br.ufal.ic.p2.wepayu.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.ufal.ic.p2.wepayu.Exception.ValorInvalidoException;
import br.ufal.ic.p2.wepayu.Exception.WePayUException;
import br.ufal.ic.p2.wepayu.utils.DataUtil;
import br.ufal.ic.p2.wepayu.utils.Intervalo;
import br.ufal.ic.p2.wepayu.utils.NumeroUtil;

public class Comissionado extends Empregado {
	private double comissao;
	private List<ResultadoVenda> vendas = new ArrayList<>();

	public Comissionado() {
		super();
	}

	public Comissionado(String nome, String endereco, String salario, String comissao) throws WePayUException {
		super(nome, endereco, salario);
		this.comissao = converterComissao(comissao);
	}

	public static double converterComissao(String comissao) throws ValorInvalidoException {
		return NumeroUtil.converterNaoNegativo(comissao, "Comissao nao pode ser nula.",
				"Comissao deve ser numerica.", "Comissao deve ser nao-negativa.");
	}

	@Override
	public String getTipo() {
		return "comissionado";
	}

	@Override
	public String getAgendaPadrao() {
		return AgendaPagamento.QUINZENAL;
	}

	@Override
	public String getAtributo(String atributo) throws WePayUException {
		if (atributo.equals("comissao")) {
			return getComissaoFormatado();
		}
		return super.getAtributo(atributo);
	}

	@Override
	public void alterarAtributo(String atributo, String valor) throws WePayUException {
		if (atributo.equals("comissao")) {
			setComissao(converterComissao(valor));
		} else {
			super.alterarAtributo(atributo, valor);
		}
	}

	@Override
	public void lancaVenda(String data, String valor) throws WePayUException {
		LocalDate dia = DataUtil.converter(data, "Data invalida.");
		double v = NumeroUtil.converterPositivo(valor, "Valor deve ser numerico.", "Valor deve ser positivo.");
		vendas.add(new ResultadoVenda(dia, v));
	}

	@Override
	public String getVendasRealizadas(String dataInicial, String dataFinal) throws WePayUException {
		return NumeroUtil.formatarDinheiro(totalVendas(Intervalo.de(dataInicial, dataFinal)));
	}

	private BigDecimal totalVendas(Intervalo intervalo) {
		BigDecimal total = BigDecimal.ZERO;
		for (ResultadoVenda venda : vendas) {
			if (intervalo.contem(venda.getDataLocal())) {
				total = total.add(NumeroUtil.decimal(venda.getValor()));
			}
		}
		return total;
	}

	@Override
	public Contracheque calcularPagamento(LocalDate data, AgendaPagamento agenda) {
		Intervalo periodo = Intervalo.periodoDePagamento(agenda.pagamentoAnterior(data), data);
		BigDecimal fixo = agenda.salarioFixoDoPeriodo(getSalario());
		BigDecimal totalVendas = totalVendas(periodo);
		BigDecimal valorComissao = NumeroUtil.truncar(totalVendas.multiply(NumeroUtil.decimal(comissao)));

		Contracheque contracheque = new Contracheque(this);
		contracheque.setFixo(fixo);
		contracheque.setVendas(totalVendas);
		contracheque.setComissao(valorComissao);
		contracheque.setBruto(fixo.add(valorComissao));
		return contracheque;
	}

	public String getComissaoFormatado() {
		return String.format(Locale.forLanguageTag("pt-BR"), "%.2f", comissao);
	}

	public double getComissao() {
		return comissao;
	}

	public void setComissao(double comissao) {
		this.comissao = comissao;
	}

	public List<ResultadoVenda> getVendas() {
		return vendas;
	}

	public void setVendas(List<ResultadoVenda> vendas) {
		this.vendas = vendas;
	}
}

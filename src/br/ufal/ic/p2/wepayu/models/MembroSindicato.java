package br.ufal.ic.p2.wepayu.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.ufal.ic.p2.wepayu.Exception.WePayUException;
import br.ufal.ic.p2.wepayu.utils.DataUtil;
import br.ufal.ic.p2.wepayu.utils.Intervalo;
import br.ufal.ic.p2.wepayu.utils.NumeroUtil;

public class MembroSindicato {
	private String idMembro;
	private double taxaSindical;
	private List<TaxaServico> taxasServico = new ArrayList<>();

	private double divida;

	public MembroSindicato() {
	}

	public MembroSindicato(String idMembro, double taxaSindical) {
		this.idMembro = idMembro;
		this.taxaSindical = taxaSindical;
	}

	public void lancaTaxaServico(String data, String valor) throws WePayUException {
		LocalDate dia = DataUtil.converter(data, "Data invalida.");
		double v = NumeroUtil.converterPositivo(valor, "Valor deve ser numerico.", "Valor deve ser positivo.");
		taxasServico.add(new TaxaServico(dia, v));
	}

	public BigDecimal totalTaxasServico(Intervalo intervalo) {
		BigDecimal total = BigDecimal.ZERO;
		for (TaxaServico taxa : taxasServico) {
			if (intervalo.contem(taxa.getDataLocal())) {
				total = total.add(NumeroUtil.decimal(taxa.getValor()));
			}
		}
		return total;
	}

	public BigDecimal descontosDevidos(Intervalo periodo, long diasDoPeriodo) {
		return NumeroUtil.decimal(taxaSindical).multiply(BigDecimal.valueOf(diasDoPeriodo))
				.add(totalTaxasServico(periodo))
				.add(NumeroUtil.decimal(divida));
	}

	public String getIdMembro() {
		return idMembro;
	}

	public void setIdMembro(String idMembro) {
		this.idMembro = idMembro;
	}

	public double getTaxaSindical() {
		return taxaSindical;
	}

	public void setTaxaSindical(double taxaSindical) {
		this.taxaSindical = taxaSindical;
	}

	public List<TaxaServico> getTaxasServico() {
		return taxasServico;
	}

	public void setTaxasServico(List<TaxaServico> taxasServico) {
		this.taxasServico = taxasServico;
	}

	public double getDivida() {
		return divida;
	}

	public void setDivida(double divida) {
		this.divida = divida;
	}
}

package br.ufal.ic.p2.wepayu.models;

import java.math.BigDecimal;

public class Contracheque {
	private final Empregado empregado;
	private BigDecimal horasNormais = BigDecimal.ZERO;
	private BigDecimal horasExtras = BigDecimal.ZERO;
	private BigDecimal fixo = BigDecimal.ZERO;
	private BigDecimal vendas = BigDecimal.ZERO;
	private BigDecimal comissao = BigDecimal.ZERO;
	private BigDecimal bruto = BigDecimal.ZERO;
	private BigDecimal descontos = BigDecimal.ZERO;

	public Contracheque(Empregado empregado) {
		this.empregado = empregado;
	}

	public BigDecimal getLiquido() {
		return bruto.subtract(descontos);
	}

	public Empregado getEmpregado() {
		return empregado;
	}

	public BigDecimal getHorasNormais() {
		return horasNormais;
	}

	public void setHorasNormais(BigDecimal horasNormais) {
		this.horasNormais = horasNormais;
	}

	public BigDecimal getHorasExtras() {
		return horasExtras;
	}

	public void setHorasExtras(BigDecimal horasExtras) {
		this.horasExtras = horasExtras;
	}

	public BigDecimal getFixo() {
		return fixo;
	}

	public void setFixo(BigDecimal fixo) {
		this.fixo = fixo;
	}

	public BigDecimal getVendas() {
		return vendas;
	}

	public void setVendas(BigDecimal vendas) {
		this.vendas = vendas;
	}

	public BigDecimal getComissao() {
		return comissao;
	}

	public void setComissao(BigDecimal comissao) {
		this.comissao = comissao;
	}

	public BigDecimal getBruto() {
		return bruto;
	}

	public void setBruto(BigDecimal bruto) {
		this.bruto = bruto;
	}

	public BigDecimal getDescontos() {
		return descontos;
	}

	public void setDescontos(BigDecimal descontos) {
		this.descontos = descontos;
	}
}

package br.ufal.ic.p2.wepayu.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.ufal.ic.p2.wepayu.Exception.WePayUException;
import br.ufal.ic.p2.wepayu.utils.DataUtil;
import br.ufal.ic.p2.wepayu.utils.Intervalo;
import br.ufal.ic.p2.wepayu.utils.NumeroUtil;

public class Horista extends Empregado {
	private static final BigDecimal JORNADA_DIARIA = BigDecimal.valueOf(8);
	private static final BigDecimal ADICIONAL_HORA_EXTRA = new BigDecimal("1.5");

	private List<CartaoDePonto> cartoes = new ArrayList<>();

	public Horista() {
		super();
	}

	public Horista(String nome, String endereco, String salario) throws WePayUException {
		super(nome, endereco, salario);
	}

	@Override
	public String getTipo() {
		return "horista";
	}

	@Override
	public String getAgendaPadrao() {
		return AgendaPagamento.SEMANAL;
	}

	@Override
	public void lancaCartao(String data, String horas) throws WePayUException {
		LocalDate dia = DataUtil.converter(data, "Data invalida.");
		double h = NumeroUtil.converterPositivo(horas, "Horas devem ser numericas.", "Horas devem ser positivas.");
		cartoes.add(new CartaoDePonto(dia, h));
	}

	@Override
	public String getHorasNormaisTrabalhadas(String dataInicial, String dataFinal) throws WePayUException {
		return NumeroUtil.formatarHoras(horasNormais(Intervalo.de(dataInicial, dataFinal)));
	}

	@Override
	public String getHorasExtrasTrabalhadas(String dataInicial, String dataFinal) throws WePayUException {
		return NumeroUtil.formatarHoras(horasExtras(Intervalo.de(dataInicial, dataFinal)));
	}

	private BigDecimal horasNormais(Intervalo intervalo) {
		BigDecimal total = BigDecimal.ZERO;
		for (CartaoDePonto cartao : cartoes) {
			if (intervalo.contem(cartao.getDataLocal())) {
				total = total.add(NumeroUtil.decimal(cartao.getHoras()).min(JORNADA_DIARIA));
			}
		}
		return total;
	}

	private BigDecimal horasExtras(Intervalo intervalo) {
		BigDecimal total = BigDecimal.ZERO;
		for (CartaoDePonto cartao : cartoes) {
			if (intervalo.contem(cartao.getDataLocal())) {
				BigDecimal extra = NumeroUtil.decimal(cartao.getHoras()).subtract(JORNADA_DIARIA);
				total = total.add(extra.max(BigDecimal.ZERO));
			}
		}
		return total;
	}

	@Override
	public Contracheque calcularPagamento(LocalDate data, AgendaPagamento agenda) {
		Intervalo periodo = Intervalo.periodoDePagamento(agenda.pagamentoAnterior(data), data);
		BigDecimal normais = horasNormais(periodo);
		BigDecimal extras = horasExtras(periodo);
		BigDecimal valorHora = NumeroUtil.decimal(getSalario());
		BigDecimal bruto = normais.multiply(valorHora)
				.add(extras.multiply(valorHora).multiply(ADICIONAL_HORA_EXTRA));

		Contracheque contracheque = new Contracheque(this);
		contracheque.setHorasNormais(normais);
		contracheque.setHorasExtras(extras);
		contracheque.setBruto(NumeroUtil.truncar(bruto));
		return contracheque;
	}

	public List<CartaoDePonto> getCartoes() {
		return cartoes;
	}

	public void setCartoes(List<CartaoDePonto> cartoes) {
		this.cartoes = cartoes;
	}
}

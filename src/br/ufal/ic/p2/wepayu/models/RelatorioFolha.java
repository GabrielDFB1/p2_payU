package br.ufal.ic.p2.wepayu.models;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import br.ufal.ic.p2.wepayu.utils.NumeroUtil;

public class RelatorioFolha {
	private static final String LINHA = "=".repeat(127);

	private static final String CABECALHO_HORISTAS =
			"Nome                                 Horas Extra Salario Bruto Descontos Salario Liquido Metodo";
	private static final String SEPARADOR_HORISTAS =
			"==================================== ===== ===== ============= ========= =============== ======================================";
	private static final String CABECALHO_ASSALARIADOS =
			"Nome                                             Salario Bruto Descontos Salario Liquido Metodo";
	private static final String SEPARADOR_ASSALARIADOS =
			"================================================ ============= ========= =============== ======================================";
	private static final String CABECALHO_COMISSIONADOS =
			"Nome                  Fixo     Vendas   Comissao Salario Bruto Descontos Salario Liquido Metodo";
	private static final String SEPARADOR_COMISSIONADOS =
			"===================== ======== ======== ======== ============= ========= =============== ======================================";

	public static void gerar(String arquivo, LocalDate data, List<Contracheque> horistas,
			List<Contracheque> assalariados, List<Contracheque> comissionados, BigDecimal totalFolha) throws IOException {
		try (PrintWriter out = new PrintWriter(new FileWriter(arquivo))) {
			String titulo = "FOLHA DE PAGAMENTO DO DIA " + data;
			out.println(titulo);
			out.println("=".repeat(titulo.length()));
			out.println();

			escreverHoristas(out, horistas);
			escreverAssalariados(out, assalariados);
			escreverComissionados(out, comissionados);

			out.println("TOTAL FOLHA: " + NumeroUtil.formatarDinheiro(totalFolha));
		}
	}

	private static void escreverTituloSecao(PrintWriter out, String nome) {
		out.println(LINHA);
		String inicio = "===================== " + nome + " ";
		out.println(inicio + "=".repeat(LINHA.length() - inicio.length()));
		out.println(LINHA);
	}

	private static void escreverHoristas(PrintWriter out, List<Contracheque> lista) {
		escreverTituloSecao(out, "HORISTAS");
		out.println(CABECALHO_HORISTAS);
		out.println(SEPARADOR_HORISTAS);
		BigDecimal horas = BigDecimal.ZERO, extras = BigDecimal.ZERO;
		Totais t = new Totais();
		for (Contracheque c : lista) {
			out.println(String.format("%-36s %5s %5s %13s %9s %15s %s", nome(c, 36),
					NumeroUtil.formatarHoras(c.getHorasNormais()), NumeroUtil.formatarHoras(c.getHorasExtras()),
					dinheiro(c.getBruto()), dinheiro(c.getDescontos()), dinheiro(c.getLiquido()), metodo(c)));
			horas = horas.add(c.getHorasNormais());
			extras = extras.add(c.getHorasExtras());
			t.somar(c);
		}
		out.println();
		out.println(String.format("%-36s %5s %5s %13s %9s %15s", "TOTAL HORISTAS",
				NumeroUtil.formatarHoras(horas), NumeroUtil.formatarHoras(extras),
				dinheiro(t.bruto), dinheiro(t.descontos), dinheiro(t.liquido)));
		out.println();
	}

	private static void escreverAssalariados(PrintWriter out, List<Contracheque> lista) {
		escreverTituloSecao(out, "ASSALARIADOS");
		out.println(CABECALHO_ASSALARIADOS);
		out.println(SEPARADOR_ASSALARIADOS);
		Totais t = new Totais();
		for (Contracheque c : lista) {
			out.println(String.format("%-48s %13s %9s %15s %s", nome(c, 48),
					dinheiro(c.getBruto()), dinheiro(c.getDescontos()), dinheiro(c.getLiquido()), metodo(c)));
			t.somar(c);
		}
		out.println();
		out.println(String.format("%-48s %13s %9s %15s", "TOTAL ASSALARIADOS",
				dinheiro(t.bruto), dinheiro(t.descontos), dinheiro(t.liquido)));
		out.println();
	}

	private static void escreverComissionados(PrintWriter out, List<Contracheque> lista) {
		escreverTituloSecao(out, "COMISSIONADOS");
		out.println(CABECALHO_COMISSIONADOS);
		out.println(SEPARADOR_COMISSIONADOS);
		BigDecimal fixo = BigDecimal.ZERO, vendas = BigDecimal.ZERO, comissao = BigDecimal.ZERO;
		Totais t = new Totais();
		for (Contracheque c : lista) {
			out.println(String.format("%-21s %8s %8s %8s %13s %9s %15s %s", nome(c, 21),
					dinheiro(c.getFixo()), dinheiro(c.getVendas()), dinheiro(c.getComissao()),
					dinheiro(c.getBruto()), dinheiro(c.getDescontos()), dinheiro(c.getLiquido()), metodo(c)));
			fixo = fixo.add(c.getFixo());
			vendas = vendas.add(c.getVendas());
			comissao = comissao.add(c.getComissao());
			t.somar(c);
		}
		out.println();
		out.println(String.format("%-21s %8s %8s %8s %13s %9s %15s", "TOTAL COMISSIONADOS",
				dinheiro(fixo), dinheiro(vendas), dinheiro(comissao),
				dinheiro(t.bruto), dinheiro(t.descontos), dinheiro(t.liquido)));
		out.println();
	}

	private static String nome(Contracheque c, int largura) {
		String nome = c.getEmpregado().getNome();
		return nome.length() > largura ? nome.substring(0, largura) : nome;
	}

	private static String metodo(Contracheque c) {
		Empregado e = c.getEmpregado();
		return e.getMetodoPagamento().descricao(e);
	}

	private static String dinheiro(BigDecimal valor) {
		return NumeroUtil.formatarDinheiro(valor);
	}

	private static class Totais {
		BigDecimal bruto = BigDecimal.ZERO, descontos = BigDecimal.ZERO, liquido = BigDecimal.ZERO;

		void somar(Contracheque c) {
			bruto = bruto.add(c.getBruto());
			descontos = descontos.add(c.getDescontos());
			liquido = liquido.add(c.getLiquido());
		}
	}
}

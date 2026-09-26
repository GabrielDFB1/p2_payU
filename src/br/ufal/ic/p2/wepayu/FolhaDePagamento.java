package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.utils.Conversor;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class FolhaDePagamento {
    private static final String LINHA = "===============================================================================================================================";

    private LocalDate data;
    private List<Empregado> empregados = new ArrayList<>();

    public FolhaDePagamento(Collection<Empregado> todos, LocalDate data) {
        this.data = data;
        for (Empregado empregado : todos) {
            if (empregado.recebeEm(data)) {
                empregados.add(empregado);
            }
        }
        empregados.sort(Comparator.comparing(Empregado::getNome));
    }

    public BigDecimal total() {
        BigDecimal total = BigDecimal.ZERO;
        for (Empregado empregado : empregados) {
            total = total.add(empregado.salarioBruto(data));
        }
        return total;
    }

    public void gerar(String arquivo) throws FileNotFoundException {
        try (PrintWriter saida = new PrintWriter(arquivo)) {
            saida.println("FOLHA DE PAGAMENTO DO DIA " + data);
            saida.println("====================================");
            saida.println();
            BigDecimal total = BigDecimal.ZERO;
            total = total.add(escreverHoristas(saida));
            total = total.add(escreverAssalariados(saida));
            total = total.add(escreverComissionados(saida));
            saida.println("TOTAL FOLHA: " + Conversor.formatarValor(total));
        }
    }

    private BigDecimal escreverHoristas(PrintWriter saida) {
        escreverCabecalho(saida, "HORISTAS");
        saida.println("Nome                                 Horas Extra Salario Bruto Descontos Salario Liquido Metodo");
        saida.println("==================================== ===== ===== ============= ========= =============== ======================================");

        double totalHoras = 0;
        double totalExtras = 0;
        BigDecimal[] totais = {BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO};
        for (Empregado empregado : empregados) {
            if (empregado instanceof EmpregadoHorista) {
                EmpregadoHorista horista = (EmpregadoHorista) empregado;
                double horas = horista.horasNormais(horista.inicioPeriodo(data), data.plusDays(1));
                double extras = horista.horasExtras(horista.inicioPeriodo(data), data.plusDays(1));
                BigDecimal[] valores = pagar(horista);
                saida.println(String.format("%-36s %5s %5s %13s %9s %15s %s", horista.getNome(),
                        Conversor.formatarHoras(horas), Conversor.formatarHoras(extras),
                        Conversor.formatarValor(valores[0]), Conversor.formatarValor(valores[1]),
                        Conversor.formatarValor(valores[2]), descricaoMetodo(horista)));
                totalHoras += horas;
                totalExtras += extras;
                somar(totais, valores);
            }
        }
        saida.println();
        saida.println(String.format("%-36s %5s %5s %13s %9s %15s", "TOTAL HORISTAS",
                Conversor.formatarHoras(totalHoras), Conversor.formatarHoras(totalExtras),
                Conversor.formatarValor(totais[0]), Conversor.formatarValor(totais[1]), Conversor.formatarValor(totais[2])));
        saida.println();
        return totais[0];
    }

    private BigDecimal escreverAssalariados(PrintWriter saida) {
        escreverCabecalho(saida, "ASSALARIADOS");
        saida.println("Nome                                             Salario Bruto Descontos Salario Liquido Metodo");
        saida.println("================================================ ============= ========= =============== ======================================");

        BigDecimal[] totais = {BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO};
        for (Empregado empregado : empregados) {
            if (empregado instanceof EmpregadoAssalariado) {
                BigDecimal[] valores = pagar(empregado);
                saida.println(String.format("%-48s %13s %9s %15s %s", empregado.getNome(),
                        Conversor.formatarValor(valores[0]), Conversor.formatarValor(valores[1]),
                        Conversor.formatarValor(valores[2]), descricaoMetodo(empregado)));
                somar(totais, valores);
            }
        }
        saida.println();
        saida.println(String.format("%-48s %13s %9s %15s", "TOTAL ASSALARIADOS",
                Conversor.formatarValor(totais[0]), Conversor.formatarValor(totais[1]), Conversor.formatarValor(totais[2])));
        saida.println();
        return totais[0];
    }

    private BigDecimal escreverComissionados(PrintWriter saida) {
        escreverCabecalho(saida, "COMISSIONADOS");
        saida.println("Nome                  Fixo     Vendas   Comissao Salario Bruto Descontos Salario Liquido Metodo");
        saida.println("===================== ======== ======== ======== ============= ========= =============== ======================================");

        BigDecimal totalFixo = BigDecimal.ZERO;
        BigDecimal totalVendas = BigDecimal.ZERO;
        BigDecimal totalComissao = BigDecimal.ZERO;
        BigDecimal[] totais = {BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO};
        for (Empregado empregado : empregados) {
            if (empregado instanceof EmpregadoComissionado) {
                EmpregadoComissionado comissionado = (EmpregadoComissionado) empregado;
                BigDecimal fixo = comissionado.salarioFixo();
                BigDecimal vendas = comissionado.vendasPeriodo(data);
                BigDecimal comissao = comissionado.comissaoPeriodo(data);
                BigDecimal[] valores = pagar(comissionado);
                saida.println(String.format("%-21s %8s %8s %8s %13s %9s %15s %s", comissionado.getNome(),
                        Conversor.formatarValor(fixo), Conversor.formatarValor(vendas), Conversor.formatarValor(comissao),
                        Conversor.formatarValor(valores[0]), Conversor.formatarValor(valores[1]),
                        Conversor.formatarValor(valores[2]), descricaoMetodo(comissionado)));
                totalFixo = totalFixo.add(fixo);
                totalVendas = totalVendas.add(vendas);
                totalComissao = totalComissao.add(comissao);
                somar(totais, valores);
            }
        }
        saida.println();
        saida.println(String.format("%-21s %8s %8s %8s %13s %9s %15s", "TOTAL COMISSIONADOS",
                Conversor.formatarValor(totalFixo), Conversor.formatarValor(totalVendas), Conversor.formatarValor(totalComissao),
                Conversor.formatarValor(totais[0]), Conversor.formatarValor(totais[1]), Conversor.formatarValor(totais[2])));
        saida.println();
        return totais[0];
    }

    private BigDecimal[] pagar(Empregado empregado) {
        BigDecimal bruto = empregado.salarioBruto(data);
        BigDecimal devido = empregado.descontosDevidos(data);
        BigDecimal descontos = devido.min(bruto);
        empregado.setDebitoSindical(devido.subtract(descontos).doubleValue());
        return new BigDecimal[]{bruto, descontos, bruto.subtract(descontos)};
    }

    private void somar(BigDecimal[] totais, BigDecimal[] valores) {
        for (int i = 0; i < totais.length; i++) {
            totais[i] = totais[i].add(valores[i]);
        }
    }

    private String descricaoMetodo(Empregado empregado) {
        return empregado.getMetodoPagamento().descricao(empregado.getEndereco());
    }

    private void escreverCabecalho(PrintWriter saida, String titulo) {
        String inicio = "===================== " + titulo + " ";
        saida.println(LINHA);
        saida.println(inicio + LINHA.substring(inicio.length()));
        saida.println(LINHA);
    }
}

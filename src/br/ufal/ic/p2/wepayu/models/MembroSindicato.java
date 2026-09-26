package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.utils.Conversor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MembroSindicato {
    private String idMembro;
    private double taxaSindical;
    private List<TaxaServico> taxasServico = new ArrayList<>();

    public MembroSindicato() {
    }

    public MembroSindicato(String idMembro, double taxaSindical) {
        this.idMembro = idMembro;
        this.taxaSindical = taxaSindical;
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

    public double totalTaxasServico(LocalDate inicio, LocalDate fim) {
        BigDecimal total = BigDecimal.ZERO;
        for (TaxaServico taxa : taxasServico) {
            if (Conversor.estaNoPeriodo(taxa.getData(), inicio, fim)) {
                total = total.add(BigDecimal.valueOf(taxa.getValor()));
            }
        }
        return total.doubleValue();
    }
}

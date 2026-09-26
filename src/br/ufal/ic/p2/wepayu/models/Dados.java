package br.ufal.ic.p2.wepayu.models;

import java.util.LinkedHashMap;
import java.util.Map;

public class Dados {
    private Map<String, Empregado> empregados = new LinkedHashMap<>();
    private int proximoId = 1;

    public Map<String, Empregado> getEmpregados() {
        return empregados;
    }

    public void setEmpregados(Map<String, Empregado> empregados) {
        this.empregados = empregados;
    }

    public int getProximoId() {
        return proximoId;
    }

    public void setProximoId(int proximoId) {
        this.proximoId = proximoId;
    }
}

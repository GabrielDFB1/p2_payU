package br.ufal.ic.p2.wepayu.models;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

import br.ufal.ic.p2.wepayu.models.Empregado;

public class Persistencia {
    private static final String ARQUIVO = "folha.xml";

    public static void salvar(Map<String, Empregado> empregados) throws IOException {
        try (XMLEncoder encoder = new XMLEncoder(
                new BufferedOutputStream(new FileOutputStream(ARQUIVO)))) {
            encoder.writeObject(empregados);
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Empregado> carregar() throws IOException {
        File arquivo = new File(ARQUIVO);
        if (!arquivo.exists()) {
            return new LinkedHashMap<>();
        }
        try (XMLDecoder decoder = new XMLDecoder(
                new BufferedInputStream(new FileInputStream(arquivo)))) {
            return (Map<String, Empregado>) decoder.readObject();
        }
    }
}
package br.ufal.ic.p2.wepayu.utils;

import br.ufal.ic.p2.wepayu.models.Dados;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;

public class Persistencia {
    private static final String ARQUIVO = "dados.xml";

    public static Dados carregar() {
        File arquivo = new File(ARQUIVO);
        if (!arquivo.exists()) {
            return new Dados();
        }
        try (XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(arquivo)))) {
            return (Dados) decoder.readObject();
        } catch (Exception e) {
            return new Dados();
        }
    }

    public static void salvar(Dados dados) {
        try (XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(ARQUIVO)))) {
            encoder.writeObject(dados);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

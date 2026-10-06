package trabalho.threads;

import java.util.ArrayList;
import java.util.List;


public final class Podio {
    private final List<String> ordemChegada = new ArrayList<>();

    public void registrarChegada(String nomeDoCarro) {
        synchronized (ordemChegada) {
            ordemChegada.add(nomeDoCarro);
            int posicao = ordemChegada.size();
            System.out.printf(
                    "[CHEGADA] %s cruzou a linha em %dº lugar!%n",
                    nomeDoCarro,
                    posicao);
        }
    }

    public List<String> getClassificacao() {
        synchronized (ordemChegada) {
            return List.copyOf(ordemChegada);
        }
    }
}

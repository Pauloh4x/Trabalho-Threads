package trabalho.threads;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;


public final class Corrida {
    public static final double DISTANCIA_TOTAL = 1_000.0;
    public static final int NUMERO_DE_CARROS = 5;

    private Corrida() {

    }

    public static void main(String[] args) {
        Podio podio = new Podio();
        CountDownLatch sinalLargada = new CountDownLatch(1);
        List<Thread> threads = new ArrayList<>(NUMERO_DE_CARROS);

        for (int i = 1; i <= NUMERO_DE_CARROS; i++) {
            boolean fazPitStop = i % 2 == 0;
            Carro carro = new Carro(
                    "Carro " + i,
                    DISTANCIA_TOTAL,
                    podio,
                    sinalLargada,
                    fazPitStop);

            Thread thread = new Thread(carro, "Thread-Carro-" + i);
            threads.add(thread);
        }

        System.out.printf(
                "Corrida pronta: %d carros, distancia de %.0f metros.%n",
                NUMERO_DE_CARROS,
                DISTANCIA_TOTAL);

        for (Thread thread : threads) {
            thread.start();
        }

        System.out.println("LARGADA!");
        sinalLargada.countDown();

        if (!aguardarTermino(threads)) {
            return;
        }

        exibirClassificacao(podio.getClassificacao());
    }

    private static boolean aguardarTermino(List<Thread> threads) {
        try {
            for (Thread thread : threads) {
                thread.join();
            }
            return true;
        } catch (InterruptedException e) {
            threads.forEach(Thread::interrupt);
            Thread.currentThread().interrupt();
            System.err.println("A thread principal foi interrompida.");
            return false;
        }
    }

    private static void exibirClassificacao(List<String> classificacao) {
        System.out.println();
        System.out.println("=== CLASSIFICACAO FINAL ===");
        for (int i = 0; i < classificacao.size(); i++) {
            System.out.printf("%dº lugar: %s%n", i + 1, classificacao.get(i));
        }
    }
}

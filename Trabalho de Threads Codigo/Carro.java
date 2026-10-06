package trabalho.threads;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;


public final class Carro implements Runnable {
    private static final double AVANCO_MINIMO = 20.0;
    private static final double AVANCO_MAXIMO = 60.0;
    private static final int PAUSA_MINIMA_MS = 100;
    private static final int PAUSA_MAXIMA_MS = 500;
    private static final int PIT_STOP_MINIMO_MS = 800;
    private static final int PIT_STOP_MAXIMO_MS = 1_400;

    private final String nome;
    private final double distanciaTotalCorrida;
    private final Podio podio;
    private final CountDownLatch sinalLargada;
    private final boolean deveFazerPitStop;

    private volatile double distanciaPercorrida;
    private boolean pitStopRealizado;

    public Carro(
            String nome,
            double distanciaTotalCorrida,
            Podio podio,
            CountDownLatch sinalLargada,
            boolean deveFazerPitStop) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do carro e obrigatorio.");
        }
        if (distanciaTotalCorrida <= 0) {
            throw new IllegalArgumentException("A distancia total deve ser positiva.");
        }
        if (podio == null || sinalLargada == null) {
            throw new IllegalArgumentException("Podio e sinal de largada sao obrigatorios.");
        }

        this.nome = nome;
        this.distanciaTotalCorrida = distanciaTotalCorrida;
        this.podio = podio;
        this.sinalLargada = sinalLargada;
        this.deveFazerPitStop = deveFazerPitStop;
    }

    @Override
    public void run() {
        try {
            sinalLargada.await();

            while (!concluiuCorrida()) {
                double avancou = avancar();
                exibirProgresso(avancou);

                if (!concluiuCorrida()) {
                    realizarPitStopSeNecessario();
                    Thread.sleep(ThreadLocalRandom.current()
                            .nextInt(PAUSA_MINIMA_MS, PAUSA_MAXIMA_MS + 1));
                }
            }

            podio.registrarChegada(nome);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.printf("[INTERROMPIDO] %s saiu da corrida.%n", nome);
        }
    }

    private double avancar() {
        double sorteado = ThreadLocalRandom.current()
                .nextDouble(AVANCO_MINIMO, AVANCO_MAXIMO + 1.0);
        double restante = distanciaTotalCorrida - distanciaPercorrida;
        double avancou = Math.min(sorteado, restante);
        distanciaPercorrida += avancou;
        return avancou;
    }

    private void exibirProgresso(double avancou) {
        System.out.printf(
                "%s andou %.0f metros e ja percorreu %.0f de %.0f metros.%n",
                nome,
                avancou,
                distanciaPercorrida,
                distanciaTotalCorrida);
    }

    private void realizarPitStopSeNecessario() throws InterruptedException {
        boolean passouDaMetade = distanciaPercorrida >= distanciaTotalCorrida / 2.0;

        if (deveFazerPitStop && passouDaMetade && !pitStopRealizado) {
            pitStopRealizado = true;
            int duracao = ThreadLocalRandom.current()
                    .nextInt(PIT_STOP_MINIMO_MS, PIT_STOP_MAXIMO_MS + 1);
            System.out.printf("[PIT STOP] %s parou por %d ms.%n", nome, duracao);
            Thread.sleep(duracao);
        }
    }

    private boolean concluiuCorrida() {
        return distanciaPercorrida >= distanciaTotalCorrida;
    }

    public String getNome() {
        return nome;
    }

    public double getDistanciaPercorrida() {
        return distanciaPercorrida;
    }
}

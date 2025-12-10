package iskahoot.server;

public class ModifiedCountdownLatch {

    private int count;
    private int bonusCount;
    private final int bonusFactor;
    private boolean bonusActive = true;
    private final Runnable finishAction; // Ação a executar quando termina

    public ModifiedCountdownLatch(int bonusFactor,
                                  int bonusCount,
                                  int waitPeriod,
                                  int count,
                                  Runnable finishAction) { // Novo parâmetro

        this.count = count;
        this.bonusCount = bonusCount;
        this.bonusFactor = bonusFactor;
        this.finishAction = finishAction;

        Thread timer = new Thread(() -> {
            try {
                Thread.sleep(waitPeriod);
                synchronized (this) {
                    bonusActive = false;
                }
            } catch (InterruptedException ignored) {
            }
        });
        timer.setDaemon(true);
        timer.start();
    }

    public synchronized int countdown() {
        if (count <= 0) return 1;

        count--;
        int factor = 1;

        if (bonusActive && bonusCount > 0) {
            bonusCount--;
            factor = bonusFactor;
        }

        if (count == 0) {
            // Quando todos respondem, corre a ação (Next Question)
            if (finishAction != null) finishAction.run();
            notifyAll();
        }

        return factor;
    }

    public synchronized void await() throws InterruptedException {
        while (count > 0) {
            wait();
        }
    }
}
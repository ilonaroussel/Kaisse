package org.example.kaisse.model;

public class Chronometer extends Thread {
    private int remainingTime = 1500; // 25 minutes en secondes

    @Override
    public void run() {
        while (remainingTime > 0) {
            try {
                Thread.sleep(1000); // Attendre 1 seconde
            } catch (InterruptedException e) {
                break;
            }

            synchronized (this) {
                remainingTime--; // Décrémenter chaque seconde
            }
        }
    }

    public synchronized int getRemainingTime() {
        return remainingTime; // Retourne le temps restant
    }
}

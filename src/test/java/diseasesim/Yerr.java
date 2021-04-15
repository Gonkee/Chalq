package diseasesim;

import java.util.concurrent.Semaphore;

public class Yerr extends Thread {

    public static void main(String[] args) {
        new Yerr();
    }

    Semaphore s1 = new Semaphore(0);
    Semaphore s2 = new Semaphore(0);
    int i = 0;
//    boolean next;


    public Yerr() {
        start();

        s1.release();
        while(true) {
            try {
                s1.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("main thread: " + i);
            s2.release();

//            next = true;
//            System.out.println("main: " + next);

        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                s2.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            System.out.println("alt:  " + next);
//            if (!next) continue;

            System.out.println("alt thread:  " + i);

//            next = false;
            i++;

            s1.release();
        }
    }
}

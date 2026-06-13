import java.util.concurrent.Semaphore;

public class WriterPreference {

    private static final Semaphore mutex_rc = new Semaphore(1);
    private static final Semaphore mutex_wc = new Semaphore(1);
    private static final Semaphore readTry = new Semaphore(1);
    private static final Semaphore rw = new Semaphore(1);

    private static int rc = 0;
    private static int wc = 0;

    static class Reader extends Thread {

        int id;

        public Reader(int id) {
            this.id = id;
        }

        @Override
        public void run() {

            try {

                while (true) {

                    readTry.acquire();

                    mutex_rc.acquire();

                    rc++;

                    if (rc == 1)
                        rw.acquire();

                    mutex_rc.release();

                    readTry.release();

                    System.out.println("Reader " + id + " is reading.");

                    Thread.sleep(1000);

                    mutex_rc.acquire();

                    rc--;

                    if (rc == 0)
                        rw.release();

                    mutex_rc.release();

                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    static class Writer extends Thread {

        int id;

        public Writer(int id) {
            this.id = id;
        }

        @Override
        public void run() {

            try {

                while (true) {

                    mutex_wc.acquire();

                    wc++;

                    if (wc == 1)
                        readTry.acquire();

                    mutex_wc.release();

                    rw.acquire();

                    System.out.println("Writer " + id + " is writing.");

                    Thread.sleep(1500);

                    rw.release();

                    mutex_wc.acquire();

                    wc--;

                    if (wc == 0)
                        readTry.release();

                    mutex_wc.release();

                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    public static void main(String[] args) {

        new Reader(1).start();
        new Reader(2).start();

        new Writer(1).start();

        new Reader(3).start();

        new Writer(2).start();

    }

}
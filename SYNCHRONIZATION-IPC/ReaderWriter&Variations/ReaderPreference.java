import java.util.concurrent.Semaphore;

public class ReaderPreference {
    private static final Semaphore mutex_rc = new Semaphore(1);
    private static final Semaphore rw = new Semaphore(1);

    private static int rc = 0; // reader count

    static class Reader extends Thread {
        int id;

        public Reader(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    mutex_rc.acquire();
                    rc++;
                    if (rc == 1)
                        rw.acquire();
                    mutex_rc.release();
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
                    rw.acquire();
                    System.out.println("Writer " + id + " is writing.");
                    Thread.sleep(1500);
                    rw.release();
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
    }
}
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class ProducerConsumerSempahore {
    private static final int BUFFER_SIZE = 5;
    private static final Queue<Integer> buffer = new LinkedList<>();

    private static final Semaphore empty = new Semaphore(BUFFER_SIZE);
    private static final Semaphore full = new Semaphore(0);
    private static final Semaphore mutex = new Semaphore(1);

    static class Producer extends Thread {

        @Override
        public void run() {
            int value = 1;
            while (true) {
                try {
                    empty.acquire();
                    mutex.acquire();

                    buffer.add(value);
                    System.out.println("Produced: " + value);

                    value++;
                    mutex.release();
                    full.release();

                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class Consumer extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    full.acquire();
                    mutex.acquire();

                    int item = buffer.remove();
                    System.out.println("Consumed: " + item);

                    mutex.release();
                    empty.release();

                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) {
        Producer producer = new Producer();
        Consumer consumer = new Consumer();

        producer.start();
        consumer.start();
    }
}

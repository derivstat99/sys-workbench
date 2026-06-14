import java.util.concurrent.Semaphore;

public class DiningPhilosophersDijkstra {
    static final int N = 5;

    enum State {
        THINKING,
        HUNGRY,
        EATING
    }

    static State[] state = new State[N];
    static Semaphore mutex = new Semaphore(1);
    static Semaphore[] S = new Semaphore[N];

    static {
        for (int i = 0; i < N; i++) {
            state[i] = State.THINKING;
            S[i] = new Semaphore(0);
        }
    }

    static int left(int i) {
        return (i + N - 1) % N;
    }

    static int right(int i) {
        return (i + 1) % N;
    }

    static void test(int i) {
        if (state[i] == State.HUNGRY && state[right(i)] != State.EATING
                && state[left(i)] != State.EATING) {
            state[i] = State.EATING;
            System.out.println("Philosopher " + i + " is allowed to eat.");
            S[i].release();
        }
    }

    static void takeFork(int i) {
        try {
            mutex.acquire();
            state[i] = State.HUNGRY;
            System.out.println("Philosopher " + i + " is hungry.");
            test(i);
            mutex.release();
            S[i].acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static void putFork(int i) {
        try {
            mutex.acquire();
            state[i] = State.THINKING;
            System.out.println("Philosopher " + i + " puts down forks");
            test(left(i));
            test(right(i));
            mutex.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class Philosopher extends Thread {
        int id;

        public Philosopher(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            while (true) {
                System.out.println("Philosopher " + id + " is thinking");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                takeFork(id);
                System.out.println("Philosopher " + id + " is eating");
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                putFork(id);
            }
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < N; i++) {
            new Philosopher(i).start();
        }
    }
}

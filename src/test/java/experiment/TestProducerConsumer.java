package experiment;

import java.util.Objects;

import org.junit.Test;

public class TestProducerConsumer {

    static class Producer implements Runnable {
        Object lock;
        Producer(Object lock) {
            this.lock = lock;
        }
        @Override
        public void run() {
        }
    }
    static class Consumer implements Runnable {
        Object lock;
        Consumer(Object lock) {
            this.lock = lock;
        }
        @Override
        public void run() {
        }
    }

    static class Buffer<T> {
        T element = null;
        boolean filled = false;
        boolean terminate = false;

        private synchronized void putInternal(T e) {
            while (filled) {
                try {
                    System.out.println("wait for put");
                    wait();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
            filled = true;
            element = e;
            notifyAll();
        }

        public void put(T e) {
            Objects.requireNonNull(e);
            putInternal(e);
        }

        public synchronized T get() {
            while (!filled) {
                try {
                    System.out.println("wait for get");
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            filled = false;
            T r = element;
            notifyAll();
            return r;
        }

        synchronized void close() {
            putInternal(null);
        }
    }

    @Test
    public void test() throws InterruptedException {
        Buffer<String> buffer = new Buffer<>();
        String[] arpha = {"a", "b", "c", "d"};
        Runnable producerAlpha = () -> {
            for (String e : arpha)
                buffer.put(e);
            buffer.close();
        };
        String[] number = {"1", "2", "3"};
        Runnable producerNumber = () -> {
            for (String e : number)
                buffer.put(e);
            buffer.close();
        };
        Thread t = new Thread(producerAlpha);
        t.start();
        String e;
        while ((e = buffer.get()) != null)
            System.out.println(e);
        t.join();
    }

}

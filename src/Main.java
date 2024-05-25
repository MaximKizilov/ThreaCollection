import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    static ArrayBlockingQueue<String> a = new ArrayBlockingQueue<>(100);
    static ArrayBlockingQueue<String> b = new ArrayBlockingQueue<>(100);
    static ArrayBlockingQueue<String> c = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(1000);
        Runnable runnableTask = () -> {
            for (int i = 0; i < 100; i++) {
                a.offer(generateText("abc", 100));
                b.offer(generateText("abc", 100));
                c.offer(generateText("abc", 100));

            }
        };

        Future<?> task = executor.submit(runnableTask);
        task.get();
        executor.shutdown();
        Thread aThread = counter(a, 'a');
        Thread bThread = counter(b, 'b');
        Thread cThread = counter(c, 'c');
        aThread.start();
        bThread.start();
        cThread.start();
        aThread.join();
        bThread.join();
        cThread.join();

    }

    public static Thread counter(ArrayBlockingQueue<String> queue, char c) {
        AtomicInteger count = new AtomicInteger();
        AtomicInteger maxValue = new AtomicInteger();
        return new Thread(() -> {
            try {
                String text = queue.take();
                for (char cc : text.toCharArray()) {
                    if (cc == c) {
                        count.getAndIncrement();
                    }
                }
                if (count.get() > maxValue.get()) {
                    maxValue.set(count.get());
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Max quantity of " + c + " is " + maxValue);
        });

    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}

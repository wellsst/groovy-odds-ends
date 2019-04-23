package threads

import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor


//ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool()
ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10, new ThreadFactory() {
    @Override
    Thread newThread(Runnable r) {
        return new Thread(r, "Dyson")
    }
})

for (int i = 0; i < 10; i++) {
    SimpleTask task = new SimpleTask(i, 1000*(10-i))
    executor.execute(task)

}

executor.shutdown()



public class SimpleTask implements Runnable {

    private long sleepTime
    int id

    public SimpleTask(int id, long sleepTime) {
        super()
        this.sleepTime = sleepTime
        this.id = id
    }

    @Override
    public void run() {
        try {
            println("Simple task START ${id} is running on " + Thread.currentThread().getName() + " with priority " + Thread.currentThread().getPriority())
            Thread.sleep(sleepTime)
            println("Simple task DONE ${id} is running on " + Thread.currentThread().getName() + " with priority " + Thread.currentThread().getPriority())
        } catch (InterruptedException e) {
            e.printStackTrace()
        }
    }

}
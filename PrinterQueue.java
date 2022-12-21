import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class PrinterQueue implements IMPMCQueue<PrintItem>
{
    // TODO: This is all yours
    private int maxQueueSize;
    private PriorityBlockingQueue<PrintItem> priorityBlockingQueue;

    public PrinterQueue(int maxElementCount)
    {
        // TODO: Implement

        // You can change this signature but also don't forget to
        // change the instantiation signature on the
        // Printer room
        this.maxQueueSize = maxElementCount;
        PrintItemComparator printItemComparator = new PrintItemComparator();
        this.priorityBlockingQueue = new PriorityBlockingQueue<>(maxQueueSize, printItemComparator);
    }

    public void Add(PrintItem data) throws QueueIsClosedExecption {
        lock.lock();
        try {
            while (priorityBlockingQueue.size() == maxQueueSize) {
                full.signalAll();                       // did not add wait yet
            }
            priorityBlockingQueue.add(data);
            notEmpty.signalAll();                          // signal or signalAll?
        } finally {
            lock.unlock();
        }
    }

    public PrintItem Consume() throws QueueIsClosedExecption {
        lock.lock();
        PrintItem printItem = priorityBlockingQueue.poll();
        try {
            if (priorityBlockingQueue.size() == 0) {
                empty.signalAll();
            }
        } finally {
            lock.unlock();
        }
        return printItem;
    }

    public int RemainingSize() {
        return priorityBlockingQueue.size();
    }

    public void CloseQueue() {
        lock.lock();
        try {
            queueClosed.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public int getMaxQueueSize() {
        return maxQueueSize;
    }

    public PriorityBlockingQueue<PrintItem> getPriorityBlockingQueue() {
        return priorityBlockingQueue;
    }

    @Override
    public Lock getLock() {
        return lock;
    }

    @Override
    public Condition getNotEmpty() {
        return notEmpty;
    }

    @Override
    public Condition getEmpty() {
        return empty;
    }

    @Override
    public Condition getFull() {
        return full;
    }

    @Override
    public Condition getRoomClosed() {
        return queueClosed;
    }
}

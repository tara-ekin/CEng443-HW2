import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public interface IMPMCQueue<T>
{
    Lock lock = new ReentrantLock();
    Condition notEmpty = lock.newCondition();
    Condition empty = lock.newCondition();
    Condition full = lock.newCondition();
    Condition notFull = lock.newCondition();
    Condition queueClosed = lock.newCondition();

    Lock getLock();
    Condition getNotEmpty();
    Condition getEmpty();
    Condition getFull();
    Condition getNotFull();
    Condition getRoomClosed();

    public int getMaxQueueSize();
    /**
     * Adds data to the queue, waits if queue is full
     *
     * @param data data to be added into the queue
     * @throws QueueIsClosedExecption when adding operation is suspended.
     */
    void Add(T data) throws QueueIsClosedExecption;

    /**
     * Consumes the first element with respect to the priority of the type T,
     * waits until an element is available in the queue
     *
     * In this HW, if there is an instructor print item it should be prioritized
     * from the student print items (but 'first come, first served' is still true internally
     * between students and instructors).
     *
     * @throws QueueIsClosedExecption when there are no elements left on the queue and queue is closed
     *          (with CloseQueue function)
     * @return returns the element
     */
    T Consume() throws QueueIsClosedExecption;

    /**
     * Non-blocking query function, this is technically an approximate value
     * since after function succeeds, another thread may remove/add an item.
     * @retun remaining size
     */
    int RemainingSize();

    /**
     * Notifies every thread that is waiting on this queue (threads that are waiting
     * on the functions will return throw QueueIsClosedExecption as a notification)
     *
     * After this call,
     *   - Consumers should not be terminated until every item on the queue is processed. After that
     *     they should be terminated.
     *   - Queue should not accept any add operations.
     *
     * This function should return only after all elements on the queue are processed
     */
    void CloseQueue();
}

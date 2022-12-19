import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PrinterRoom
{
    private class Printer implements Runnable
    {
        private int id;
        private IMPMCQueue<PrintItem> roomQueue;

        public Printer(int id, IMPMCQueue<PrintItem> roomQueue)
        {
            this.id = id;
            this.roomQueue = roomQueue;
            SyncLogger.Instance().Log(SyncLogger.ThreadType.MAIN_THREAD, this.id,
                    String.format(SyncLogger.FORMAT_PRINTER_LAUNCH, this.id));
        }

        public void run() {
            roomQueue.getLock().lock();
            try {
                while (roomQueue == null || roomQueue.RemainingSize() == 0) {
                    roomQueue.getNotEmpty().await();
                }
                PrintItem printedItem = roomQueue.Consume();
                printedItem.print();
                SyncLogger.Instance().Log(SyncLogger.ThreadType.CONSUMER, printedItem.getId(),
                        String.format(SyncLogger.FORMAT_PRINT_DONE, printedItem));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                roomQueue.getLock().unlock();
            }
        }
    }

    private IMPMCQueue<PrintItem> roomQueue;
    private final List<Printer> printers;
    private final List<Thread> printerThreads = new ArrayList<>();

    public PrinterRoom(int printerCount, int maxElementCount)
    {
        // Instantiating the shared queue
        roomQueue = new PrinterQueue(maxElementCount);

        // Let's try streams
        // Printer creation automatically launches its thread
        printers = Collections.unmodifiableList(IntStream.range(0, printerCount)
                                                         .mapToObj(i -> new Printer(i, roomQueue))
                                                         .collect(Collectors.toList()));
//         Printers are launched using the same queue
        printers.forEach(printer -> printerThreads.add(new Thread(printer)));
        printerThreads.forEach(Thread::start);
    }

    public boolean SubmitPrint(PrintItem item, int producerId)
    {
        // TODO: Implement
        SyncLogger.Instance().Log(SyncLogger.ThreadType.PRODUCER, producerId,
                String.format(SyncLogger.FORMAT_ADD, item));

        roomQueue.Add(item);
        return true;
    }

    public void CloseRoom()
    {
        // TODO: Implement
    }
}

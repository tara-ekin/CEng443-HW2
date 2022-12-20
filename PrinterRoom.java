import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PrinterRoom
{
    private class Printer implements Runnable
    {
        private int id;
        private IMPMCQueue<PrintItem> roomQueue;
        private Boolean exit;

        public Printer(int id, IMPMCQueue<PrintItem> roomQueue)
        {
            this.id = id;
            this.roomQueue = roomQueue;
            exit = false;
            SyncLogger.Instance().Log(SyncLogger.ThreadType.MAIN_THREAD, this.id,
                    String.format(SyncLogger.FORMAT_PRINTER_LAUNCH, this.id));
            Thread t = new Thread(this);
            t.start();
        }

        public void run() {
            while (!exit) {
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

        public void stop() {
            exit = true;
            SyncLogger.Instance().Log(SyncLogger.ThreadType.CONSUMER, this.id,
                    String.format(SyncLogger.FORMAT_TERMINATING));
        }
    }

    private IMPMCQueue<PrintItem> roomQueue;

    public IMPMCQueue<PrintItem> getRoomQueue() {
        return this.roomQueue;
    }
    private final List<Printer> printers;

    public List<Printer> getPrinters() {
        return this.printers;
    }
    private final List<Thread> printerThreads = new ArrayList<>();
    public List<Thread> getPrinterThreads() {
        return this.printerThreads;
    }

    private Boolean roomOpen;

    public PrinterRoom(int printerCount, int maxElementCount)
    {
        roomOpen = true;
        // Instantiating the shared queue
        roomQueue = new PrinterQueue(maxElementCount);

        // Let's try streams
        // Printer creation automatically launches its thread
        printers = Collections.unmodifiableList(IntStream.range(0, printerCount)
                                                         .mapToObj(i -> new Printer(i, roomQueue))
                                                         .collect(Collectors.toList()));
        // Printers are launched using the same queue
        printers.forEach(printer -> printerThreads.add(new Thread(printer)));
    }

    public boolean SubmitPrint(PrintItem item, int producerId)
    {
        // TODO: Implement
        if (roomOpen) {
            SyncLogger.Instance().Log(SyncLogger.ThreadType.PRODUCER, producerId,
                    String.format(SyncLogger.FORMAT_ADD, item));

            roomQueue.Add(item);
            return true;
        } else {
            return false;
        }
    }

    public void CloseRoom()
    {
        // TODO: Implement
        roomOpen = false;
        printers.forEach(Printer::stop);
//        roomQueue.getRoomClosed().signalAll();
    }
}

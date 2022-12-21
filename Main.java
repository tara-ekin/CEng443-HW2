import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main
{
    static class Producer implements Runnable
    {
        private PrinterRoom room;
        private int producerID;
        private PrintItem.PrintType type;

        public Producer(PrinterRoom room, int id, PrintItem.PrintType type)
        {
            this.room = room;
            this.producerID = id;
            this.type = type;
            SyncLogger.Instance().Log(SyncLogger.ThreadType.PRODUCER, producerID,
                    String.format(SyncLogger.FORMAT_PRODUCER_LAUNCH, producerID));
        }

        @Override
        public void run() {
            while (true)
            {
                PrintItem item = new PrintItem(new Random().nextInt(1000), type, producerID);
                if(!room.SubmitPrint(item, producerID))
                {
                    break;
                }
            }
            SyncLogger.Instance().Log(SyncLogger.ThreadType.PRODUCER, producerID,
                    String.format(SyncLogger.FORMAT_TERMINATING, producerID));
        }
    }

    public static void main(String args[]) throws InterruptedException
    {
        PrinterRoom room = new PrinterRoom(2, 8);
        List<Producer> producers = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            PrintItem.PrintType type = (i % 2 == 0) ? PrintItem.PrintType.STUDENT : PrintItem.PrintType.INSTRUCTOR;
            producers.add(new Producer(room, i, type));
        }

        for (Producer p : producers) {
            new Thread(p).start();
        }

        Thread.sleep((long)(3 * 1000));

        SyncLogger.Instance().Log(SyncLogger.ThreadType.MAIN_THREAD, 0,
                "Closing Room");
        room.CloseRoom();

        SyncLogger.Instance().Log(SyncLogger.ThreadType.MAIN_THREAD, 0,
                "Room is Closed");
    }
}

//import java.util.ArrayList;
//import java.util.concurrent.ThreadLocalRandom;
//
//public class Main
//{
//    static class Producer implements Runnable
//    {
//        // TODO: You may want to implement this class to test your code
//        private final int jobsToSubmit;
//        private final int sleepDuration;
//        private final int id;
//        private final float studentJobProbability;
//        private final int jobBaseDurationInMs;
//        private PrinterRoom room;
//
//        private Thread myThread;
//
//        public Producer(PrinterRoom room, int id, int jobCount, int sleepDur, int jobBaseDurationInMs, float studentJobProb){
//            this.room = room;
//            this.id = id;
//            this.jobsToSubmit  = jobCount;
//            this.sleepDuration = sleepDur;
//            this.studentJobProbability = studentJobProb;
//            this.jobBaseDurationInMs = jobBaseDurationInMs;
//
//            myThread = new Thread(this);
//            myThread.start();
//        }
//
//        public void join()
//        {
//            // TODO: Provide a thread join functionality for the main thread
//            try {
//                myThread.join();
//            } catch (InterruptedException e) {
//            }
//        }
//
//        @Override
//        public void run() {
//            int submittedCount = 0;
//
//            try {
//                Thread.sleep((long)(sleepDuration));
//            } catch (InterruptedException e) {}
//
//            while(submittedCount < jobsToSubmit)
//            {
//                float rand = ThreadLocalRandom.current().nextFloat();
//                PrintItem.PrintType type = PrintItem.PrintType.INSTRUCTOR;
//                if(rand < studentJobProbability){
//                    type = PrintItem.PrintType.STUDENT;
//                }
//
//                float perctOffset = ThreadLocalRandom.current().nextFloat();
//                int offset = (int)Math.floor(jobBaseDurationInMs * perctOffset);
//
//                PrintItem itemToSubmit = new PrintItem(jobBaseDurationInMs + offset, type, id);
//                if(room.SubmitPrint(itemToSubmit, id)){
////                   SyncLogger.Instance().Log(SyncLogger.ThreadType.PRODUCER, id, "_dork_ SUBMITTED " + itemToSubmit);
//                    submittedCount++;
//                }
//                else{
//                    // room was closed, decide what to do.
//                    // this basic imp just terminates. can try adding few more for further testing.
//                    SyncLogger.Instance().Log(SyncLogger.ThreadType.PRODUCER, id, String.format(SyncLogger.FORMAT_ROOM_CLOSED, itemToSubmit));
//                    break;
//                }
//                try {
//                    Thread.sleep((long)(sleepDuration));
//                } catch (InterruptedException e) {}
//            }
//
//            SyncLogger.Instance().Log(SyncLogger.ThreadType.PRODUCER, id, "Producer: " + id + " is terminating.");
//        }
//    }
//
//    public static void main(String args[]) throws InterruptedException
//    {
//        PrinterRoom room = new PrinterRoom(5, 5);
//
//        int producerCount = 6;
//        ArrayList<Producer> producers = new ArrayList<>();
//        for(int i = 0; i < producerCount; i +=2){
//            producers.add(new Producer(room, i, 10, 100*(i+1), 1000,0.70f));
//            producers.add(new Producer(room, i+1, 10, 500, 200,0.15f));
//        }
//
//        int roomCloseDelayInSeconds = 15;
//        Thread.sleep((long)(roomCloseDelayInSeconds * 1000));
//
//        // Log before close
//        SyncLogger.Instance().Log(SyncLogger.ThreadType.MAIN_THREAD, 0, "Closing Room");
//        room.CloseRoom();
//
//        for (Producer prod: producers) {
//            prod.join();
//        }
//
//        // original comment: This should print only after all elements are closed (here we wait 3 seconds so it should be immediate)
//        SyncLogger.Instance().Log(SyncLogger.ThreadType.MAIN_THREAD, 0, "Room is Closed");
//    }

//    private void OriginalSubmitRoutine()
//    {
//        while(true)
//        {
//            PrintItem item0 = new PrintItem(100, PrintItem.PrintType.STUDENT, 0);
//            PrintItem item1 = new PrintItem(50, PrintItem.PrintType.INSTRUCTOR,1);
//            PrintItem item2 = new PrintItem(66, PrintItem.PrintType.STUDENT, 2);
//            if(!room.SubmitPrint(item0, 0))
//            {
//                SyncLogger.Instance().Log(SyncLogger.ThreadType.PRODUCER, 0,
//                                          String.format(SyncLogger.FORMAT_ROOM_CLOSED, item0));
//                break;
//            }else{
//                SyncLogger.Instance().Log(SyncLogger.ThreadType.PRODUCER, 0, "_dork_ ADDED " + item0.toString());
//            }
//            if(!room.SubmitPrint(item1, 0))
//            {
//                SyncLogger.Instance().Log(SyncLogger.ThreadType.PRODUCER, 0,
//                                          String.format(SyncLogger.FORMAT_ROOM_CLOSED, item1));
//                break;
//            }else{
//                SyncLogger.Instance().Log(SyncLogger.ThreadType.PRODUCER, 0, "_dork_ ADDED " + item1.toString());
//            }
//            if(!room.SubmitPrint(item2, 0))
//            {
//                SyncLogger.Instance().Log(SyncLogger.ThreadType.PRODUCER, 0,
//                                          String.format(SyncLogger.FORMAT_ROOM_CLOSED, item2));
//                break;
//            }else{
//                SyncLogger.Instance().Log(SyncLogger.ThreadType.PRODUCER, 0, "_dork_ ADDED " + item2.toString());
//            }
//            break;
//        }
//    }
//}

//public class Main
//{
//   static class Producer implements Runnable
//   {
//
//       // TODO: You may want to implement this class to test your code
//       public void run() {
//           System.out.println("Test");
//       }
//
//       public void join()
//       {
//           // TODO: Provide a thread join functionality for the main thread
//           System.out.println("Test");
//       }
//   }
//
//    public static void main(String args[]) throws InterruptedException
//    {
//        PrinterRoom room = new PrinterRoom(2, 8);
//        while(true)
//        {
//            PrintItem item0 = new PrintItem(100, PrintItem.PrintType.STUDENT, 0);
//            PrintItem item1 = new PrintItem(50, PrintItem.PrintType.INSTRUCTOR,1);
//            PrintItem item2 = new PrintItem(66, PrintItem.PrintType.STUDENT, 2);
//            if(!room.SubmitPrint(item0, 0))
//            {
//                SyncLogger.Instance().Log(SyncLogger.ThreadType.PRODUCER, 0,
//                                          String.format(SyncLogger.FORMAT_ROOM_CLOSED, item0));
//                break;
//            }
//            if(!room.SubmitPrint(item1, 0))
//            {
//                SyncLogger.Instance().Log(SyncLogger.ThreadType.PRODUCER, 0,
//                                          String.format(SyncLogger.FORMAT_ROOM_CLOSED, item1));
//                break;
//            }
//            if(!room.SubmitPrint(item2, 0))
//            {
//                SyncLogger.Instance().Log(SyncLogger.ThreadType.PRODUCER, 0,
//                                          String.format(SyncLogger.FORMAT_ROOM_CLOSED, item2));
//                break;
//            }
//            break;
//        }
//
//        // Wait a little we are doing produce on the same thread that will do the close
//        // actual tests won't do this.
//        Thread.sleep((long)(3 * 1000));
//        // Log before close
//        SyncLogger.Instance().Log(SyncLogger.ThreadType.MAIN_THREAD, 0,
//                                  "Closing Room");
//        room.CloseRoom();
//        // This should print only after all elements are closed (here we wait 3 seconds so it should be immediate)
//        SyncLogger.Instance().Log(SyncLogger.ThreadType.MAIN_THREAD, 0,
//                                  "Room is Closed");
//    }
//}
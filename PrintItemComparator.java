import java.util.Comparator;

public class PrintItemComparator implements Comparator<PrintItem> {
    public int compare(PrintItem p1, PrintItem p2) {
        if (p1.getPrintType() == p2.getPrintType()) {
            return 0;
        } else if (p1.getPrintType() == PrintItem.PrintType.INSTRUCTOR) {
            return -1;
        } else {
            return 1;
        }
    }
}

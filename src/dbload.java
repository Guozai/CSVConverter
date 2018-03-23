public class dbload {
    private static int pageSize = 4096;
    private static String fileName = "asic.csv";
    public static void main(String[] args) {
//        if (args[0] == "-p") {
//            pageSize = Integer.parseInt(args[1]);
//            fileName = args[2];
//        }

        HeapFileCreater heapFileCreater = new HeapFileCreater();
        heapFileCreater.setPageSize(pageSize);
        heapFileCreater.setFileName(fileName);
        heapFileCreater.launch();
    }
}

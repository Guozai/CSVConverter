public class dbload {
    private static int pageSize = 4096;
    private static String fileName = "asic.csv";
    public static void main(String[] args) {
//        try {
//            if (args.length == 3) {
//                if (args[0].equals("-p")) {
//                    pageSize = Integer.parseInt(args[1]);
//                    fileName = args[2];
//                } else {
//                    throw new Exception("First argument is not \"-p\"");
//                }
//            } else {
//                throw new Exception("Need 3 arguments: java dbload -p pagesize datafile");
//            }
//        } catch (NumberFormatException e) {
//            System.err.println(e.getClass().getName() + ": " + e.getMessage());
//        } catch (Exception e) {
//            System.err.println(e.getClass().getName() + ": " + e.getMessage());
//        }

        HeapFileCreater heapFileCreater = new HeapFileCreater();
        heapFileCreater.setPageSize(pageSize);
        heapFileCreater.setFileName(fileName);
        heapFileCreater.launch();

//        btest test = new btest();
//        test.launch();
    }
}

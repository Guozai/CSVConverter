public class dbload {
    private static int pageSize = 4096;
    //private static String fileName = "BUSINESS_NAMES_201803.csv";
    private static String fileName = "asic.csv";
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis(); // timer for calculating program execution time

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
        heapFileCreater.setFileIn(fileName);
        heapFileCreater.launch();

//        btest test = new btest();
//        test.launch();

        long stopTime = System.currentTimeMillis();
        double elapsedTime = (stopTime - startTime) / 1000.0; // execution time of the program in seconds
        System.out.println("Execution time:           " + elapsedTime + " seconds");
    }
}

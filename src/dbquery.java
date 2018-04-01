public class dbquery {
    private static String queryKey = "";
    private static int pageSize = 0;

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis(); // timer for calculating program execution time

        try {
            if (args.length == 2) {
                queryKey = args[0];
                pageSize = Integer.parseInt(args[1]);

                HeapSearch query = new HeapSearch(queryKey, pageSize);
                query.launch();

            } else {
                throw new Exception("Need 2 arguments: java dbquery text pagesize");
            }
        } catch (NumberFormatException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        long stopTime = System.currentTimeMillis();
        double elapsedTime = (stopTime - startTime) / 1000.0; // execution time of the program in seconds
        System.out.println("Execution time:           " + elapsedTime + " seconds");
    }
}

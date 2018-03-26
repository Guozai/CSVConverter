public class dbquery {
    private static String queryKey = "RPR COMMUNICATIONS";
    private static int pageSize = 4096;
    public static void main(String[] args) {
        HeapSearch heapSearch = new HeapSearch(queryKey, pageSize);
        heapSearch.launch();

//        rtest test = new rtest(queryKey, pageSize);
//        test.launch();
    }
}

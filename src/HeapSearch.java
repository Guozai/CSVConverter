import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class HeapSearch {
    private final int INT_SIZE = 4;
    private final int LEN_STR_SIZE = 1; // each element has a 1 byte header saving the length of the element
    private final int REG_NAME_SIZE = 20;
    private final int STATUS_SIZE = 15;
    private final int STATE_NUM_SIZE = 20;
    private final int STATE_SIZE = 3;
    private final int ABN_SIZE = 13;
    private final int COMMA_SIZE = 1;
    private final int RECORD_SIZE = REG_NAME_SIZE + STATUS_SIZE + STATE_NUM_SIZE + STATE_SIZE + ABN_SIZE + INT_SIZE * 3 + 60;
    private String queryKey;
    private int pageSize;
    private int lenStr = 0;

    private int pos = 0; // position pointer
    private int numRec = 0; // number of records in the file
    private int numPage = 0;
    // count of record searched
    private int countRec = 0;

    public HeapSearch (String queryKey, int pageSize) {
        this.queryKey = queryKey;
        this.pageSize = pageSize;
    }

    public void launch() {

        // read the source file
        //try (FileInputStream fis = new FileInputStream(new File("heap." + Integer.toString(pageSize)))) {
        try (FileInputStream fis = new FileInputStream(new File("heap.128"))) {
            // allocate a channel to read the file
            FileChannel fc = fis.getChannel();
            // allocate a buffer, size of pageSize
            ByteBuffer buffer = ByteBuffer.allocate(pageSize);
            // read a page of pageSize bytes
            // -1 means eof.
            if (fc.read(buffer) != -1) {
                // accumulate the number of page after read(buf)
                numPage++;
                // flip from filling to emptying
                buffer.flip();

                // get the first 4 bytes of page 1
                if (numPage == 1) {
                    numRec = buffer.getInt();
                    pos += INT_SIZE;
                }

                while (countRec < 2) { // while (countRec < numRec)
                    // moving pointer to the beginning of BN_NAME (col 2)
                    // if position over page size, read another page

                    if (pos + LEN_STR_SIZE > pageSize) {
                        buffer.clear();
                        if (fc.read(buffer) != -1) {
                            buffer.flip();
                            pos = 0; // reset pointer
                        } else
                            break;
                    }
                    if (checkNotNull(buffer)) { // REGISTER_NAME
                        if (pos + REG_NAME_SIZE > pageSize) {
                            buffer.clear();
                            if (fc.read(buffer) != -1) {
                                buffer.flip();
                                pos = 0; // reset pointer
                            } else
                                break;
                        }
                        pos += REG_NAME_SIZE;
                    }
                    System.out.println("regName: " + pos);

                    if (pos + LEN_STR_SIZE > pageSize) {
                        buffer.clear();
                        if (fc.read(buffer) != -1) {
                            buffer.flip();
                            pos = 0; // reset pointer
                        } else
                            break;
                    }
                    // get the length of data element
                    if (getLenStr(buffer)) { // BN_NAME
                        if (pos + lenStr > pageSize) {
                            buffer.clear();
                            if (fc.read(buffer) != -1) {
                                buffer.flip();
                                pos = 0; // reset pointer
                            } else
                                break;
                        }
                        // get BN_NAME
                        String sName = getString(buffer);
                        System.out.println(sName);
                    }
                    System.out.println("bnName: " + pos);

                    // move pointer to next BN_NAME
                    if (pos + LEN_STR_SIZE > pageSize) {
                        buffer.clear();
                        if (fc.read(buffer) != -1) {
                            buffer.flip();
                            pos = 0; // reset pointer
                        } else
                            break;
                    }
                    if (checkNotNull(buffer)) {            // BN_STATUS
                        if (pos + STATUS_SIZE > pageSize) {
                            buffer.clear();
                            if (fc.read(buffer) != -1) {
                                buffer.flip();
                                pos = 0; // reset pointer
                            } else
                                break;
                        }
                        pos += STATUS_SIZE;
                    }

                    for (int j = 0; j < 3; j++) { // BN_REG_DT, BN_CANCEL_DT, BN_RENEW_DT
                        if (pos + INT_SIZE > pageSize) {
                            buffer.clear();
                            if (fc.read(buffer) != -1) {
                                buffer.flip();
                                pos = 0; // reset pointer
                            } else
                                break;
                        }
                        if (checkNotNullDate(buffer)) {
                            pos += INT_SIZE;
                        } else {
                            if (pos + LEN_STR_SIZE > pageSize) {
                                buffer.clear();
                                if (fc.read(buffer) != -1) {
                                    buffer.flip();
                                    pos = 0; // reset pointer
                                } else
                                    break;
                            } else {
                                pos += LEN_STR_SIZE;
                            }
                        }
                    }
                    System.out.println("3 dates after: " + pos);

                    if (pos + LEN_STR_SIZE > pageSize) {
                        buffer.clear();
                        if (fc.read(buffer) != -1) {
                            buffer.flip();
                            pos = 0; // reset pointer
                        } else
                            break;
                    }
                    if (checkNotNull(buffer)) {               // BN_STATE_NUM
                        if (pos + STATE_NUM_SIZE > pageSize) {
                            buffer.clear();
                            if (fc.read(buffer) != -1) {
                                buffer.flip();
                                pos = 0; // reset pointer
                            } else
                                break;
                        }
                        pos += STATE_NUM_SIZE;
                    }
                    System.out.println("State number: " + pos);

                    if (pos + LEN_STR_SIZE > pageSize) {
                        buffer.clear();
                        if (fc.read(buffer) != -1) {
                            buffer.flip();
                            pos = 0; // reset pointer
                        } else
                            break;
                    }
                    if (checkNotNull(buffer)) {           // BN_STATE_OF_REG
                        if (pos + STATE_SIZE > pageSize) {
                            buffer.clear();
                            if (fc.read(buffer) != -1) {
                                buffer.flip();
                                pos = 0; // reset pointer
                            } else
                                break;
                        }
                        pos += STATE_SIZE;
                    }
                    System.out.println("State of reg: " + pos);

                    if (pos + LEN_STR_SIZE > pageSize) {
                        buffer.clear();
                        if (fc.read(buffer) != -1) {
                            buffer.flip();
                            pos = 0; // reset pointer
                        } else
                            break;
                    }
                    if (checkNotNull(buffer)) {         // BN_ABN
                        if (pos + ABN_SIZE > pageSize) {
                            buffer.clear();
                            if (fc.read(buffer) != -1) {
                                buffer.flip();
                                pos = 0; // reset pointer
                            } else
                                break;
                        }
                        pos += ABN_SIZE;
                    }
                    System.out.println("abn: " + pos);

                    if (pos + COMMA_SIZE + RECORD_SIZE > pageSize) {
                        buffer.clear();
                        if (fc.read(buffer) != -1) {
                            buffer.flip();
                            pos = 0; // reset pointer
                        } else
                            break;
                    } else {
                        pos += COMMA_SIZE;
                    }
                    System.out.println("comma: " + pos);

                    countRec++;
                }
                buffer.clear();
            }
            fc.close();
            fis.close();
        } catch (FileNotFoundException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    private boolean getLenStr (ByteBuffer buffer) {
        try {
            lenStr = buffer.get(pos);
            pos += LEN_STR_SIZE;
            if (lenStr == -1) // found null element
                return false;
        } catch (NumberFormatException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return true;
    }

    private boolean checkNotNull (ByteBuffer buffer) {
        return getLenStr(buffer);
    }

    private boolean checkNotNullDate (ByteBuffer buffer) {
        try {
            lenStr = buffer.get(pos);
            if (lenStr == -1) { // found null element
                pos += LEN_STR_SIZE;
                return false;
            }
        } catch (NumberFormatException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return true;
    }

    private String getString(ByteBuffer buffer) {
        byte[] bnName = new byte[lenStr];
        String s = "";
        for (int i = 0; i < lenStr; i++) {
            bnName[i] = buffer.get(pos);
            pos++;
        }
        try {
            s = new String(bnName, "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return s;
    }

    private void showStats(String where, FileChannel fc, Buffer b) throws IOException {
        System.out.println( where +
                " channelPosition: " +
                fc.position() +
                " bufferPosition: " +
                b.position() +
                " limit: " +
                b.limit() +
                " remaining: " +
                b.remaining() +
                " capacity: " +
                b.capacity() );
    }
}

import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class HeapSearch {
    private final int INT_SIZE = 4;
    private final int LEN_STR_SIZE = 1; // each element has a 1 byte header saving the length of the element
    private final int REG_NAME_SIZE = 20;
    private final int BN_NAME_SIZE = 200;
    private final int STATUS_SIZE = 15;
    private final int DATE_SIZE = INT_SIZE * 3;
    private final int STATE_NUM_SIZE = 20;
    private final int STATE_SIZE = 3;
    private final int ABN_SIZE = 13;
    private final int COMMA_SIZE = 1;
    private final int RECORD_SIZE = REG_NAME_SIZE + BN_NAME_SIZE + STATUS_SIZE + STATE_NUM_SIZE + STATE_SIZE + ABN_SIZE + INT_SIZE * 3 + LEN_STR_SIZE * 6;
    private String queryKey;
    private int pageSize;
    private String fileName;
    private int lenStr = 0;

    private int pos = 0; // position pointer
    private int numRec = 0; // number of records in the file
    private int numPage = 0;
    // count of record searched
    private int countRec = 0;

    public HeapSearch (String queryKey, int pageSize) {
        this.queryKey = queryKey;
        this.pageSize = pageSize;
        fileName = "heap." + Integer.toString(pageSize);
    }

    public void launch() {
        System.out.println("------------------------------------------");

        // read the source file
        try (FileInputStream fis = new FileInputStream(new File(fileName))) {
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

                while (countRec < 13) { // while (countRec < numRec)
                    // moving pointer to the beginning of BN_NAME (col 2)
                    // if position over page size, read another page

                    if (pos + LEN_STR_SIZE >= pageSize) {
                        buffer.clear();
                        if (fc.read(buffer) != -1) {
                            buffer.flip();
                            pos = 0; // reset pointer
                        } else
                            break;
                    }
                    if (checkNotNull(buffer)) { // REGISTER_NAME

                        System.out.println("regName LenStr " + pos);
                        System.out.println("regName Length: " + lenStr);

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
                    countRec++;

                    System.out.println("regName: " + pos);

                    if (pos + LEN_STR_SIZE >= pageSize) {
                        buffer.clear();
                        if (fc.read(buffer) != -1) {
                            buffer.flip();
                            pos = 0; // reset pointer
                        } else
                            break;
                    }
                    // get the length of data element
                    if (getLenStr(buffer)) { // BN_NAME

                        System.out.println("bn name LenStr " + pos);
                        System.out.println("bn Name Length: " + lenStr);

                        if (pos + lenStr >= pageSize) {
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
                    countRec++;

                    System.out.println("bnName: " + pos);

                    // move pointer to next BN_NAME
                    if (pos + LEN_STR_SIZE >= pageSize) {
                        buffer.clear();
                        if (fc.read(buffer) != -1) {
                            buffer.flip();
                            pos = 0; // reset pointer
                        } else
                            break;
                    }
                    if (checkNotNull(buffer)) {            // BN_STATUS
                        if (pos + STATUS_SIZE >= pageSize) {
                            buffer.clear();
                            if (fc.read(buffer) != -1) {
                                buffer.flip();
                                pos = 0; // reset pointer
                            } else
                                break;
                        }
                        pos += STATUS_SIZE;
                    }
                    countRec++;

                    System.out.println("status: " + pos);

                    for (int j = 0; j < 3; j++) { // BN_REG_DT, BN_CANCEL_DT, BN_RENEW_DT
                        if (pos + INT_SIZE >= pageSize) {
                            buffer.clear();
                            if (fc.read(buffer) != -1) {
                                buffer.flip();
                                pos = 0; // reset pointer
                            } else
                                break;
                        }
                        if (!checkNotNullDate(buffer)) {
                            if (pos + LEN_STR_SIZE >= pageSize) {
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
                        countRec++;
                    }
                    System.out.println("3 dates after: " + pos);

                    if (pos + LEN_STR_SIZE >= pageSize) {
                        buffer.clear();
                        if (fc.read(buffer) != -1) {
                            buffer.flip();
                            pos = 0; // reset pointer
                        } else
                            break;
                    }
                    if (checkNotNull(buffer)) {               // BN_STATE_NUM
                        if (pos + STATE_NUM_SIZE >= pageSize) {
                            buffer.clear();
                            if (fc.read(buffer) != -1) {
                                buffer.flip();
                                pos = 0; // reset pointer
                            } else
                                break;
                        }
                        pos += STATE_NUM_SIZE;
                    }
                    countRec++;

                    System.out.println("State number: " + pos);

                    if (pos + LEN_STR_SIZE >= pageSize) {
                        buffer.clear();
                        if (fc.read(buffer) != -1) {
                            buffer.flip();
                            pos = 0; // reset pointer
                        } else
                            break;
                    }
                    if (checkNotNull(buffer)) {           // BN_STATE_OF_REG
                        if (pos + STATE_SIZE >= pageSize) {
                            buffer.clear();
                            if (fc.read(buffer) != -1) {
                                buffer.flip();
                                pos = 0; // reset pointer
                            } else
                                break;
                        }
                        pos += STATE_SIZE;
                    }
                    countRec++;

                    System.out.println("State of reg: " + pos);

                    if (pos + LEN_STR_SIZE >= pageSize) {
                        buffer.clear();
                        if (fc.read(buffer) != -1) {
                            buffer.flip();
                            pos = 0; // reset pointer
                        } else
                            break;
                    }
                    if (checkNotNull(buffer)) {         // BN_ABN
                        if (pos + ABN_SIZE >= pageSize) {
                            buffer.clear();
                            if (fc.read(buffer) != -1) {
                                buffer.flip();
                                pos = 0; // reset pointer
                            } else
                                break;
                        }
                        pos += ABN_SIZE;
                    }
                    countRec++;

                    System.out.println("abn: " + pos);

                    if (pos + COMMA_SIZE >= pageSize) {
                        buffer.clear();
                        if (fc.read(buffer) != -1) {
                            buffer.flip();
                            pos = 0; // reset pointer
                        } else
                            break;
                    } else {
                        pos += COMMA_SIZE;
                    }
                    System.out.println("after comma: " + pos);
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
            byte temp = buffer.get(pos);
            pos += LEN_STR_SIZE;
            if ((int)temp == -1) // found null element
                return false;
            else
                lenStr = temp & 0xff;
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
            byte temp = buffer.get(pos);
            if ((int)temp == -1) { // found null element
                pos += LEN_STR_SIZE;
                return false;
            } else
                pos += DATE_SIZE;
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

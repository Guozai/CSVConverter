import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class HeapSearch {
    private final int INT_SIZE = 4;
    private final int LEN_STR_SIZE = 1; // each element has a 1 byte header saving the length of the element
    private final int REG_NAME_SIZE = 20;
    private final int STATUS_SIZE = 15;
    private final int DATE_SIZE = 12;
    private final int STATE_NUM_SIZE = 20;
    private final int STATE_SIZE = 3;
    private final int ABN_SIZE = 13;
    private String queryKey;
    private int pageSize;
    private int lenStr = 0;

    private int pos = 0; // position pointer
    private int numRec = 0; // number of records in the file
    private int numPage = 0;
    // count of record searched; the first record is not in the while loop
    // so the count is initialized as 1 instead of 0
    private int count = 1;

    public HeapSearch (String queryKey, int pageSize) {
        this.queryKey = queryKey;
        this.pageSize = pageSize;
    }

    public void launch() {

        // read the source file
        //try (FileInputStream fis = new FileInputStream(new File("heap." + Integer.toString(pageSize)))) {
        try (FileInputStream fis = new FileInputStream(new File("heap.64"))) {
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

                while (count < 2) {
                    // moving pointer to the beginning of BN_NAME (col 2)
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
                    }

                    // move pointer to next BN_NAME
                    if (pos + LEN_STR_SIZE > pageSize) {
                        buffer.clear();
                        if (fc.read(buffer) != -1) {
                            buffer.flip();
                            pos = 0; // reset pointer
                        } else
                            break;
                    }
                    if (checkNotNull(buffer)) {// BN_STATUS
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

                    if (pos + DATE_SIZE > pageSize) {
                        buffer.clear();
                        if (fc.read(buffer) != -1) {
                            buffer.flip();
                            pos = 0; // reset pointer
                        } else
                            break;
                    }
                    for (int j = 0; j < 3; j++) { // BN_REG_DT, BN_CANCEL_DT, BN_RENEW_DT
                        if (checkNotNullDate(buffer))
                            pos += DATE_SIZE;
                    }

                    if (pos + LEN_STR_SIZE > pageSize) {
                        buffer.clear();
                        if (fc.read(buffer) != -1) {
                            buffer.flip();
                            pos = 0; // reset pointer
                        } else
                            break;
                    }
                    if (checkNotNull(buffer)) { // BN_STATE_NUM
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

                    if (pos + LEN_STR_SIZE > pageSize) {
                        buffer.clear();
                        if (fc.read(buffer) != -1) {
                            buffer.flip();
                            pos = 0; // reset pointer
                        } else
                            break;
                    }
                    if (checkNotNull(buffer)) { // BN_STATE_OF_REG
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

                    if (pos + LEN_STR_SIZE > pageSize) {
                        buffer.clear();
                        if (fc.read(buffer) != -1) {
                            buffer.flip();
                            pos = 0; // reset pointer
                        } else
                            break;
                    }
                    if (checkNotNull(buffer)) { // BN_ABN
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
            lenStr = buffer.get(pos) & 0xff;
            pos += LEN_STR_SIZE;
            if (lenStr == 255)
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
            lenStr = buffer.get(pos) & 0xff;
            if (lenStr == 255) {
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
        System.out.println(s);
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

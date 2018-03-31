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
    private final int EOP_SIZE = 1;
    private final int RECORD_SIZE = REG_NAME_SIZE + BN_NAME_SIZE + STATUS_SIZE + STATE_NUM_SIZE + STATE_SIZE + ABN_SIZE + INT_SIZE * 3 + LEN_STR_SIZE * 6;
    private String queryKey;
    private int pageSize;
    private String fileName;

    private int REC_NUM = 0;

    private int lenStr = 0;
    private int pos = 0; // position pointer
    private int pcol = 0;
    private boolean isEndPage = false;
    private boolean isEndFile = false;
    private int numRec = 0; // number of records in the file
    private int numPage = 0;
    // count of record searched
    private int countRec = 0;
    private int countFound = 0;

    public HeapSearch (String queryKey, int pageSize) {
        this.queryKey = queryKey;
        this.pageSize = pageSize;
        fileName = "heap." + Integer.toString(pageSize);
    }

    public void launch() {
        // read the source file
        try (FileInputStream fis = new FileInputStream(new File(fileName))) {
            // allocate a channel to read the file
            FileChannel fc = fis.getChannel();
            // allocate a buffer, size of pageSize
            ByteBuffer buffer = ByteBuffer.allocate(INT_SIZE);
            // read a page of pageSize bytes
            // -1 means eof.
            if (fc.read(buffer) != -1) {
                // flip from filling to emptying
                buffer.flip();
                REC_NUM = buffer.getInt();
            }
            fc.close();
            fis.close();
        } catch (FileNotFoundException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        System.out.println("numRec: " + REC_NUM);

        // read the source file
        try (FileInputStream fis = new FileInputStream(new File(fileName))) {
            // allocate a channel to read the file
            FileChannel fc = fis.getChannel();
            // allocate a buffer, size of pageSize
            ByteBuffer buffer = ByteBuffer.allocate(pageSize);
            while (countRec < numRec) {
                // read a page of pageSize bytes
                // -1 means eof.
                while (fc.read(buffer) != -1) {
                    // accumulate the number of page after reading a page
                    numPage++;
                    // flip from filling to emptying
                    buffer.flip();
                    // reset pointer
                    pos = 0;

                    // go across numRec (4 bytes)
                    if (numPage == 1) {
                        pos += INT_SIZE;
                    }

                    while (!checkEOPmark(buffer)) {
                        System.out.println("Reach here");

                        // register name
                        pos += REG_NAME_SIZE;
                        // business name
                        pos += LEN_STR_SIZE + getLenStr(buffer);
                        // business status
                        pos += STATUS_SIZE;
                        // BN_REG_DT BN_CANCEL_DT BN_RENEW_DT
                        for (int j = 0; j < 3; j++)
                            pos += DATE_SIZE;
                        // business state number
                                pos += STATE_NUM_SIZE;
                                break;
                            case 7: // state of registration
                                pos += STATE_SIZE;
                                break;
                            case 8: // abn number
                                pos += ABN_SIZE;
                            case 9: // comma
                                pos += COMMA_SIZE;
                            default:
                                break;
                        }
                        // pcol increment
                        if (pcol < 10)
                            pcol++;
                        else
                            pcol = 0;
                    }
                    // numRec increment
                    numRec++;
                    System.out.println("Reach end of page");
                }
                System.out.println("Reach end of file");

//                while (countRec < numRec && !isEndPage) {
//                    if ()
//                    switch (pcol) {
//                        case 0: // register name
//                            if (pos + REG_NAME_SIZE < pageSize) {
//                                if (checkNotNull(buffer))
//                                    pos += REG_NAME_SIZE;
//                                else
//                                    pos += LEN_STR_SIZE;
//                                pcol++; // next column
//                            } else {
//                                isEndPage = true;
//                            }
//                            break;
//                        case 1: // business name
//                            String sName = "";
//                            if (pos + LEN_STR_SIZE + sName.length() < pageSize) {
//                                if (checkNotNull(buffer)) {
//                                    if (pos + INT_SIZE + lenStr < pageSize)
//                                        sName = getString(buffer);
//                                        pos += LEN_STR_SIZE + lenStr;
//                                    else {
//                                        isEndPage = true;
//                                    }
//                                }
//                                else {
//                                    pos += LEN_STR_SIZE;
//                                }
//                                if (sName.equals(queryKey))
//                                    countFound++;
//                                pcol++;
//                            } else {
//                                isEndPage = true;
//                            }
//                            break;
//                        case 2: // business status
//                            if (pos + INT_SIZE + STATUS_SIZE < pageSize) {
//                                if (checkNotNull(buffer))
//                                    pos += INT_SIZE + STATUS_SIZE;
//                                else
//                                    pos += INT_SIZE;
//                                pcol++;
//                            } else {
//                                isEndPage = true;
//                            }
//                            break;
//                        case 3: // BN_REG_DT
//                        case 4: // BN_CANCEL_DT
//                        case 5: // BN_RENEW_DT
//                            if (pos + INT_SIZE < pageSize) {
//                                if (checkNotNull(buffer)) {
//                                    if (pos + DATE_SIZE < pageSize)
//                                        pos += DATE_SIZE;
//                                    else
//                                        isEndPage = true;
//                                } else
//                                    pos += INT_SIZE;
//                                pcol++;
//                            } else {
//                                isEndPage = true;
//                            }
//                            break;
//                        case 6: // business state number
//                            if (pos + INT_SIZE + STATE_NUM_SIZE < pageSize) {
//                                if (checkNotNull(buffer))
//                                    pos += INT_SIZE + STATE_NUM_SIZE;
//                                else
//                                    pos += INT_SIZE;
//                                pcol++;
//                            } else {
//                                isEndPage = true;
//                            }
//                            break;
//                        case 7: // state of registration
//                            if (pos + INT_SIZE + STATE_SIZE < pageSize) {
//                                if (checkNotNull(buffer))
//                                    pos += INT_SIZE + STATE_SIZE;
//                                else
//                                    pos += INT_SIZE;
//                                pcol++;
//                            } else {
//                                isEndPage = true;
//                            }
//                            break;
//                        case 8: // abn number
//                            if (pos + INT_SIZE + ABN_SIZE < pageSize) {
//                                if (checkNotNull(buffer))
//                                    pos += INT_SIZE + ABN_SIZE;
//                                else
//                                    pos += INT_SIZE;
//                                pcol++;
//                            } else {
//                                isEndPage = true;
//                            }
//                            break;
//                        case 9:  // comma
//                            if (pos + COMMA_SIZE < pageSize) {
//                                pos += COMMA_SIZE;
//                                pcol++;
//                            } else {
//                                isEndPage = true;
//                            }
//                            break;
//                        default:
//                            break;
//                    }
//                    if (isEndPage)
//                        break;
//                }
//                // end of page
//                buffer.clear();
//            }








//            if (fc.read(buffer) != -1) {
//                // accumulate the number of page after read(buf)
//                numPage++;
//                // flip from filling to emptying
//                buffer.flip();
//
//                // get the first 4 bytes of page 1
//                if (numPage == 1) {
//                    numRec = buffer.getInt();
//                    pos += INT_SIZE;
//                }
//
//                for (int m = 0; m < 2; m++) {
//                    // moving pointer to the beginning of BN_NAME (col 2)
//                    // if position over page size, read another page
//
//                    if (pos + LEN_STR_SIZE >= pageSize) {
//                        buffer.clear();
//                        if (fc.read(buffer) != -1) {
//                            buffer.flip();
//                            pos = 0; // reset pointer
//                        } else
//                            break;
//                    }
//                    if (checkNotNull(buffer)) { // REGISTER_NAME
//                        if (pos + REG_NAME_SIZE > pageSize) {
//                            buffer.clear();
//                            if (fc.read(buffer) != -1) {
//                                buffer.flip();
//                                pos = 0; // reset pointer
//                            } else
//                                break;
//                        }
//                        pos += REG_NAME_SIZE;
//                    }
//
//                    if (pos + LEN_STR_SIZE >= pageSize) {
//                        buffer.clear();
//                        if (fc.read(buffer) != -1) {
//                            buffer.flip();
//                            pos = 0; // reset pointer
//                        } else
//                            break;
//                    }
//                    // get the length of data element
//                    if (getLenStr(buffer)) { // BN_NAME
//                        if (pos + lenStr >= pageSize) {
//                            buffer.clear();
//                            if (fc.read(buffer) != -1) {
//                                buffer.flip();
//                                pos = 0; // reset pointer
//                            } else
//                                break;
//                        }
//                        // get BN_NAME
//                        String sName = getString(buffer);
//                        System.out.println(sName);
//                    }
//
//                    // move pointer to next BN_NAME
//                    if (pos + LEN_STR_SIZE >= pageSize) {
//                        buffer.clear();
//                        if (fc.read(buffer) != -1) {
//                            buffer.flip();
//                            pos = 0; // reset pointer
//                        } else
//                            break;
//                    }
//                    if (checkNotNull(buffer)) {            // BN_STATUS
//                        if (pos + STATUS_SIZE >= pageSize) {
//                            buffer.clear();
//                            if (fc.read(buffer) != -1) {
//                                buffer.flip();
//                                pos = 0; // reset pointer
//                            } else
//                                break;
//                        }
//                        pos += STATUS_SIZE;
//                    }
//
//                    for (int j = 0; j < 3; j++) { // BN_REG_DT, BN_CANCEL_DT, BN_RENEW_DT
//                        if (pos + INT_SIZE >= pageSize) {
//                            buffer.clear();
//                            if (fc.read(buffer) != -1) {
//                                buffer.flip();
//                                pos = 0; // reset pointer
//                            } else
//                                break;
//                        }
//                        if (!checkNotNullDate(buffer)) {
//                            if (pos + LEN_STR_SIZE >= pageSize) {
//                                buffer.clear();
//                                if (fc.read(buffer) != -1) {
//                                    buffer.flip();
//                                    pos = 0; // reset pointer
//                                } else
//                                    break;
//                            } else {
//                                pos += LEN_STR_SIZE;
//                            }
//                        }
//                    }
//
//                    if (pos + LEN_STR_SIZE >= pageSize) {
//                        buffer.clear();
//                        if (fc.read(buffer) != -1) {
//                            buffer.flip();
//                            pos = 0; // reset pointer
//                        } else
//                            break;
//                    }xzs2
//                    if (checkNotNull(buffer)) {               // BN_STATE_NUM
//                        if (pos + STATE_NUM_SIZE >= pageSize) {
//                            buffer.clear();
//                            if (fc.read(buffer) != -1) {
//                                buffer.flip();
//                                pos = 0; // reset pointer
//                            } else
//                                break;
//                        }
//                        pos += STATE_NUM_SIZE;
//                    }
//
//                    if (pos + LEN_STR_SIZE >= pageSize) {
//                        buffer.clear();
//                        if (fc.read(buffer) != -1) {
//                            buffer.flip();
//                            pos = 0; // reset pointer
//                        } else
//                            break;
//                    }
//                    if (checkNotNull(buffer)) {           // BN_STATE_OF_REG
//                        if (pos + STATE_SIZE >= pageSize) {
//                            buffer.clear();
//                            if (fc.read(buffer) != -1) {
//                                buffer.flip();
//                                pos = 0; // reset pointer
//                            } else
//                                break;
//                        }
//                        pos += STATE_SIZE;
//                    }
//
//                    if (pos + LEN_STR_SIZE >= pageSize) {
//                        buffer.clear();
//                        if (fc.read(buffer) != -1) {
//                            buffer.flip();
//                            pos = 0; // reset pointer
//                        } else
//                            break;
//                    }
//                    if (checkNotNull(buffer)) {         // BN_ABN
//                        if (pos + ABN_SIZE >= pageSize) {
//                            buffer.clear();
//                            if (fc.read(buffer) != -1) {
//                                buffer.flip();
//                                pos = 0; // reset pointer
//                            } else
//                                break;
//                        }
//                        pos += ABN_SIZE;
//                    }
//
//                    if (pos + COMMA_SIZE >= pageSize) {
//                        buffer.clear();
//                        if (fc.read(buffer) != -1) {
//                            buffer.flip();
//                            pos = 0; // reset pointer
//                        } else
//                            break;
//                    } else {
//                        pos += COMMA_SIZE;
//                    }
//                    countRec++;
//                }
//
//                while (countRec < numRec) {
//                    // moving pointer to the beginning of BN_NAME (col 2)
//                    // if position over page size, read another page
//
//                    if (pos + LEN_STR_SIZE >= pageSize) {
//                        buffer.clear();
//                        if (fc.read(buffer) != -1) {
//                            buffer.flip();
//                            pos = 0; // reset pointer
//                        } else
//                            break;
//                    }
//                    if (checkNotNull(buffer)) { // REGISTER_NAME
//
//                        System.out.println("regName LenStr " + pos);
//                        System.out.println("regName Length: " + lenStr);
//
//                        if (pos + REG_NAME_SIZE > pageSize) {
//                            buffer.clear();
//                            if (fc.read(buffer) != -1) {
//                                buffer.flip();
//                                pos = 0; // reset pointer
//                            } else
//                                break;
//                        }
//                        pos += REG_NAME_SIZE;
//                    }
//
//                    System.out.println("regName: " + pos);
//
//                    if (pos + LEN_STR_SIZE >= pageSize) {
//                        buffer.clear();
//                        if (fc.read(buffer) != -1) {
//                            buffer.flip();
//                            pos = 0; // reset pointer
//                        } else
//                            break;
//                    }
//                    // get the length of data element
//                    if (getLenStr(buffer)) { // BN_NAME
//
//                        System.out.println("bn name LenStr " + pos);
//                        System.out.println("bn Name Length: " + lenStr);
//
//                        if (pos + lenStr >= pageSize) {
//                            buffer.clear();
//                            if (fc.read(buffer) != -1) {
//                                buffer.flip();
//                                pos = 0; // reset pointer
//                            } else
//                                break;
//                        }
//                        // get BN_NAME
//                        String sName = getString(buffer);
//                        System.out.println(sName);
//                    }
//
//                    System.out.println("bnName: " + pos);
//
//                    // move pointer to next BN_NAME
//                    if (pos + LEN_STR_SIZE >= pageSize) {
//                        buffer.clear();
//                        if (fc.read(buffer) != -1) {
//                            buffer.flip();
//                            pos = 0; // reset pointer
//                        } else
//                            break;
//                    }
//                    if (checkNotNull(buffer)) {            // BN_STATUS
//                        if (pos + STATUS_SIZE >= pageSize) {
//                            buffer.clear();
//                            if (fc.read(buffer) != -1) {
//                                buffer.flip();
//                                pos = 0; // reset pointer
//                            } else
//                                break;
//                        }
//                        pos += STATUS_SIZE;
//                    }
//
//                    System.out.println("status: " + pos);
//
//                    for (int j = 0; j < 3; j++) { // BN_REG_DT, BN_CANCEL_DT, BN_RENEW_DT
//                        if (pos + INT_SIZE >= pageSize) {
//                            buffer.clear();
//                            if (fc.read(buffer) != -1) {
//                                buffer.flip();
//                                pos = 0; // reset pointer
//                            } else
//                                break;
//                        }
//                        if (!checkNotNullDate(buffer)) {
//                            if (pos + LEN_STR_SIZE >= pageSize) {
//                                buffer.clear();
//                                if (fc.read(buffer) != -1) {
//                                    buffer.flip();
//                                    pos = 0; // reset pointer
//                                } else
//                                    break;
//                            } else {
//                                pos += LEN_STR_SIZE;
//                            }
//                        }
//                    }
//                    System.out.println("3 dates after: " + pos);
//
//                    if (pos + LEN_STR_SIZE >= pageSize) {
//                        buffer.clear();
//                        if (fc.read(buffer) != -1) {
//                            buffer.flip();
//                            pos = 0; // reset pointer
//                        } else
//                            break;
//                    }
//                    if (checkNotNull(buffer)) {               // BN_STATE_NUM
//                        if (pos + STATE_NUM_SIZE >= pageSize) {
//                            buffer.clear();
//                            if (fc.read(buffer) != -1) {
//                                buffer.flip();
//                                pos = 0; // reset pointer
//                            } else
//                                break;
//                        }
//                        pos += STATE_NUM_SIZE;
//                    }
//
//                    System.out.println("State number: " + pos);
//
//                    if (pos + LEN_STR_SIZE >= pageSize) {
//                        buffer.clear();
//                        if (fc.read(buffer) != -1) {
//                            buffer.flip();
//                            pos = 0; // reset pointer
//                        } else
//                            break;
//                    }
//                    if (checkNotNull(buffer)) {           // BN_STATE_OF_REG
//                        if (pos + STATE_SIZE >= pageSize) {
//                            buffer.clear();
//                            if (fc.read(buffer) != -1) {
//                                buffer.flip();
//                                pos = 0; // reset pointer
//                            } else
//                                break;
//                        }
//                        pos += STATE_SIZE;
//                    }
//
//                    System.out.println("State of reg: " + pos);
//
//                    if (pos + LEN_STR_SIZE >= pageSize) {
//                        buffer.clear();
//                        if (fc.read(buffer) != -1) {
//                            buffer.flip();
//                            pos = 0; // reset pointer
//                        } else
//                            break;
//                    }
//                    if (checkNotNull(buffer)) {         // BN_ABN
//                        if (pos + ABN_SIZE >= pageSize) {
//                            buffer.clear();
//                            if (fc.read(buffer) != -1) {
//                                buffer.flip();
//                                pos = 0; // reset pointer
//                            } else
//                                break;
//                        }
//                        pos += ABN_SIZE;
//                    }
//
//                    System.out.println("abn: " + pos);
//
//                    if (pos + COMMA_SIZE >= pageSize) {
//                        buffer.clear();
//                        if (fc.read(buffer) != -1) {
//                            buffer.flip();
//                            pos = 0; // reset pointer
//                        } else
//                            break;
//                    } else {
//                        pos += COMMA_SIZE;
//                    }
//                    countRec++;
//
//                    System.out.println("after comma: " + pos);
//                }
//                buffer.clear();






            }
            fc.close();
            fis.close();
        } catch (FileNotFoundException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        if (countFound == 0)
            System.out.println("BN_Name does not contain " + queryKey + ".");
        else
            System.out.println(countFound + " records found containing " + queryKey + "!");
    }

    private int getLenStr (ByteBuffer buffer) {
        try {
            lenStr = (int)buffer.get(pos);
            if (lenStr == -1) // found null element
                lenStr = 0;
        } catch (NumberFormatException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return lenStr;
    }

    private boolean checkEOPmark(ByteBuffer buffer) {
        byte[] bnName = new byte[LEN_STR_SIZE];
        bnName[0] = buffer.get(pos);
        String s = "";
        try {
            s = new String(bnName, "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        if (s.equals("}")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkNotNull (ByteBuffer buffer) { return false; }

    private String getString(ByteBuffer buffer) {
        int pointer = 0;
        byte[] bnName = new byte[lenStr];
        String s = "";
        pointer += LEN_STR_SIZE;
        for (int i = 0; i < lenStr; i++) {
            bnName[i] = buffer.get(pointer);
            pointer++;
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

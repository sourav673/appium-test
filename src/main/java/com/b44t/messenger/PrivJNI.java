package com.b44t.messenger;

public class PrivJNI {

    public final static int PRV_EVENT_CREATE_NONE                   = 0;
    public final static int PRV_EVENT_CREATE_VAULT                  = 1;
    public final static int PRV_EVENT_DEINIT                        = 2;
    public final static int PRV_EVENT_ABORT                         = 3;
    public final static int PRV_EVENT_SHUTDOWN                      = 4;
    public final static int PRV_EVENT_USER_ENTERED_PASSCODE         = 5;
    public final static int PRV_EVENT_ADD_NEW_PEER                  = 6;
    public final static int PRV_EVENT_RECEIVED_PEER_PDU             = 7;
    public final static int PRV_EVENT_ENCRYPT_FILE                  = 8;
    public final static int PRV_EVENT_DECRYPT_FILE                  = 9;
    public final static int PRV_EVENT_REQUEST_SSS                   = 10;
    public final static int PRV_EVENT_RECEIVED_SSS                  = 11;
    public final static int PRV_EVENT_STOP_RENDERING                = 12;
    public final static int PRV_EVENT_PEER_OFFLINE                  = 13;
    public final static int PRV_EVENT_PEER_TIMEOUT_REACHED          = 14;
    public final static int PRV_EVENT_FILE_SANITY_FAILED            = 15;
    /*
     * NOTE: Add any event above PRV_EVENT_LAST and update PRV_EVENT_LAST
     */ 
    public final static int PRV_EVENT_LAST                          = 16;


    public final static int PRV_APP_STATUS_ERROR                    = 0;
    public final static int PRV_APP_STATUS_FAILED                   = 1;
    public final static int PRV_APP_STATUS_INVALID_REQUEST          = 2;
    public final static int PRV_APP_STATUS_VAULT_IS_READY           = 3;
    public final static int PRV_APP_STATUS_VAULT_FAILED             = 4;
    public final static int PRV_APP_STATUS_USER_ALREADY_EXISTS      = 5;
    public final static int PRV_APP_STATUS_USER_DOESNOT_EXISTS      = 6;
    public final static int PRV_APP_STATUS_PEER_ALREADY_ADDED       = 7;
    public final static int PRV_APP_STATUS_SEND_PEER_PDU            = 8;
    public final static int PRV_APP_STATUS_PEER_ADD_ACCEPTED        = 9;
    public final static int PRV_APP_STATUS_PEER_ADD_COMPLETE        = 10;
    public final static int PRV_APP_STATUS_PEER_ADD_PENDING         = 11;
    public final static int PRV_APP_STATUS_PEER_BLOCKED             = 12;
    public final static int PRV_APP_STATUS_FILE_ENCRYPTED           = 13;
    public final static int PRV_APP_STATUS_FILE_ENCRYPTION_FAILED   = 14;
    public final static int PRV_APP_STATUS_FILE_DECRYPTED           = 15;
    public final static int PRV_APP_STATUS_FILE_DECRYPTION_FAILED   = 16;
    public final static int PRV_APP_STATUS_INVALID_FILE             = 17;
    public final static int PRV_APP_STATUS_FILE_INACCESSIBLE        = 18;
    public final static int PRV_APP_STATUS_AWAITING_PEER_AUTH       = 19;
    /*
     * NOTE: Add any event above PRV_APP_STATUS_LIB_LAST and update PRV_APP_STATUS_LIB_LAST
     */ 
    public final static int PRV_APP_STATUS_LIB_LAST                 = 20;

    // Message method to be called by the C++ code
    public void onNativeMsgCallback(String pID, int statusCode, byte[] pdu) {

        if (statusCode == PrivJNI.PRV_APP_STATUS_VAULT_IS_READY) {
            System.out.println("ALICE: Congratulations! Vault is created\n");
            JEvent jevent = new JEvent(PrivJNI.PRV_EVENT_ADD_NEW_PEER, "", "Bob", "009", "", "", "", 0, pdu);
            produceEvent(jevent);
            System.out.println("ALICE: Add new peer: BOB (009)");
        } else if (statusCode == PrivJNI.PRV_APP_STATUS_SEND_PEER_PDU && pID.toLowerCase().contains("009")) {
            System.out.println("\nBOB: Add Peer Request:" + pID);
            JEvent jevent = new JEvent(PrivJNI.PRV_EVENT_RECEIVED_PEER_PDU, "", "Alice", "007", "", "", "", 0, pdu);
            produceEvent(jevent);
            System.out.println("BOB: Add Peer Accept :" + pID);
        } else if (statusCode == PrivJNI.PRV_APP_STATUS_SEND_PEER_PDU && pID.toLowerCase().contains("007")) {
            System.out.println("\nBOB: Add Peer Request:" + pID);
            JEvent jevent = new JEvent(PrivJNI.PRV_EVENT_RECEIVED_PEER_PDU, "", "Bob", "009", "", "", "", 0, pdu);
            produceEvent(jevent);
            System.out.println("BOB: Add Peer Accept :" + pID);
        } else if (statusCode == PrivJNI.PRV_APP_STATUS_PEER_ADD_COMPLETE && pID.toLowerCase().contains("009")) {
            System.out.println("\nALICE: Add peer Complete:" + pID);
            JEvent jevent = new JEvent(PrivJNI.PRV_EVENT_ENCRYPT_FILE, "", "Bob", "009", "", "/Users/milinddeore/PROJECTS/privitty/privitty-native/libpriv/src/platform/macos", "Antler.pdf", 0, pdu);
            produceEvent(jevent);
            System.out.println("\nALICE: Encrypt a the file: Antler.pdf");
        } else if (statusCode == PrivJNI.PRV_APP_STATUS_FILE_ENCRYPTED) {
            System.out.println("\nALICE: Encrypted the given file");
            JEvent jevent = new JEvent(PrivJNI.PRV_EVENT_DECRYPT_FILE, "", "Bob", "009", "", "", "Antler.prv", 1, new byte[0]);
            produceEvent(jevent);
            System.out.println("\nALICE: Decrypt a the file: Antler.pdf");
        } else if (statusCode == PrivJNI.PRV_APP_STATUS_FILE_DECRYPTED) {
            System.out.println("\nALICE: Decrypted a given file");
        } else if (statusCode == PrivJNI.PRV_APP_STATUS_FILE_INACCESSIBLE) {
            System.out.println("CPP CB: ERROR: Failed to open file for reading");
        } else {
            System.out.println(">> DEFAULT: " + statusCode);
        }
    }


    public native String version();
    public native void startEventLoop(String path);
    public native void stopConsumer();
    public native void produceEvent(JEvent event);
    public native void registerMsgCallback();
}

class JEvent {

    /* Event Payload JNI <--> Native */
    public int eventType;
    public String mID;
    public String mName;
    public String pID;
    public String pCode;
    public String filePath;
    public String fileName;
    public int direction;
    public byte[] pdu;


    // Constructor
    public JEvent(int event_type, String my_id, String my_name, String peer_id, String pcode, String file_path, String file_name, int dir, byte[] pdu) {
        this.eventType = event_type;
        this.mID = my_id;
        this.mName = my_name;
        this.pID = peer_id;
        this.pCode = pcode;
        this.filePath = file_path;
        this.fileName = file_name;
        this.direction = dir;
        this.pdu = pdu;
    }
}


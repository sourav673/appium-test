package com.b44t.messenger;
import android.util.Log;
import com.b44t.messenger.PrivEvent;

import android.content.Context;
import com.b44t.messenger.DcContext;
import com.b44t.messenger.DcMsg;

import org.thoughtcrime.securesms.connect.DcHelper;

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

    private Context context = null;
    public PrivJNI(Context context) {
        this.context = context;
    }

    public native String version();
    public native void startEventLoop(String path);
    public native void stopConsumer();
    public native void produceEvent(PrivEvent event);
}



package com.b44t.messenger;

public class PrivEvent
{
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

  public PrivEvent(int eventType, String mID, String mName, String pID, String pCode, String filePath, String fileName, int direction, byte[] pdu) {
    this.eventType = eventType;
    this.mID = mID;
    this.mName = mName;
    this.pID = pID;
    this.pCode = pCode;
    this.filePath = filePath;
    this.fileName = fileName;
    this.direction = direction;
    this.pdu = pdu;
  }
}

package com.company;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Zach on 4/24/16.
 */
public class Message {
    /*
    msgID number(10) not null,
	senderEmail varchar2(128) not null,
	recipientEmail varchar2(128) not null,
	time_sent timestamp,
	msg_subject varchar2(1024),
	msg_body varchar2(1024),
     */

    private long msgID;
    private String senderEmail;
    private String recipientEmail;
    private Timestamp timeSent;
    private String msgSubject;
    private String msgBody;

    public String getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
    }

    public Timestamp getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(Date timeSent) {
        this.timeSent = new Timestamp(timeSent.getTime());
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public long getMsgID() {
        return msgID;
    }

    public void setMsgID(long msgID) {
        this.msgID = msgID;
    }

    public String getMsgSubject() {
        return msgSubject;
    }

    public void setMsgSubject(String msgSubject) {
        this.msgSubject = msgSubject;
    }
}

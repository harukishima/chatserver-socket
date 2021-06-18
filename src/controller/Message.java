package controller;

import java.io.Serializable;

public class Message implements Serializable {
    private String sender;
    private String receiver;
    private String body;
    private int code;

    public Message(String sender, String receiver, String body, int code) {
        this.sender = sender;
        this.receiver = receiver;
        this.body = body;
        this.code = code;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getBody() {
        return body;
    }

    public int getCode() {
        return code;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", body='" + body + '\'' +
                ", code=" + code +
                '}';
    }
}

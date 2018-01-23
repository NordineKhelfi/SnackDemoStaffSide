package com.khelfi.snackdemostaffside.Model;

/**
 * Downstream HTTP messages (JSON) model
 * --> https://firebase.google.com/docs/cloud-messaging/http-server-ref#downstream-http-messages-json
 *
 * Created by norma on 23/01/2018.
 */

public class Message {
    public String to;
    public Notification notification;

    public Message(String to, Notification notification) {
        this.to = to;
        this.notification = notification;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }
}

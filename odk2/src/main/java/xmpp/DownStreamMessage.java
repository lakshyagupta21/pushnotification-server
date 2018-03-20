package xmpp;

import java.util.HashMap;

public class DownStreamMessage {
    String regId;
    HashMap<String,String> notificationMessage;
    String uuid;

    public DownStreamMessage(String regId, HashMap<String, String> notificationMessage, String uuid) {
        this.regId = regId;
        this.notificationMessage = notificationMessage;
        this.uuid = uuid;
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public HashMap<String, String> getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(HashMap<String, String> notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}

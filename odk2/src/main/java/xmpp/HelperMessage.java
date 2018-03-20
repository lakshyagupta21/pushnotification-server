package xmpp;

import Utils.Constants;

import java.util.HashMap;
import java.util.Map;

public class HelperMessage {

    public static Map<String, Object> message(DownStreamMessage msg) {
        final Map<String, Object> map = new HashMap<String, Object>();
        if (msg.getRegId() != null) {
            map.put(Constants.TO, msg.getRegId());
        }
        if (msg.getUuid() != null) {
            map.put(Constants.MESSAGE_ID, msg.getUuid());
        }
        map.put(Constants.DATA, msg.getNotificationMessage());
        return map;
    }
}

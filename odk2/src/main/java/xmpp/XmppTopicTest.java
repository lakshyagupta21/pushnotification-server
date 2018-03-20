package xmpp;

import Utils.Constants;
import Utils.Keys;
import Utils.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class XmppTopicTest {
    private static final String TAG = "XmppTopicTest";

    public static void main(String args[]) throws IOException, InterruptedException, XMPPException, SmackException {

        XmppConnection connection = new XmppConnection(Keys.AUTH_KEY, Keys.SENDER_ID);
        connection.connect();

        Logger.log(XmppTokenTest.class.getName(), "Connection successful");

        String message = createMessageTopic(Keys.TOPIC);
        if (message != null) {
            connection.sendPacket(message);
        } else {
            Logger.log(TAG, "Message error check createMessageFunction");
        }

        String lock = "lock";
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static String createMessageTopic(String topic) {
        String message = null;

        String uid = String.valueOf(UUID.randomUUID());
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put("title", "Notification title");
        dataMap.put("message", "This is a notification message");
        dataMap.put("type", "topic");
        dataMap.put("topic",topic);

        DownStreamMessage outMessage = new DownStreamMessage(Constants.TOPIC_PREFIX + topic, dataMap, uid);
        Map<String, Object> json = HelperMessage.message(outMessage);

        try {
            message = new ObjectMapper().writeValueAsString(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }
}

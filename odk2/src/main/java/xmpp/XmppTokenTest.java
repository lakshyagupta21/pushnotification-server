package xmpp;

import Utils.Keys;
import Utils.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class XmppTokenTest {

    private static final String TAG = "XmppTokenTest";

    public static void main(String args[]) throws IOException, InterruptedException, XMPPException, SmackException {

        XmppConnection connection = new XmppConnection(Keys.AUTH_KEY, Keys.SENDER_ID);
        connection.connect();

        Logger.log(XmppTokenTest.class.getName(), "Connection successful");

        String message = createMessage(Keys.REGIS_ID);
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

    private static String createMessage(String regisid) {
        String message = null;

        String uid = String.valueOf(UUID.randomUUID());
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put("title", "Notification title");
        dataMap.put("message", "This is a notification message");
        dataMap.put("type", "singleuser");

        DownStreamMessage outMessage = new DownStreamMessage(null, dataMap, uid);
        Map<String, Object> json = HelperMessage.message(outMessage);

        try {
            message = new ObjectMapper().writeValueAsString(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }
}

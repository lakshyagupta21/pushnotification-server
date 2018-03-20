package xmpp;

import Utils.Constants;
import Utils.Logger;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import javax.net.ssl.SSLSocketFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class XmppConnection implements StanzaListener {

    private XMPPTCPConnection connection;
    private String apiKey;
    private String serverId;
    private String username;

    public XmppConnection(String apiKey, String serverId) {
        this.apiKey = apiKey;
        this.serverId = serverId;
        this.username = serverId + '@' + Constants.FCM_SERVER_CONNECTION;
    }

    public void connect() throws InterruptedException, XMPPException, SmackException, IOException {
        final XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder();
        config.setXmppDomain("FCM XMPP Client Connection Server");
        config.setHost(Constants.FCM_SERVER);
        config.setPort(Constants.FCM_PORT_TESTING);
        config.setSecurityMode(SecurityMode.ifpossible);
        config.setSendPresence(false);
        config.setSocketFactory(SSLSocketFactory.getDefault());
        config.setDebuggerEnabled(true);

        connection = new XMPPTCPConnection(config.build());

        connection.connect();

        // Enable automatic reconnection
        ReconnectionManager.getInstanceFor(connection).enableAutomaticReconnection();

        // Disable Roster at login
        Roster.getInstanceFor(connection).setRosterLoadedAtLogin(false);

        SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
        SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");

        //  Handling connection related issues
        connection.addConnectionListener(new ConnectionListener() {

            @Override
            public void reconnectionSuccessful() {
                Logger.log(XmppConnection.class.getName(), "Reconnection successful ...");
            }

            @Override
            public void reconnectionFailed(Exception e) {
                Logger.log(XmppConnection.class.getName(), e.getMessage());
            }

            @Override
            public void reconnectingIn(int seconds) {
                Logger.log(XmppConnection.class.getName(), "Reconnecting in " +  seconds);
            }

            @Override
            public void connectionClosedOnError(Exception e) {
                Logger.log(XmppConnection.class.getName(), "Connection closed on error");
            }

            @Override
            public void connectionClosed() {
                Logger.log(XmppConnection.class.getName(), "Connection closed.");
            }

            @Override
            public void authenticated(XMPPConnection arg0, boolean arg1) {
                Logger.log(XmppConnection.class.getName(), "User authenticated");
            }

            @Override
            public void connected(XMPPConnection arg0) {
                Logger.log(XmppConnection.class.getName(), "Connection established");
            }
        });

        connection.login(username, apiKey);
        Logger.log(XmppConnection.class.getName(), "User logged in: " + username);
    }

    public void sendPacket(String json){
        final Stanza request = new xmpp.DataPacketExtension(json).toPacket();
        try {
            connection.sendStanza(request);
        } catch (SmackException.NotConnectedException | InterruptedException e) {
            Logger.log(XmppConnection.class.getName(), "The packet could not be sent due to a connection problem. Packet: {}" + request.toXML());
        }
    }

    @Override
    public void processStanza(Stanza packet) {
        Logger.log(XmppConnection.class.getName(), "Received: " + packet.toXML());

        DataPacketExtension fcmPacket = (DataPacketExtension) packet.getExtension(Constants.FCM_NAMESPACE);
        String json = fcmPacket.getJson();
        Map<String, Object> map = new HashMap<String, Object>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            //convert JSON string to Map
            map = mapper.readValue(json, new TypeReference<HashMap<String, Object>>() {});
            System.out.println(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package xmpp;

import Utils.Constants;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;

public class DataPacketExtension implements ExtensionElement {

    String json;

    DataPacketExtension(String json) {
        this.json = json;
    }

    public String getJson(){
        return json;
    }

    @Override
    public String getNamespace() {
        return Constants.FCM_NAMESPACE;
    }

    @Override
    public String getElementName() {
        return Constants.FCM_ELEMENT_NAME;
    }


/*    <message id="">
  <gcm xmlns="google:mobile:data">
    {
        "to": "/topics/foo-bar",
            "data": {
        "message": "This is a Firebase Cloud Messaging Topic http_database.Message!",
    }
    }

  </gcm>
</message>
*/

    @Override
    public CharSequence toXML() {
        System.out.println(String.format("<%s xmlns=\"%s\">%s</%s>", getElementName(), getNamespace(), json, getElementName()));
        return String.format("<%s xmlns=\"%s\">%s</%s>", getElementName(), getNamespace(), json, getElementName());
    }

    public Stanza toPacket() {
        final Message message = new Message();
        message.addExtension(this);
        return message;
    }
}

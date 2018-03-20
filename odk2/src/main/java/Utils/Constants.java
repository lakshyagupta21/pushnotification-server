package Utils;

public class Constants {


    public static final String FCM_SERVER = "gcm.googleapis.com";

    // testing port
    public static final int FCM_PORT_TESTING = 5236;

    // production port
    public static final int FCM_POST_PRODUCTION = 5235;

    public static final String FCM_ELEMENT_NAME = "gcm";
    public static final String FCM_NAMESPACE = "google:mobile:data";
    public static final String FCM_SERVER_CONNECTION = "gcm.googleapis.com";


    // targets, options and payloads for downstream
    // targets
    public static final String TO = "to";
    public static final String MESSAGE_ID = "message_id";
    public static final String DATA = "data";
    public static final String TOPIC_PREFIX = "/topics/";




    // http protocol parameters
    public static final int SEND_USER = 1;
    public static final int SEND_TOPIC = 2;

    public static final String USERS = "users";
    public static final String SUBSCRIPTION = "subscription_info";
    public static final String ACCOUNT_INFO = "account_info";

}
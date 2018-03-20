package http_database;

import Utils.Constants;
import Utils.Keys;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import model.User;
import Utils.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Firebase http_database.Database quickstart sample for the Java Admin SDK.
 * See: https://firebase.google.com/docs/admin/setup#add_firebase_to_your_app
 * code source : Firebase-quickstart
 */

public class Database {

    private static final String TAG = "Database";
    private static DatabaseReference database;
    private static HashMap<String, User> uniqueUsers;


    public static void startListeners() {

        database.child(Constants.USERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    if (uniqueUsers.containsKey(childSnapshot.getKey())) {
                        continue;
                    }

                    User user = childSnapshot.child(Constants.ACCOUNT_INFO).getValue(User.class);
                    HashMap<String, String> subscription = (HashMap<String, String>) childSnapshot.child(Constants.SUBSCRIPTION).getValue();

                    ArrayList<String> subscribeList = new ArrayList<>();

                    if (subscription == null) {
                        user.setSubscriptionList(subscribeList);
                    } else {
                        // using for-each loop for iteration over Map.entrySet()
                        for (Map.Entry<String, String> entry : subscription.entrySet())
                            subscribeList.add(entry.getValue());
                    }
                    user.setSubscriptionList(subscribeList);

                    uniqueUsers.put(childSnapshot.getKey(), user);

                    PushServer pushServer = new PushServer();
                    String response = pushServer.send(user.getToken(),Constants.SEND_USER);
                    Logger.log(TAG,"onDataChange " + "Message Sent " + response);
                }
                System.out.println(uniqueUsers);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        database.child(Constants.USERS).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {

                if (uniqueUsers.containsKey(snapshot.getKey())) {
                    return;
                }

                User user = snapshot.child(Constants.ACCOUNT_INFO).getValue(User.class);
                HashMap<String, String> subscription = (HashMap<String, String>) snapshot.child("subscription_info").getValue();

                ArrayList<String> subscribeList = new ArrayList<>();

                if (subscription != null) {
                    // using for-each loop for iteration over Map.entrySet()
                    for (Map.Entry<String, String> entry : subscription.entrySet())
                        subscribeList.add(entry.getValue());
                }
                user.setSubscriptionList(subscribeList);

                uniqueUsers.put(snapshot.getKey(), user);
                Logger.log(TAG, "Users List " + uniqueUsers);

                PushServer pushServer = new PushServer();
                String response = pushServer.send(user.getToken(),Constants.SEND_USER);
                Logger.log(TAG,"onChildAdded " + "Message Sent " + response);
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
                Logger.log(TAG,"onChildChanged " + snapshot);
                if (uniqueUsers.containsKey(snapshot.getKey())) {

                    User user = uniqueUsers.get(snapshot.getKey());
                    // user already exists
                    // either token value is changed or new topic s subscribed
                    ArrayList<String> subscribeList = new ArrayList<>();

                    HashMap<String, String> subscription = (HashMap<String, String>) snapshot.child(Constants.SUBSCRIPTION).getValue();

                    if (subscription != null) {
                        // using for-each loop for iteration over Map.entrySet()
                        for (Map.Entry<String, String> entry : subscription.entrySet())
                            subscribeList.add(entry.getValue());
                    }
                    // check for new topic
                    for (String topic : subscribeList) {
                        if (!user.getSubscriptionList().contains(topic)) {
                            // new topic send notification
                            PushServer pushServer = new PushServer();
                            String response = pushServer.send(topic, Constants.SEND_TOPIC);
                            Logger.log(TAG,"onChildChanged " + "Message Sent " + response);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot){
                Logger.log(TAG, "onChildRemoved " + snapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {
                Logger.log(TAG,"onChildMoved " + snapshot);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Logger.log(TAG, "onCancelled " + error);
            }
        });
    }


    public static void main(String[] args) {

        // Initialize Firebase
        try {
            // [START initialize]
            FileInputStream serviceAccount = new FileInputStream(Keys.SERVICE_FILE_PATH);
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl(Keys.DATABASE_URL)
                    .build();
            FirebaseApp.initializeApp(options);
            System.out.println("Initialized");
            // [END initialize]
        } catch (IOException e) {
            System.out.println("ERROR: invalid service account credentials. See README.");
            System.out.println(e.getMessage());

            System.exit(1);
        }

        // Shared http_database.Database reference
        database = FirebaseDatabase.getInstance().getReference();

        uniqueUsers = new HashMap<>();

        // Start listening to the http_database.Database
        startListeners();

        String lock = "lock";
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}

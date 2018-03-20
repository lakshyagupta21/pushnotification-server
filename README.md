# pushnotification-server
This project is created to test and verify few things related to FCM Server implementation.

If you need to test sending notification to a Android device then first setup your project for Android device follow this link and follow the instructions mentioned in the [Readme.md](https://github.com/lakshyagupta21/pushnotification-android)

1. After setting up of Android project. Copy the same sender key in [Key.java](https://github.com/lakshyagupta21/pushnotification-server/blob/master/odk2/src/main/java/Utils/Keys.java) file
2. Follow <b>Add firebase to you app</b> section in [Firebase docs](https://firebase.google.com/docs/admin/setup) and download the key file and save it. Copy the absolute path of the generated key file and paste it in [Keys.java](https://github.com/lakshyagupta21/pushnotification-server/blob/master/odk2/src/main/java/Utils/Keys.java) by SERVICE_FILE_PATH
3. Follow this [link](https://stackoverflow.com/questions/40168564/where-can-i-find-my-firebase-reference-url-in-firebase-account) to get the databaseurl of your project and replace it by DATABASE_URL in same Keys.java file.
4. Follow this [link](https://stackoverflow.com/questions/37427709/firebase-messaging-where-to-get-server-key) to get the server key
5. Registration key will be useful when sending notification to single user so replace it by user's token value which you can see in the project's database

# How to Use
1. To start using it, you need to have set up the [Push Server](https://github.com/MathiasBerwig/Push-server).

2. With the Push Server OK, open the file ``Config.java`` and change the addresses of your server and the GCM Sender ID:
```java
public static class ADRESSES {
        public static final String SERVER_URL = "http://your-server-name/";
        public static final String INSERT_USER_URL = SERVER_URL + "Push-server/insert_user.php";
        public static final String DELETE_USER_URL = SERVER_URL + "Push-server/delete_user.php";
    }

    public static class GCM {
        public static final String DEFAULT_SENDER_ID = "YOUR-GCM-ID";
    }
```

3. Build and run the app. 

4. Register the device to your server: open the Push application and name your user/device. After this, click Register and wait for the Toast to show the result.

5. Open the Push Server and send the message:

![Push Server form](https://raw.githubusercontent.com/MathiasBerwig/Push-server/master/screenshots/gcm-web-form.PNG)

6. Done! The device should show the notification now:

![Notification on Device](https://raw.githubusercontent.com/MathiasBerwig/Push/master/screenshots/gcm-android-notification.png)

![Notification on Device](https://raw.githubusercontent.com/MathiasBerwig/Push/master/screenshots/gcm-android-application.png)

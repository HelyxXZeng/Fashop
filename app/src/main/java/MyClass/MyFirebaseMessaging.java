package MyClass;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.fashop.R;
import com.example.fashop.activity.SellerOrderDetailActivity;
import com.example.fashop.activity.UserOrderDetailActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    private static final String NOTIFICATION_CHANNEL_ID = "MY_NOTIFICATION_CHANNEL_ID";

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            String accountType = ""+ds.child("accountType").getValue();

                            //get data from notification
                            String notificationType = remoteMessage.getData().get("notificationType");
                            if (notificationType.equals("NewOrder")){
                                String buyerUid = remoteMessage.getData().get("buyerUid");
                                String shopAccountType = remoteMessage.getData().get("shopAccountType");
                                String orderId = remoteMessage.getData().get("orderId");
                                String notificationTitle = remoteMessage.getData().get("notificationTitle");
                                String notificationDescription = remoteMessage.getData().get("notificationMessage");

                                if (firebaseUser != null && (accountType.equals("Admin") || accountType.equals("Staff"))){
                                    showNotification(orderId, shopAccountType, buyerUid, notificationTitle, notificationDescription, notificationType);
                                }

                            }

                            if (notificationType.equals("OrderStatusChanged")){
                                String buyerUid = remoteMessage.getData().get("buyerUid");
                                String sellerUid = remoteMessage.getData().get("shopAccountType");
                                String orderId = remoteMessage.getData().get("orderUid");
                                String notificationTitle = remoteMessage.getData().get("notificationTitle");
                                String notificationDescription = remoteMessage.getData().get("notificationMessage"); //

                                if (firebaseUser != null && firebaseAuth.getUid().equals(buyerUid)){
                                    showNotification(orderId, sellerUid, buyerUid, notificationTitle, notificationDescription, notificationType);
                                }
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void showNotification(String orderId, String sellerUid, String buyerUid,
                                  String notificationTitle, String notificationDescription,
                                  String notificationType){
        //notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //id for notification
        int notificationID = new Random().nextInt(3000);

        //check if android version is 0 or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            setupNotificationChannel(notificationManager);
        }

        //handle notification click, start order activity
        Intent intent = null;
        if (notificationType.equals("NewOrder")){
            //open OrderDetailsSellerActivity
            intent = new Intent(this, SellerOrderDetailActivity.class);
            intent.putExtra("orderId", orderId);
            intent.putExtra("orderBy", buyerUid);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        }
        else if (notificationType.equals("OrderStatusChanged")){
            intent = new Intent(this, UserOrderDetailActivity.class);
            intent.putExtra("orderId", orderId);
            intent.putExtra("orderTo", sellerUid);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }

        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0, new Intent[]{intent} , PendingIntent.FLAG_ONE_SHOT);

        //Large Icon
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.logo_no_background);

        //sound of notification
        Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setSmallIcon(R.drawable.logo_no_background)
                .setLargeIcon(largeIcon)
                .setContentTitle(notificationTitle)
                .setContentText(notificationDescription)
                .setSound(notificationSoundUri)
                .setAutoCancel(true);  // cancel/dismiss when clicked
//                .setContentIntent(pendingIntent); //add intent
        //show notification
        notificationManager.notify(notificationID, notificationBuilder.build());
    }

    private void setupNotificationChannel(NotificationManager notificationManager) {
        CharSequence channelName = "Some Sample Text";
        String channelDescription = "Channel Description here";

        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setDescription(channelDescription);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.enableVibration(true);
        if (notificationManager != null){
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}

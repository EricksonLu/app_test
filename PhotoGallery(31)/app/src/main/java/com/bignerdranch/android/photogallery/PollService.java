package com.bignerdranch.android.photogallery;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

//与activity一样，服务是一个提供了生命周期回调方法的应用组件。而且，这些回调方法同样也会在主UI线程上运行
//初始创建的服务不会在后台线程上运行任何代码，而IntentService已提供了一套标准实现后台服务的代码。
//通过startService(Intent)方法启动的服务，其生命周期很简单，并具有四种生命周期回调方法
//onCreate()方法，服务创建时调用
//onStartCommand(Intent,int,int)方法，每次组件通过startService(Intent)方法启动服务时调用一次。两个整数参数，一个是一组标识符，一个是启动ID。标识符用来表示
//当前intent发送究竟是一次重新发送，还是一次从没成功过的发送。每次调用onStartCommand(Intent,int,int)方法，启动ID都会不同。因此，启动ID也可用于区分不同的命令
//onDestroy()方法调用，服务不再需要时调用，通常是在服务停止后。
//最后一个问题是，服务是如何停止的？更具所开发服务的具体类型，有多种方式可以停止五福。服务类型由onStartCommand()方法的返回值决定，可能的服务类型有
//Service.START_NOT_STICKY、START_REDELIVER_INTENT、START_STICKY等

//IntentService是一种non-sticky服务，non-sticky服务在服务自己认为已完成任务时停止。为获得non-sticky服务，应返回START_NOT_STICKY或START_REDELIVER_INTENT
//通过调用stopSelf()或stopSelf(int)方法，告诉Android任务已完成，stopself是无条件方法，不管onStartCommand方法调用多少次该方法总是会成功停止服务
//而IntentService使用的是stopSelf(int)方法，该方法需要来自于onStartCommand()方法的启动ID只有在接受最新启动ID后，该方法才会停止服务
//START_NOT_STICKY或START_REDELIVER_INTENT区别在于如果系统需要在服务完成任务之前关闭它，START_NOT_STICKY型服务会被关闭。而START_REDELIVER_INTENT型服务
//会在可用资源不再吃紧时，尝试再次启动服务。根据操作与应用的重要程度，在START_NOT_STICKY和START_REDELIVER_INTENT之间做出选择，这个应用的服务，根据定时器的设定
//重复运行，如果发生方法调用失败，也不会产生严重后果，因此应选择START_NOT_STICKY,同时，它也是IntentService的默认行为，当然，也可以调用IntentService.setIntentRedelivery(true)方法切换使用START_REDELIVER_INTENT

//sticky服务会持续运行，直到外部组件调用Context.stopService(Intent)方法让它停止为止，如因某种原因需终止服务，可传入一个null intent给onStartCommand()实现服务的重启
//sticky服务适用于长时间运行的服务，如音乐播放器这种启动后一直保持运行状态，直到用户主动停止的服务。即使是这样，也应考虑一种使用non-sticky服务代替它，因为拍段服务是否已启动会比较困难

//bindService(Intent,ServiceConnection,int)方法绑定一个服务，通过服务绑定可连接到一个服务并直接调用它的方法，ServiceConnection是代表服务绑定的一个对象，并负责接收全部绑定的回调方法
//对服务来说，绑定引入了另外两个生命周期回调方法：onBind(Intent)方法，绑定服务时调用，返回来自ServiceConnection.onServiceConnected(ComponentName,IBinder)方法的Ibinder对象
//onUnbind(Intent)方法，服务绑定终止时调用
//如果服务是一个本地服务，MyBinder就可能是本地进程中的一个简单java对象，通常MyBinder用于提供一个句柄，以便直接调用服务方法

//允许用户在后台查询新的搜索结果，一旦有了新的搜索结果，用户即可在状态栏接受到通知信息
//服务的intent又称作命令(command)IntentService逐个执行命令队列里的命令，接收到首个命令时，IntentService即完成启动，并出发一个后台线程，然后将命令放入队列。
//随后，IntentService继续按顺序执行每条命令。并同时为每条命令在后台线程上调用onHandleIntent方法。新进命令总是防止在队列尾部，最后，执行完队列中全部命令后，服务业随即停止并被销毁。
//因为能够响应Intent所以，需要在AndroidManifest.xml中声明它
public class PollService extends IntentService {
    private static final String TAG = "PollService";
    
    private static final int POLL_INTERVAL = 1000 * 60 * 5; // 5 minutes
    private static final int POLL_INTERVAL1 = 1000 * 15;   //15 seconds
//    存储定时器的状态信息
    public static final String PREF_IS_ALARM_ON = "isAlarmOn";

    public static final String ACTION_SHOW_NOTIFICATION = "com.bignerdranch.android.photogallery.SHOW_NOTIFICATION";

    public static final String PERM_PRIVATE = "com.bignerdranch.android.photogallery.PRIVATE";
    
    public PollService() {
        super(TAG);
    }

    @Override
    public void onHandleIntent(Intent intent) {
//        使用ConnectivityManager确认网络连接是否可用
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressWarnings("deprecation")
//                getBackgroundDataSetting方法返回为true，其次确认getActiveNetworkInfo返回结果不为空
//                在旧版本中，应检查getBackgroundDataSetting方法的返回结果，如果返回结果为false表示不逊于使用后台数据
//                在4.0后版本中，后台数据设置直接会禁用网络，getActiveNetworkInfo如果返回为空，则网络不可用。
//                如果使用getActiveNetworkInfo还需要获取ACCESS_NETWORK_STATE权限
        boolean isNetworkAvailable = cm.getBackgroundDataSetting() &&
            cm.getActiveNetworkInfo() != null;        
        if (!isNetworkAvailable) return;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        从SharedPreferences获取查询字符串
        String query = prefs.getString(FlickrFetchr.PREF_SEARCH_QUERY, null);
//        从SharedPreferences中获取上一次结果
        String lastResultId = prefs.getString(FlickrFetchr.PREF_LAST_RESULT_ID, null);

        ArrayList<GalleryItem> items;
        if (query != null) {
            items = new FlickrFetchr().search(query);
        } else {
            items = new FlickrFetchr().fetchItems();
        }

        if (items.size() == 0) 
            return;
//        get(0)代表获取第1个元素，然后getId()是获取它的ID名
        String resultId = items.get(0).getId();

        if (!resultId.equals(lastResultId)) {
            Log.i(TAG, "Got a new result: " + resultId);

//            因为Notification builder传参的限制需要获得resource的使用方法，这个r表示可以获得string.xml的索引
            Resources r = getResources();
//            此处的getActivity同getService的参数解释，在下面的isServiceAlarmOn可以看到flags的一些用法。
            PendingIntent pi = PendingIntent
                .getActivity(this, 0, new Intent(this, PhotoGalleryActivity.class), 0);
//            PendingIntent.getActivities(Context,requestCode,Intent[] intent,int flags)
//            配置Notification
            Notification notification = new NotificationCompat.Builder(this)
//                    设置标题Ticker text
                .setTicker(r.getString(R.string.new_pictures_title))
//                    设置小图标
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
//                    标题
                .setContentTitle(r.getString(R.string.new_pictures_title))
//                    文字
                .setContentText(r.getString(R.string.new_pictures_text))
//                    与AlarmManager类似，这里通过使用PendingIntent来完成指定任务，用户在下拉抽屉中点击Notification消息时，setAutoCancel(true)方法可调整
//                    上述行为，使用这个设置方法，用户点击Notification后，也可以将该消息从消息抽屉中删除
//                    最后调用NotificationManager.notify方法，传入的整形参数是通知消息的标识符，在整个应用中该值应该是唯一的，如是使用同一ID发送两条消息
//                    则第二条消息会替换掉第一条消息，这也是进度条或其他动态视觉效果的实现方式
                    .setContentIntent(pi)
                .setAutoCancel(true)
                .build();
//
            showBackgroundNotification(0, notification);
        }

        prefs.edit()
            .putString(FlickrFetchr.PREF_LAST_RESULT_ID, resultId)
            .commit();
    }

//    AlarmManager是可以发送Intent的系统服务，如何告诉AlarmManager发送什么样的intent呢，使用PendingIntent。
//    所以，可以使用PendingIntent打包intent“我想启动PollService”，然后将其发送给系统中的其他部件如AlarmManager
//    这个方法是一个静态方法，所以，可使定时器代码和与之相关的代码都放置在PollService中，但同时又允许其他系统部件调用它。通常会从前段fragment或控制层代码中启停定时器
//    注意这个setServiceAlarm的调用的参数，在fragment中使用getActivity，在StartupReceiver中使用onReceive中的context。
    public static void setServiceAlarm(Context context, boolean isOn) {

        Intent i = new Intent(context, PollService.class);
//        创建一个用来启动PollService的PendingIntent
//        PendingIntent.getService方法打包了一个Context.startService(Intent)方法的调用
//        它的4个参数：
//        1用来发送intent的Context
//        2一个区分PendingIntent来源的请求代码
//        3待发送的Intent对象
//        4一组用来决定如何创建PendingIntent的标识符

//        使用PendingIntent.getService方法获取PendingIntent时，告诉了操作系统请记住，我们需要使用startService(Intent)方法发送这个Intent
//        随后调用PendingIntent对象的send方法时，操作系统会按照我们的要求发送原来封装的intent
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
//        获得一个AlarmManager，AlarmManager的一个用处就是即使进程停止了，AlarmManager依然会不断发送intent，以反复启动PollService服务。
//        在这里表现就是不断的弹出来notification
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

//        一个PendingIntent只能登记一个定时器，这也是isOn为false时，setServiceAlarm(Context,bolean)方法的工作原理
//        将PendingIntent交给其他应用使用时，它是代表当前应用发送token对象的，另外，PendingIntent本身存在于操作系统而不是token里，因此实际上是我们在控制着它
//        如果不顾及别人感受的话,可以在交给别人一个PendingIntent对象后,立即撤销它,让send()方法啥都不做
        if (isOn) {
//            设置定时器4个参数
//            1描述定时器时间基准的常量
//            2定时器运行的开始时间
//            3定时器循环的时间间隔
//            4到时候要发送的Intent
            alarmManager.setRepeating(AlarmManager.RTC, 
                    System.currentTimeMillis(), POLL_INTERVAL, pi);
        } else {
//            撤销PendingIntent定时器
            alarmManager.cancel(pi);
//            撤销PendingIntent
            pi.cancel();
        }
//        把定时器的状态添加到SharedPreferences中
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(PollService.PREF_IS_ALARM_ON, isOn)
            .commit();
    }

    public static boolean isServiceAlarmOn(Context context) {
//        传入FLAG_NO_CREATE判断定时器的启停状态，这里的PendingIntent仅用于设置定时器，因此PendingIntent空值表示定时器还未设置
        Intent i = new Intent(context, PollService.class);
        PendingIntent pi = PendingIntent.getService(
                context, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }


//    这个叫result receiver
//    最后的receiver
//    负责发送通知信息，并接受其他接收者返回的结果码，它的运行应该总是在最后的
    private static BroadcastReceiver sNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent i) {
            Log.i(TAG, "received result: " + getResultCode());
            if (getResultCode() != Activity.RESULT_OK)
                // a foreground activity cancelled the broadcast
                return;
//            这里的第二个参数是后面的default value 啥意思？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？
            int requestCode = i.getIntExtra("REQUEST_CODE", 0);
            Notification notification = (Notification)i.getParcelableExtra("NOTIFICATION");
//
            NotificationManager notificationManager = (NotificationManager)
                    c.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(requestCode, notification);
        }
    };

//    在后台通过创建通知
//    服务已在后台运行并制定任务了，首次显示通知信息时，在状态栏上显示的ticker text
//    ticker text消失后，在状态栏上显示图标，代表通知信息自身，在通知抽屉中显示视图，用户点击抽屉中的通知信息，触发PendingIntent
//    发送broadcast intent
//    可发送有序broadcast的新方法，这个方法打包了一个Notification调用，然后作为一个broadcast发出，只要通知消息还没被撤销，可指定一个result receiver发出打包的Notification
    void showBackgroundNotification(int requestCode, Notification notification) {
//        发送自己的broadcast intent：首先，创建一个inent并传入sendBroadcast(intent)方法即可
        Intent i = new Intent(ACTION_SHOW_NOTIFICATION);

        i.putExtra("REQUEST_CODE", requestCode);
//        把build好的notification传给result receiver
        i.putExtra("NOTIFICATION", notification);
//        发送带有权限的broadcast，任何应用必须使用同样的权限才能接受这个发送的intent，利用broadcast intent解决过滤前台通知消息的问题
        sendOrderedBroadcast(i, PERM_PRIVATE, null, null, Activity.RESULT_OK, null, null);
    }
}

package com.snda.inote.provider;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;
import com.snda.inote.Consts;
import com.snda.inote.Inote;
import com.snda.inote.R;
import com.snda.inote.activity.AddFullActivity;
import com.snda.inote.activity.MainActivity;
import com.snda.inote.activity.NoteActivity;
import com.snda.inote.model.Note;
import com.snda.inote.model.User;
import com.snda.inote.service.MKSyncService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tom
 * Date: 11-3-1
 * Time: 下午6:11
 */
public class WidgetComponent extends AppWidgetProvider {
    private static List<Note> nearlyList = new ArrayList<Note>();

    int[] titles = new int[]{R.id.nodeitem1_title, R.id.nodeitem2_title, R.id.nodeitem3_title};
    int[] abstracts = new int[]{R.id.nodeitem1_abstract, R.id.nodeitem2_abstract, R.id.nodeitem3_abstract};
    int[] items = new int[]{R.id.nodeitem1, R.id.nodeitem2, R.id.nodeitem3};

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        String packageName = context.getPackageName();

        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];
            RemoteViews views = new RemoteViews(packageName, R.layout.widget);
            PendingIntent iconIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
            views.setOnClickPendingIntent(R.id.logo, iconIntent);

            PendingIntent fullNoteIntent = PendingIntent.getActivity(context, 0, new Intent(context, AddFullActivity.class), 0);
            views.setOnClickPendingIntent(R.id.add_note, fullNoteIntent);

            PendingIntent syncIntent = PendingIntent.getService(context, 0, new Intent(context, MKSyncService.class), 0);
            views.setOnClickPendingIntent(R.id.refesh, syncIntent);


            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        String action = intent.getAction();

        String key = intent.getStringExtra("key");
        User user = Inote.getUser();
        if (user == null || user.isOffLineUser()) {
            nearlyList.clear();
        }
        if (Consts.SERVICE_BEGIN.equals(key)) {
            views.setViewVisibility(R.id.progress, View.VISIBLE);
            views.setViewVisibility(R.id.refesh, View.GONE);
            updateWidget(context, views);
            return;
        } else if (Consts.SERVICE_END.equals(key)) {
            views.setViewVisibility(R.id.progress, View.GONE);
            views.setViewVisibility(R.id.refesh, View.VISIBLE);
            updateWidget(context, views);
            return;
        } else if (key == null) {
            views.setViewVisibility(R.id.progress, View.GONE);
            views.setViewVisibility(R.id.refesh, View.VISIBLE);
        }


        if ("com.sdo.note.opennote_item1".equals(action)) {
            if (nearlyList.size() < 1) {
                return;
            }
            Intent intent1 = new Intent(context, NoteActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent1.putExtra("id", nearlyList.get(0).get_ID());
            context.startActivity(intent1);
            return;
        } else if ("com.sdo.note.opennote_item2".equals(action)) {
            if (nearlyList.size() < 2) {
                return;
            }
            Intent intent1 = new Intent(context, NoteActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent1.putExtra("id", nearlyList.get(1).get_ID());
            context.startActivity(intent1);
            return;
        } else if ("com.sdo.note.opennote_item3".equals(action)) {
            if (nearlyList.size() < 3) {
                return;
            }
            Intent intent1 = new Intent(context, NoteActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent1.putExtra("id", nearlyList.get(2).get_ID());
            context.startActivity(intent1);
            return;
        }


        PendingIntent iconIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
        views.setOnClickPendingIntent(R.id.logo, iconIntent);

        PendingIntent fullNoteIntent = PendingIntent.getActivity(context, 0, new Intent(context, AddFullActivity.class), 0);
        views.setOnClickPendingIntent(R.id.add_note, fullNoteIntent);

        PendingIntent syncIntent = PendingIntent.getService(context, 0, new Intent(context, MKSyncService.class), 0);
        views.setOnClickPendingIntent(R.id.refesh, syncIntent);


        if (user == null || user.isOffLineUser()) {
            views.setTextViewText(R.id.updatetime, context.getString(R.string.please_login_first_warring));
        } else {
            nearlyList = Inote.instance.getNearlyNoteList("3");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(context.getResources().getString(R.string.last_sync_time_format));
            views.setTextViewText(R.id.updatetime, simpleDateFormat.format(new Date()));
        }
        cleanTextViewText(views);
        updateTextViewText(views);
        updateEventHandler(context, views);
        updateWidget(context, views);
    }

    private void updateWidget(Context context, RemoteViews views) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        appWidgetManager.updateAppWidget(new ComponentName(context, WidgetComponent.class), views);
    }

    private void cleanTextViewText(RemoteViews views) {
        for (int i = 0; i < titles.length; i++) {
            views.setTextViewText(titles[i], "");
            views.setTextViewText(abstracts[i], "");
        }
    }

    private void updateTextViewText(RemoteViews views) {
        for (int i = 0; i < nearlyList.size(); i++) {
            Note note = nearlyList.get(i);
            views.setTextViewText(titles[i], note.getTitle());
            views.setTextViewText(abstracts[i], note.getAbstract());
        }
    }

    private void updateEventHandler(Context context, RemoteViews views) {
        Intent intent = new Intent("com.sdo.note.opennote_item1");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        views.setOnClickPendingIntent(items[0], pendingIntent);

        intent = new Intent("com.sdo.note.opennote_item2");
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        views.setOnClickPendingIntent(items[1], pendingIntent);

        intent = new Intent("com.sdo.note.opennote_item3");
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        views.setOnClickPendingIntent(items[2], pendingIntent);
    }
}
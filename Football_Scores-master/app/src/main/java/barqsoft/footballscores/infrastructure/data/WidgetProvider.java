package barqsoft.footballscores.infrastructure.data;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import barqsoft.footballscores.R;
import barqsoft.footballscores.infrastructure.service.MyFetchService;
import barqsoft.footballscores.infrastructure.service.WidgetService;

/**
 * Created by michaelbabenkov on 22/07/15.
 */
public class WidgetProvider extends AppWidgetProvider {

    private static final String TAG = WidgetProvider.class.getSimpleName();
    public static final String EXTRA_WIDGET_ID = TAG + ":widget_id";

    @Override
    public void onReceive(Context context, Intent intent) {

            super.onReceive(context, intent);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            //start service for fetching fresh data
            final Intent intent = new Intent(context, MyFetchService.class);
            intent.putExtra(EXTRA_WIDGET_ID, appWidgetId);
            context.startService(intent);

            //start service for widget views
            final Intent widgetIntent=new Intent(context, WidgetService.class);
            widgetIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            widgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            widgetIntent.setData(Uri.parse(widgetIntent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.fragment_main);
            remoteViews.setRemoteAdapter(R.id.scores_list, widgetIntent);

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

}

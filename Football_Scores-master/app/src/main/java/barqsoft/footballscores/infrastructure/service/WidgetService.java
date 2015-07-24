package barqsoft.footballscores.infrastructure.service;

import android.content.Intent;
import android.widget.RemoteViewsService;

import barqsoft.footballscores.infrastructure.adapters.WidgetViewsFactory;

/**
 * Created by michaelbabenkov on 23/07/15.
 */
public class WidgetService extends RemoteViewsService{
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return(new WidgetViewsFactory(this.getApplicationContext(),
                intent));
    }
}

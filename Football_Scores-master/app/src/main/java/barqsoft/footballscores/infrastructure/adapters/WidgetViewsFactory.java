package barqsoft.footballscores.infrastructure.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import barqsoft.footballscores.R;
import barqsoft.footballscores.infrastructure.data.DatabaseContract;

/**
 * Created by michaelbabenkov on 23/07/15.
 */
public class WidgetViewsFactory implements RemoteViewsService.RemoteViewsFactory{
    private Context mContext;
    private Cursor mCursor;

    public static final String CONTENT_AUTHORITY = "barqsoft.footballscores";
    public static final String PATH = "scores";
    public static Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
    public static Uri SCORES_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH)
            .build();

    public WidgetViewsFactory(Context context, Intent intent) {
        this.mContext=context;

    }
    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return getCursorCount();
    }


    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews row=
                new RemoteViews(mContext.getPackageName(), R.layout.scores_list_item);

        if(!mCursor.moveToNext()){
            return null;
        }

        final String homeTeamName = mCursor.getString(
                mCursor.getColumnIndex(DatabaseContract.ScoresTable.HOME_COL));
        row.setTextViewText(R.id.home_name, homeTeamName);

        final String awayTeamName = mCursor.getString(
                mCursor.getColumnIndex(DatabaseContract.ScoresTable.AWAY_COL));
        row.setTextViewText(R.id.away_name, awayTeamName);

        row.setTextViewText(R.id.data_textview, mCursor.getString(
                mCursor.getColumnIndex(DatabaseContract.ScoresTable.DATE_COL)));

        final int homeGoals = mCursor.getInt(
                mCursor.getColumnIndex(DatabaseContract.ScoresTable.HOME_GOALS_COL));
        final int awayGoals = mCursor.getInt(
                mCursor.getColumnIndex(DatabaseContract.ScoresTable.AWAY_GOALS_COL));

        row.setTextViewText(R.id.score_textview, getScores(homeGoals, awayGoals));

        row.setImageViewResource(R.id.home_crest, getTeamCrestByTeamName(
                homeTeamName));
        row.setImageViewResource(R.id.away_crest, getTeamCrestByTeamName(
                awayTeamName));


        return(row);
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    //get list size
    public int getCursorCount(){
        final Cursor cursor = mContext.getContentResolver().query(SCORES_CONTENT_URI,
                null,
                BaseColumns._ID + ">=?",
                new String []{"0"},
                null );
        final int count = cursor.getCount();
        mCursor = cursor;
        return count;
    }

    public static String getScores(int home_goals,int awaygoals)
    {
        if(home_goals < 0 || awaygoals < 0)
        {
            return " - ";
        }
        else
        {
            return String.valueOf(home_goals) + " - " + String.valueOf(awaygoals);
        }
    }

    public static int getTeamCrestByTeamName (String teamname)
    {
        if (teamname==null){return R.drawable.no_icon;}
        switch (teamname)
        {
            case "Arsenal London FC" : return R.drawable.arsenal;
            case "Manchester United FC" : return R.drawable.manchester_united;
            case "Swansea City" : return R.drawable.swansea_city_afc;
            case "Leicester City" : return R.drawable.leicester_city_fc_hd_logo;
            case "Everton FC" : return R.drawable.everton_fc_logo1;
            case "West Ham United FC" : return R.drawable.west_ham;
            case "Tottenham Hotspur FC" : return R.drawable.tottenham_hotspur;
            case "West Bromwich Albion" : return R.drawable.west_bromwich_albion_hd_logo;
            case "Sunderland AFC" : return R.drawable.sunderland;
            case "Stoke City FC" : return R.drawable.stoke_city;
            default: return R.drawable.no_icon;
        }
    }
}

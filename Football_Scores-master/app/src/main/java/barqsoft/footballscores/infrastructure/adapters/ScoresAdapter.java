package barqsoft.footballscores.infrastructure.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import barqsoft.footballscores.R;
import barqsoft.footballscores.infrastructure.data.DatabaseContract;
import barqsoft.footballscores.infrastructure.utils.Utilities;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class ScoresAdapter extends CursorAdapter
{
    private Long mDetailMatchId = 0L;
    private String FOOTBALL_SCORES_HASHTAG = mContext.getString(R.string.football_hash_tag);

    public ScoresAdapter(Context context, Cursor cursor, int flags)
    {
        super(context,cursor,flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        final View view = LayoutInflater.from(context).inflate(R.layout.scores_list_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor)
    {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        final String homeTeamName = cursor.getString(
                cursor.getColumnIndex(DatabaseContract.ScoresTable.HOME_COL));
        viewHolder.mHomeName.setText(homeTeamName);

        final String awayTeamName = cursor.getString(
                cursor.getColumnIndex(DatabaseContract.ScoresTable.AWAY_COL));
        viewHolder.mAwayName.setText(awayTeamName);

        viewHolder.mDate.setText( cursor.getString(
                cursor.getColumnIndex(DatabaseContract.ScoresTable.DATE_COL)));

        final int homeGoals = cursor.getInt(
                cursor.getColumnIndex(DatabaseContract.ScoresTable.HOME_GOALS_COL));
        final int awayGoals = cursor.getInt(
                cursor.getColumnIndex(DatabaseContract.ScoresTable.AWAY_GOALS_COL));

        viewHolder.mScore.setText(Utilities.getScores(homeGoals, awayGoals));

        viewHolder.mMatchId = cursor.getLong(
                cursor.getColumnIndex(DatabaseContract.ScoresTable.MATCH_ID));


        viewHolder.mHomeCrest.setImageResource(Utilities.getTeamCrestByTeamName(
                homeTeamName));
        viewHolder.mAwayCrest.setImageResource(Utilities.getTeamCrestByTeamName(
                awayTeamName));

        final View detailInclude = view.findViewById(R.id.details_layout);

        if(viewHolder.mMatchId == mDetailMatchId)
        {

            detailInclude.setVisibility(View.VISIBLE);

            final int league =  cursor.getInt(
                    cursor.getColumnIndex(DatabaseContract.ScoresTable.LEAGUE_COL));
            final int matchDay = cursor.getInt(
                    cursor.getColumnIndex(DatabaseContract.ScoresTable.MATCH_DAY));
            final TextView matchDayTextView = (TextView) detailInclude.findViewById(R.id.matchday_textview);
            matchDayTextView.setText(Utilities.getMatchDay(matchDay,
                    league));

            final TextView leagueTextVIew = (TextView) detailInclude.findViewById(R.id.league_textview);
            leagueTextVIew.setText(Utilities.getLeague(
                    cursor.getInt(cursor.getColumnIndex(DatabaseContract.ScoresTable.LEAGUE_COL))));

            final Button shareButton = (Button) detailInclude.findViewById(R.id.share_button);
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //add Share Action
                    context.startActivity(createShareForecastIntent(viewHolder.mHomeName.getText() + " "
                            + viewHolder.mScore.getText() + " " + viewHolder.mAwayName.getText() + " "));
                }
            });
        }
        else
        {
            detailInclude.setVisibility(View.GONE);
        }

    }
    public Intent createShareForecastIntent(String ShareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, ShareText + FOOTBALL_SCORES_HASHTAG);
        return shareIntent;
    }

    public void setDetailMatchId(Long detailMatchId) {
        this.mDetailMatchId = detailMatchId;
    }

    public class ViewHolder
    {
        public TextView mHomeName;
        public TextView mAwayName;
        public TextView mScore;
        public TextView mDate;
        public ImageView mHomeCrest;
        public ImageView mAwayCrest;
        public long mMatchId;
        public ViewHolder(View view)
        {
            mHomeName = (TextView) view.findViewById(R.id.home_name);
            mAwayName = (TextView) view.findViewById(R.id.away_name);
            mScore = (TextView) view.findViewById(R.id.score_textview);
            mDate = (TextView) view.findViewById(R.id.data_textview);
            mHomeCrest = (ImageView) view.findViewById(R.id.home_crest);
            mAwayCrest = (ImageView) view.findViewById(R.id.away_crest);
        }
    }

}

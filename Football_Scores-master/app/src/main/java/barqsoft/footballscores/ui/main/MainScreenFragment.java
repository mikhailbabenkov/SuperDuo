package barqsoft.footballscores.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import barqsoft.footballscores.R;
import barqsoft.footballscores.infrastructure.adapters.ScoresAdapter;
import barqsoft.footballscores.infrastructure.data.DatabaseContract;
import barqsoft.footballscores.infrastructure.service.MyFetchService;
import barqsoft.footballscores.ui.base.BaseFragment;
import butterknife.Bind;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainScreenFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG = MainScreenFragment.class.getName();
    public static final String ARG_DATE = TAG + ":arg_date";
    @Bind(R.id.scores_list)
    ListView mScoresList;
    private ScoresAdapter mAdapter;
    private String mDate;
    public static final int SCORES_LOADER = 0;
    private int last_selected_item = -1;
    private final Contract sDummyContract = new Contract() {
        @Override
        public Long getSelectedMatchId() {
            return -666L;
        }

        @Override
        public void setSelectedMatchId(Long id) {

        }
    };
    private Contract mContract = sDummyContract;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mContract = (Contract) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(mContract.getClass().getSimpleName() +
                    " is not implemented by " + activity.getClass().getSimpleName());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle bundle = getArguments();
        if (bundle != null) {
            mDate = bundle.getString(ARG_DATE);
        }
        mAdapter = new ScoresAdapter(getActivity(), null, 0);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter.setDetailMatchId(mContract.getSelectedMatchId());
        getLoaderManager().initLoader(SCORES_LOADER, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateScores();
        mScoresList.setAdapter(mAdapter);

        mScoresList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ScoresAdapter.ViewHolder selected = (ScoresAdapter.ViewHolder) view.getTag();
                final long selectedMatchId = selected.mMatchId;
                mContract.setSelectedMatchId(selectedMatchId);
                mAdapter.setDetailMatchId(selectedMatchId);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch (i){
            case SCORES_LOADER:
                return new CursorLoader(getActivity(), DatabaseContract.ScoresTable.buildScoreWithDate(),
                        null, null, new String[]{mDate}, null);
            default:
                return null;
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        final int id = cursorLoader.getId();
        switch (id){
            case SCORES_LOADER:
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    cursor.moveToNext();
                }
                mAdapter.swapCursor(cursor);
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }

    private void updateScores() {
        Intent service_start = new Intent(getActivity(), MyFetchService.class);
        getActivity().startService(service_start);
    }

    public interface Contract {
        Long getSelectedMatchId();
        void setSelectedMatchId(Long id);
    }
}

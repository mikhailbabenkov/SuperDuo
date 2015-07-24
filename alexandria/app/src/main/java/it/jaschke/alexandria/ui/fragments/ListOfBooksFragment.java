package it.jaschke.alexandria.ui.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import butterknife.Bind;
import it.jaschke.alexandria.R;
import it.jaschke.alexandria.api.BookListAdapter;
import it.jaschke.alexandria.api.Callback;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.ui.base.BaseFragment;


public class ListOfBooksFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @Bind(R.id.searchButton)
    ImageButton mSearchButton;
    @Bind(R.id.searchText)
    EditText mSearchText;
    @Bind(R.id.listOfBooks)
    ListView mListOfBooks;
    private BookListAdapter mBookListAdapter;
    private int position = ListView.INVALID_POSITION;

    private final int LOADER_ID = 10;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBookListAdapter = new BookListAdapter(getActivity(), null, 0);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       return inflater.inflate(R.layout.fragment_list_of_books, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(mBookListAdapter.isEmpty()){
            restartLoader();
        }
        mSearchButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ListOfBooksFragment.this.restartLoader();
                    }
                }
        );
        mListOfBooks.setAdapter(mBookListAdapter);
        mListOfBooks.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = mBookListAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    ((Callback) getActivity())
                            .onItemSelected(cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry._ID)));
                }
            }
        });
    }

    @Override
    protected int getTitle() {
        return R.string.books;
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = null;
        String[] selectionArgs = null;
        String searchString = mSearchText == null ? null : mSearchText.getText().toString();

        if(!TextUtils.isEmpty(searchString)){
            searchString = "%" + searchString + "%";
            selection = AlexandriaContract.BookEntry.TITLE + " LIKE ? OR " +
                    AlexandriaContract.BookEntry.SUBTITLE + " LIKE ? ";
            selectionArgs = new String[]{searchString,searchString};
        }

        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mBookListAdapter.swapCursor(data);
        if (position != ListView.INVALID_POSITION) {
            mListOfBooks.smoothScrollToPosition(position);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mBookListAdapter.swapCursor(null);
    }
}

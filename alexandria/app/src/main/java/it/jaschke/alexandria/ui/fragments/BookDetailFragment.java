package it.jaschke.alexandria.ui.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import it.jaschke.alexandria.R;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.services.DownloadImage;
import it.jaschke.alexandria.ui.base.BaseFragment;


public class BookDetailFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EAN_KEY = "EAN";
    private final int LOADER_ID = 10;
    @Bind(R.id.fullBookTitle)
    TextView mFullBookTitle;
    @Bind(R.id.fullBookCover)
    ImageView mFullBookCover;
    @Bind(R.id.fullBookSubTitle)
    TextView mFullBookSubTitle;
    @Bind(R.id.fullBookDesc)
    TextView mFullBookDesc;
    @Bind(R.id.categories)
    TextView mCategories;
    @Bind(R.id.authors)
    TextView mAuthors;
    @Bind(R.id.backButton)
    ImageButton mBackButton;
    @Bind(R.id.delete_button)
    Button mDeleteButton;
    private String mEan;
    private ShareActionProvider mShareActionProvider;
    private Intent mShareIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        final Bundle arguments = getArguments();
        if (arguments != null) {
            mEan = arguments.getString(BookDetailFragment.EAN_KEY);
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_full_book, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FragmentActivity activity = getActivity();
                final Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, mEan);
                bookIntent.setAction(BookService.DELETE_BOOK);
                if (activity != null) {
                    activity.startService(bookIntent);
                    activity.getSupportFragmentManager().popBackStack();
                }
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.book_detail, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        setShareIntent();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(mEan)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            bind(data);
        }
    }

    private void bind(final Cursor data) {
        //set Full book title
        final String bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        mFullBookTitle.setText(bookTitle);
        //set Share button
        mShareIntent = new Intent(Intent.ACTION_SEND);
        mShareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        mShareIntent.setType("text/plain");
        mShareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + bookTitle);
        setShareIntent();
        //set Book subtitle
        final String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        mFullBookSubTitle.setText(bookSubTitle);
        //set Book desk
        final String desc = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.DESC));
        mFullBookDesc.setText(desc);
        //set Authors
        final String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
        if(authors!=null) {
            final String[] authorsArr = authors.split(",");
            mAuthors.setLines(authorsArr.length);
            mAuthors.setText(authors.replace(",", "\n"));
        }
        //set Book cover
        final String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        if (Patterns.WEB_URL.matcher(imgUrl).matches()) {
            new DownloadImage(mFullBookCover).execute(imgUrl);
            mFullBookCover.setVisibility(View.VISIBLE);
        }
        //set Categories
        final String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        mCategories.setText(categories);

        if (getActivity().findViewById(R.id.right_container) != null) {
            mBackButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    protected int getTitle() {
        return R.string.books;
    }

    private void setShareIntent(){
        if(mShareActionProvider != null && mShareIntent != null){
            mShareActionProvider.setShareIntent(mShareIntent);
        }
    }
}
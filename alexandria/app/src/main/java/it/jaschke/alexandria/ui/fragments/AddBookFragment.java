package it.jaschke.alexandria.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.Bind;
import info.vividcode.android.zxing.CaptureActivity;
import info.vividcode.android.zxing.CaptureActivityIntents;
import info.vividcode.android.zxing.CaptureResult;
import it.jaschke.alexandria.R;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.ui.base.BaseFragment;


public class AddBookFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = AddBookFragment.class.getSimpleName();
    @Bind(R.id.ean)
    EditText mEan;
    @Bind(R.id.scan_button)
    Button mScanButton;
    @Bind(R.id.bookTitle)
    TextView mBookTitle;
    @Bind(R.id.bookSubTitle)
    TextView mBookSubTitle;
    @Bind(R.id.bookCover)
    ImageView mBookCover;
    @Bind(R.id.authors)
    TextView mAuthors;
    @Bind(R.id.categories)
    TextView mCategories;
    @Bind(R.id.delete_button)
    ImageButton mDeleteButton;
    @Bind(R.id.save_button)
    ImageButton mSaveButton;
    private final int LOADER_ID = 1;
    private final String EAN_CONTENT = "eanContent";
    private static final int REQUEST_SCANNING = 666;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       return inflater.inflate(R.layout.fragment_add_book, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            mEan.setText(savedInstanceState.getString(EAN_CONTENT));
        }

        mEan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //no need
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //no need
            }

            @Override
            public void afterTextChanged(Editable s) {
                String ean = s.toString();
                //catch isbn10 numbers
                if (ean.length() == 10 && !ean.startsWith("978")) {
                    ean = "978" + ean;
                }
                //Once we have an ISBN, start a book intent
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, ean);
                bookIntent.setAction(BookService.FETCH_BOOK);
                getActivity().startService(bookIntent);
                AddBookFragment.this.restartLoader();
            }
        });

        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent captureIntent = new Intent(getActivity(), CaptureActivity.class);
                // Using `CaptureActivityIntents`, set parameters to an intent.
                // (There is no requisite parameter to set to an intent.)
                // For instance, `setPromptMessage` method set prompt message displayed on `CaptureActivity`.
                CaptureActivityIntents.setPromptMessage(captureIntent, "Barcode scanning...");
                // Start activity.
                startActivityForResult(captureIntent, REQUEST_SCANNING);

            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFields();
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, mEan.getText().toString());
                bookIntent.setAction(BookService.DELETE_BOOK);
                getActivity().startService(bookIntent);
                clearFields();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EAN_CONTENT, mEan.getText().toString());

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        CursorLoader cursorLoader;

        if (TextUtils.isEmpty(mEan.getText())) {
            cursorLoader =  null;
        }else{
            String eanStr = mEan.getText().toString();
            //TODO: fix magic numbers
            if (eanStr.length() == 10 && !eanStr.startsWith("978")) {
                eanStr = "978" + eanStr;
            }
            cursorLoader =  new CursorLoader(
                    getActivity(),
                    AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(eanStr)),
                    null,
                    null,
                    null,
                    null
            );
        }

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            bind(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    protected int getTitle() {
        return R.string.scan;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_SCANNING && resultCode == Activity.RESULT_OK) {
            final CaptureResult result = CaptureResult.parseResultIntent(data);
            mEan.setText(result.getContents());
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



    private void bind(final Cursor cursor){
        //set Title
        final String bookTitle = cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        mBookTitle.setText(bookTitle);
        //set Subtitle
        final String bookSubTitle = cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        mBookSubTitle.setText(bookSubTitle);
        //set Authors
        final String authors = cursor.getString(cursor.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
        if(authors!=null) {
            final String[] authorsArr = authors.split(",");
            mAuthors.setLines(authorsArr.length);
            mAuthors.setText(authors.replace(",", "\n"));
        }
        //set Book cover
        final String imgUrl = cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        if (Patterns.WEB_URL.matcher(imgUrl).matches()) {
            mBookCover.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(this).load(imgUrl).into(mBookCover);
            mBookCover.setVisibility(View.VISIBLE);
        }
        //set Categories
        final String categories = cursor.getString(cursor.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        mCategories.setText(categories);
        //set visibility 'save' and 'delete' buttons
        mSaveButton.setVisibility(View.VISIBLE);
        mDeleteButton.setVisibility(View.VISIBLE);
    }

    private void clearFields() {
        mEan.setText(null);
        mBookTitle.setText(null);
        mBookSubTitle.setText(null);
        mAuthors.setText(null);
        mCategories.setText(null);
        mBookCover.setVisibility(View.INVISIBLE);
        mSaveButton.setVisibility(View.INVISIBLE);
        mDeleteButton.setVisibility(View.INVISIBLE);
    }
    private void restartLoader() {
        getLoaderManager().destroyLoader(LOADER_ID);
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

}

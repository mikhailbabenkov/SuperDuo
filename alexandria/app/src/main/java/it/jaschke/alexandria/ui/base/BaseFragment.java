package it.jaschke.alexandria.ui.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import butterknife.ButterKnife;

/**
 * Created by michaelbabenkov on 20/07/15.
 */
public abstract class BaseFragment extends Fragment  {
    private final BaseCallback sDummyCallback = new BaseCallback() {
        @Override
        public boolean isTablet() {
            return false;
        }

    };
    private BaseCallback mBaseCallback = sDummyCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mBaseCallback = (BaseCallback) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(mBaseCallback.getClass().getSimpleName()+
                    " is not implemented by" + activity.getClass().getSimpleName());
        }

        activity.setTitle(getTitle());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mBaseCallback = sDummyCallback;
    }

    protected abstract int getTitle();

    protected BaseCallback getBaseCallback() {
        return mBaseCallback;
    }

    public interface BaseCallback{
        boolean isTablet();
    }
}

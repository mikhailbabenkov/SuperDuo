package it.jaschke.alexandria.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.ui.base.BaseFragment;


public class AboutFragment extends BaseFragment {

    @Override
    protected int getTitle() {
        return R.string.about;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

}

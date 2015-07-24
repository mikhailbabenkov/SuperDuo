package it.jaschke.alexandria.api;

import it.jaschke.alexandria.ui.base.BaseFragment;

/**
 * Created by saj on 25/01/15.
 */
public interface Callback extends BaseFragment.BaseCallback{
    void onItemSelected(String ean);
}

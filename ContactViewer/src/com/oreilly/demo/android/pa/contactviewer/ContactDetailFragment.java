/* $Id: $
 */
package com.oreilly.demo.android.pa.contactviewer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * ContactDetailFragment
 */
public class ContactDetailFragment extends Fragment {
    /**
     * @return a new ContactDetailFragment
     */
    public static final ContactDetailFragment newInstance() {
        return newInstance(null, null);
    }

    /**
     * @param contactId
     * @param contact
     * @return a new ContactDetailFragment
     */
    public static final ContactDetailFragment newInstance(
        String contactId,
        String contact)
    {
        Bundle init = new Bundle();
        init.putString(ContactDetails.TAG_ID, contactId);
        init.putString(ContactDetails.TAG_CONTACT, contact);

        return newInstance(init);
    }

    /**
     * @param init
     * @return a new ContactDetailFragment
     */
    public static final ContactDetailFragment newInstance(Bundle init) {
        ContactDetailFragment frag = new ContactDetailFragment();
        frag.setArguments(init);
        return frag;
    }


    private ContactDetails details;

    /** @see android.support.v4.app.Fragment#onCreate(android.os.Bundle) */
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        if (null == state) { state = getArguments(); }

        details = new ContactDetails(state);

        details.createLoaders(getActivity());
    }

    /** @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle) */
    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle b)
    {
        View view = inflater.inflate(
            R.layout.contact_detail,
            container,
            false);  //!!! this is important

        details.setView(view);

        details.populateContact();

        for (ContactDetailsLoader loader
            : details.initLoaders(getLoaderManager()).values())
        {
            loader.populateFields();
        }

        return view;
    }

    /** @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle) */
    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        details.saveToBundle(state);
    }
}

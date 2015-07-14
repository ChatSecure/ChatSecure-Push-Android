package org.chatsecure.pushdemo.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.chatsecure.pushdemo.R;
import org.chatsecure.pushsecure.pushsecure.PushSecureClient;
import org.chatsecure.pushsecure.pushsecure.response.Account;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AccountRegistrationListener} interface
 * to handle interaction events.
 */
public class RegistrationFragment extends Fragment implements View.OnClickListener {

    @Bind(R.id.username)
    EditText usernameEditText;

    @Bind(R.id.password)
    EditText passwordEditText;

    @Bind(R.id.createAccountButton)
    Button signupButton;

    @Bind(R.id.container)
    ViewGroup container;

    private PushSecureClient client;
    private AccountRegistrationListener mListener;

    public static RegistrationFragment newInstance(PushSecureClient client) {
        RegistrationFragment frag = new RegistrationFragment();
        frag.setPushSecureClient(client);
        return frag;
    }

    public RegistrationFragment() {
        // Required empty public constructor
    }

    public void setPushSecureClient(PushSecureClient client) {
        this.client = client;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_push_secure_registration, container, false);
        ButterKnife.bind(this, root);

        signupButton.setOnClickListener(this);
        return root;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (AccountRegistrationListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {

        setEntryViewsEnabled(false);
        signupButton.setText(R.string.signing_up);

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (client == null) throw new IllegalStateException("PushSecureClient not set!");

        client.authenticateAccount(null, username, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mListener::onAccountCreated,
                        throwable -> {
                            Timber.e(throwable, getActivity().getString(R.string.failed_to_create_account));
                            setEntryViewsEnabled(true);
                            signupButton.setText(R.string.create_account);
                            Snackbar.make(container, R.string.failed_to_create_account, Snackbar.LENGTH_LONG)
                                    .show();
                        });
    }

    private void setEntryViewsEnabled(boolean isEnabled) {
        usernameEditText.setEnabled(isEnabled);
        passwordEditText.setEnabled(isEnabled);
        signupButton.setEnabled(isEnabled);
    }

    public interface AccountRegistrationListener {
        void onAccountCreated(Account account);
    }
}

package org.chatsecure.pushdemo.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.jakewharton.rxbinding.widget.RxTextView;

import org.chatsecure.pushdemo.R;
import org.chatsecure.pushsecure.PushSecureClient;
import org.chatsecure.pushsecure.response.Account;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.Response;
import rx.Observable;
import rx.Subscription;
import timber.log.Timber;

/**
 * Account registration UI
 * <p>
 * The host {@link Activity} must implement {@link AccountRegistrationListener} to be notified
 * of account creation.
 */
public class RegistrationFragment extends Fragment implements View.OnClickListener {

    @Bind(R.id.usernameLayout)
    TextInputLayout usernameLayout;

    @Bind(R.id.username)
    EditText usernameEditText;

    @Bind(R.id.passwordLayout)
    TextInputLayout passwordLayout;

    @Bind(R.id.password)
    EditText passwordEditText;

    @Bind(R.id.createAccountButton)
    Button signupButton;

    @Bind(R.id.container)
    ViewGroup container;

    private PushSecureClient client;
    private AccountRegistrationListener mListener;
    private Subscription userInputSubscription;

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

        userInputSubscription =
                Observable.merge(RxTextView.textChanges(usernameEditText),
                        RxTextView.textChanges(passwordEditText))

                .distinctUntilChanged(textChangedEvent ->
                usernameEditText.getText().hashCode() ^
                        passwordEditText.getText().hashCode())

                .subscribe(onTextChangeEvent -> {
                    signupButton.setVisibility(checkUserPasswordEntry(false) ? View.VISIBLE : View.INVISIBLE);
                });

        passwordEditText.setOnEditorActionListener((view, actionId, event) -> {
            if (checkUserPasswordEntry(true)) {
                onClick(null);
            }
            return false;
        });

        signupButton.setOnClickListener(this);
        return root;
    }

    @Override
    public void onDestroyView () {
        super.onDestroyView();

        if (userInputSubscription != null) {
            userInputSubscription.unsubscribe();
            userInputSubscription = null;
        }
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

        client.authenticateAccount(username, password, null, new PushSecureClient.RequestCallback<Account>() {
            @Override
            public void onSuccess(Account response) {
                mListener.onAccountCreated(response);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Timber.e(throwable, getActivity().getString(R.string.failed_to_create_account));
                setEntryViewsEnabled(true);
                signupButton.setText(R.string.create_account);
                Snackbar.make(container, R.string.failed_to_create_account, Snackbar.LENGTH_LONG)
                        .show();
            }
        });
    }

    private boolean checkUserPasswordEntry(boolean showError) {
        boolean usernameValid = !TextUtils.isEmpty(usernameEditText.getText());
        boolean passwordValid = !TextUtils.isEmpty(passwordEditText.getText());

        if (showError) {
            usernameLayout.setError(usernameValid ? null : "Enter a username");
            passwordLayout.setError(passwordValid ? null : "Enter a password");
        } else {
            // Even if error show not requested, we should always clear errors
            // that are no longer valid
            if (usernameValid) usernameLayout.setError(null);
            if (passwordValid) passwordLayout.setError(null);
        }

        return usernameValid && passwordValid;
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

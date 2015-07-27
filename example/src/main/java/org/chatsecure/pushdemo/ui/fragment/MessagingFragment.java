package org.chatsecure.pushdemo.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.chatsecure.pushdemo.DataProvider;
import org.chatsecure.pushdemo.R;
import org.chatsecure.pushsecure.pushsecure.PushSecureClient;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.RetrofitError;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * UI for obtaining and sharing push tokens on behalf of the current account,
 * as well as sending data payloads to push tokens.
 */
public class MessagingFragment extends Fragment implements View.OnClickListener {

    private PushSecureClient client;
    private DataProvider provider;

    @Bind(R.id.container)
    ViewGroup container;

    @Bind(R.id.sharePushTokenButton)
    Button shareTokenButton;

    @Bind(R.id.payloadEditText)
    EditText payloadEditText;

    @Bind(R.id.peerTokenEditText)
    EditText peerTokenEditText;

    @Bind(R.id.sendMessageButton)
    Button sendMessageButton;

    public static MessagingFragment newInstance(PushSecureClient client, DataProvider provider) {
        MessagingFragment fragment = new MessagingFragment();
        fragment.setPushSecureClient(client);
        fragment.setDataProvider(provider);
        return fragment;
    }

    public MessagingFragment() {
        // Required empty public constructor
    }

    public void setPushSecureClient(PushSecureClient client) {
        this.client = client;
    }

    public void setDataProvider(DataProvider provider) {
        this.provider = provider;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_messaging, container, false);
        ButterKnife.bind(this, root);

        shareTokenButton.setOnClickListener(this);
        sendMessageButton.setOnClickListener(this);


        return root;
    }

    @Override
    public void onClick(@NonNull final View button) {
        switch (button.getId()) {
            case R.id.sharePushTokenButton:

                button.setEnabled(false);
                client.createToken(provider.getGcmToken(), null)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(newToken -> {
                            button.setEnabled(true);
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_TEXT, newToken.token);
                            startActivity(Intent.createChooser(shareIntent, "Share Push Token"));
                        }, throwable -> {
                            Timber.e(throwable, "Error fetching new token");
                            button.setEnabled(true);
                            Snackbar.make(container, "Error fetching new token", Snackbar.LENGTH_SHORT)
                                    .show();
                        });
                break;

            case R.id.sendMessageButton:

                button.setEnabled(false);
                client.sendMessage(peerTokenEditText.getText().toString(), payloadEditText.getText().toString())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(message -> {
                            button.setEnabled(true);
                            String feedbackMessage = "Sent Message";
                            Timber.d(feedbackMessage);
                            Snackbar.make(container, feedbackMessage, Snackbar.LENGTH_SHORT)
                                    .show();
                        }, throwable -> {
                            String message = "Error sending message.";
                            if (throwable instanceof RetrofitError && ((RetrofitError) throwable).getResponse().getStatus() == 404)
                                message += " Push token may be invalid.";

                            button.setEnabled(true);
                            Timber.e(throwable, message);
                            Snackbar.make(container, message, Snackbar.LENGTH_SHORT)
                                    .show();
                        });
                break;
        }
    }
}

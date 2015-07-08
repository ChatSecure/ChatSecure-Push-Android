package org.chatsecure.pushdemo.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import timber.log.Timber;

public class MessagingFragment extends Fragment implements View.OnClickListener {

    private PushSecureClient client;
    private DataProvider provider;

    @Bind(R.id.sharePushTokenButton)
    Button shareTokenButton;

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
    public void onClick(@NonNull View button) {
        switch (button.getId()) {
            case R.id.sharePushTokenButton:
                client.createToken(provider.getGcmToken(), null)
                        .subscribe(newToken -> {
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_TEXT, newToken.token);
                            startActivity(Intent.createChooser(shareIntent, "Share Push Token"));
                        });
                break;

            case R.id.sendMessageButton:
                client.sendMessage(peerTokenEditText.getText().toString(), "hello")
                        .subscribe(message -> Timber.d("Sent message"));
                break;
        }
    }
}

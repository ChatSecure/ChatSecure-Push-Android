package org.chatsecure.pushdemo.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.chatsecure.pushdemo.DataProvider;
import org.chatsecure.pushdemo.R;
import org.chatsecure.pushdemo.ui.adapter.TokenAdapter;
import org.chatsecure.pushsecure.PushSecureClient;
import org.chatsecure.pushsecure.response.PushToken;
import org.chatsecure.pushsecure.response.TokenList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.Response;
import timber.log.Timber;

/**
 * UI for managing a user's tokens
 */
public class TokensFragment extends Fragment implements TokenAdapter.Listener {

    private PushSecureClient client;
    private DataProvider provider;
    private TokenAdapter adapter;

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    @Bind(R.id.progressBar)
    ProgressBar progressIndicator;

    @Bind(R.id.emptyText)
    TextView emptyText;

    public static TokensFragment newInstance(PushSecureClient client, DataProvider provider) {
        TokensFragment fragment = new TokensFragment();
        fragment.setPushSecureClient(client);
        fragment.setDataProvider(provider);
        return fragment;
    }

    public TokensFragment() {
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
        View root = inflater.inflate(R.layout.recyclerview, container, false);
        ButterKnife.bind(this, root);
        adapter = new TokenAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        displayTokens();
        return root;
    }

    private void displayTokens() {
        client.getTokens(new PushSecureClient.RequestCallback<TokenList>() {
            @Override
            public void onSuccess(TokenList response) {
                adapter.setTokens(response.results);
                progressIndicator.setVisibility(View.GONE);
                maybeDisplayEmptyText();
            }

            @Override
            public void onFailure(Throwable throwable) {
                String message = "Failed to fetch tokens";
                Timber.e(throwable, message);
                Snackbar.make(recyclerView, message, Snackbar.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private void maybeDisplayEmptyText() {
        if (adapter.getItemCount() == 0) {
            emptyText.setText(R.string.you_have_no_tokens);
            emptyText.setVisibility(View.VISIBLE);
        } else {
            emptyText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRevokeTokenRequested(PushToken token) {
        client.deleteToken(token.token, new PushSecureClient.RequestCallback<Void>() {
            @Override
            public void onSuccess(Void response) {
                Timber.d("Delete token");
                adapter.removeToken(token);
                maybeDisplayEmptyText();
            }

            @Override
            public void onFailure(Throwable throwable) {
                String message = "Failed to delete token";
                Timber.e(throwable, message);
                Snackbar.make(recyclerView, message, Snackbar.LENGTH_SHORT)
                        .show();
            }
        });
    }
}

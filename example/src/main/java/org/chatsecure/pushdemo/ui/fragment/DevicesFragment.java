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
import org.chatsecure.pushdemo.ui.adapter.DeviceAdapter;
import org.chatsecure.pushsecure.PushSecureClient;
import org.chatsecure.pushsecure.response.Device;
import org.chatsecure.pushsecure.response.DeviceList;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * UI for managing a user's devices
 */
public class DevicesFragment extends Fragment implements DeviceAdapter.Listener {

    private PushSecureClient client;
    private DataProvider provider;
    private DeviceAdapter adapter;

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    @Bind(R.id.progressBar)
    ProgressBar progressIndicator;

    @Bind(R.id.emptyText)
    TextView emptyText;

    public static DevicesFragment newInstance(PushSecureClient client, DataProvider provider) {
        DevicesFragment fragment = new DevicesFragment();
        fragment.setPushSecureClient(client);
        fragment.setDataProvider(provider);
        return fragment;
    }

    public DevicesFragment() {
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
        View root = inflater.inflate(R.layout.recyclerview, container, false);
        ButterKnife.bind(this, root);

        adapter = new DeviceAdapter(provider.getDevice(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        displayDevices();
        return root;
    }

    private void displayDevices() {
        // TODO : Combined APNS + GCM Devices call
        client.getGcmDevices(new PushSecureClient.RequestCallback<DeviceList>() {
            @Override
            public void onSuccess(DeviceList response) {
                adapter.setDevices(response.results);
                progressIndicator.setVisibility(View.GONE);
                maybeDisplayEmptyText();
            }

            @Override
            public void onFailure(Throwable t) {
                String message = "Failed to fetch devices";
                Timber.e(t, message);
                Snackbar.make(recyclerView, message, Snackbar.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private void maybeDisplayEmptyText() {
        if (adapter.getItemCount() == 0) {
            emptyText.setText(R.string.you_have_no_devices);
            emptyText.setVisibility(View.VISIBLE);
        } else {
            emptyText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRevokeDeviceRequested(Device device) {
        client.deleteDevice(device.id, new PushSecureClient.RequestCallback<Void>() {
            @Override
            public void onSuccess(Void response) {
                Timber.d("Delete token");
                adapter.removeDevice(device);
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

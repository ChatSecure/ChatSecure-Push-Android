package org.chatsecure.pushdemo.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.chatsecure.pushdemo.DataProvider;
import org.chatsecure.pushdemo.R;
import org.chatsecure.pushdemo.ui.adapter.DeviceAdapter;
import org.chatsecure.pushsecure.PushSecureClient;
import org.chatsecure.pushsecure.response.Device;

import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * UI for managing a user's devices
 */
public class DevicesFragment extends Fragment implements DeviceAdapter.Listener {

    private PushSecureClient client;
    private DataProvider provider;
    private DeviceAdapter adapter;

    private RecyclerView recyclerView;

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
        recyclerView = (RecyclerView) inflater.inflate(R.layout.recyclerview, container, false);
        adapter = new DeviceAdapter(provider.getDevice(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        displayDevices();
        return recyclerView;
    }

    private void displayDevices() {
        client.getAllDevices()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(adapter::setDevices);
    }

    @Override
    public void onRevokeDeviceRequested(Device device) {
        client.deleteDevice(device.id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resp -> {
                            Timber.d("Delete token http response %d", resp.getStatus());
                            adapter.removeDevice(device);
                        },
                        throwable -> {
                            String message = "Failed to delete token";
                            Timber.e(throwable, message);
                            Snackbar.make(recyclerView, message, Snackbar.LENGTH_SHORT)
                                    .show();
                        });
    }
}

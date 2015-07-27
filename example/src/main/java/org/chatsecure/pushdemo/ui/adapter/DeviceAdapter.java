package org.chatsecure.pushdemo.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.chatsecure.pushdemo.R;
import org.chatsecure.pushsecure.response.Device;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Binds {@link org.chatsecure.pushsecure.response.PushToken}s to views within a {@link RecyclerView}
 * Created by dbro on 7/27/15.
 */
public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {

    private SimpleDateFormat sdf = new SimpleDateFormat("EE M/d/yyyy h:mm a", Locale.US);

    public List<Device> devices = new ArrayList<>();

    private Listener listener;

    public DeviceAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
        notifyDataSetChanged();
    }

    public void removeDevice(Device device) {
        int idx = devices.indexOf(device);
        devices.remove(idx);
        notifyItemRemoved(idx);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.device_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Device device = devices.get(position);

        viewHolder.name.setText(device.name != null ? device.name : "Untitled device");
        viewHolder.createdDate.setText("Created " + sdf.format(device.dateCreated));
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView createdDate;

        Button revoke;

        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            createdDate = (TextView) itemView.findViewById(R.id.createdDate);
            revoke = (Button) itemView.findViewById(R.id.revokeButton);

            revoke.setOnClickListener(v -> {
                v.setEnabled(false);
                ((Button) v).setText("Revoking...");
                listener.onRevokeDeviceRequested((Device) v.getTag());
            });
        }
    }

    public interface Listener {

        /**
         * Handle a user request to revoke the given device. When the device is successfully
         * revoked, call {@link #removeDevice(Device)}} to notify the adapter
         * @param device the device to revoke
         */
        void onRevokeDeviceRequested(Device device);
    }
}

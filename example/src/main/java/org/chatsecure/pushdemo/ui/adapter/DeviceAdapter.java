package org.chatsecure.pushdemo.ui.adapter;

import android.graphics.Typeface;
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

    public List<Device> devices = new ArrayList<>();

    private SimpleDateFormat sdf = new SimpleDateFormat("EE M/d/yyyy h:mm a", Locale.US);
    private Listener listener;
    private Device thisDevice;

    public DeviceAdapter(Device thisDevice, Listener listener) {
        this(listener);
        this.thisDevice = thisDevice;
    }

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

        String name = device.name != null ? device.name : "Untitled device";
        viewHolder.name.setText(name + String.format(" (%s)", device.type));
        viewHolder.createdDate.setText("Created " + sdf.format(device.dateCreated));
        viewHolder.revoke.setTag(device);
        viewHolder.revoke.setText(R.string.revoke);

        if (thisDevice != null && thisDevice.id.equals(device.id)) {
            viewHolder.name.setTypeface(viewHolder.name.getTypeface(), Typeface.BOLD);
        } else {
            viewHolder.name.setTypeface(viewHolder.name.getTypeface(), Typeface.NORMAL);
        }

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
                ((Button) v).setText(R.string.revoking);
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

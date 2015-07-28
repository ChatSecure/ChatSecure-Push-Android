package org.chatsecure.pushdemo.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.chatsecure.pushdemo.R;
import org.chatsecure.pushsecure.response.PushToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Binds {@link org.chatsecure.pushsecure.response.PushToken}s to views within a {@link RecyclerView}
 * Created by dbro on 7/27/15.
 */
public class TokenAdapter extends RecyclerView.Adapter<TokenAdapter.ViewHolder> {

    public List<PushToken> tokens = new ArrayList<>();

    private Listener listener;

    public TokenAdapter(Listener listener) {
        this.listener = listener;
    }

    public void removeToken(PushToken token) {
        int idx = tokens.indexOf(token);
        tokens.remove(idx);
        notifyItemRemoved(idx);
    }

    public void setTokens(List<PushToken> tokens) {
        this.tokens = tokens;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.token_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        PushToken token = tokens.get(position);

        viewHolder.name.setText(token.name != null ? token.name : "Untitled token");
        viewHolder.token.setText("Token: " + token.token);
        viewHolder.device.setText("Device: " + token.getDeviceIdentifier());

        viewHolder.revoke.setTag(token);
        viewHolder.revoke.setText(R.string.revoke);

    }

    @Override
    public int getItemCount() {
        return tokens.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView token;
        TextView device;

        Button revoke;

        public ViewHolder(View itemView) {
            super(itemView);

            revoke = (Button) itemView.findViewById(R.id.revokeButton);
            name = (TextView) itemView.findViewById(R.id.name);
            token = (TextView) itemView.findViewById(R.id.token);
            device = (TextView) itemView.findViewById(R.id.device);

            revoke.setOnClickListener(v -> {
                v.setEnabled(false);
                ((Button) v).setText("Revoking...");
                listener.onRevokeTokenRequested((PushToken) v.getTag());
            });
        }
    }

    public interface Listener {

        /**
         * Handle a user request to revoke the given token. When the token is successfully
         * revoked, call {@link #removeToken(PushToken)} to notify the adapter
         * @param token the token to revoke
         */
        void onRevokeTokenRequested(PushToken token);
    }
}

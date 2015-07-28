package org.chatsecure.pushsecure.response;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by davidbrodsky on 6/24/15.
 */
public class Account {

    private static final String NO_ID = "NO_ID";

    public final @NonNull String username;
    public final @NonNull String token;
    public final @NonNull String id;
    public final @Nullable String email;

    public Account(@NonNull String username, @NonNull String token, @Nullable String email) {
        this(username, token, NO_ID, email);
    }


    public Account(@NonNull String username, @NonNull String token, @NonNull String id, @Nullable String email) {
        this.username = username;
        this.token = token;
        this.id = id;

        this.email = email;
    }
}

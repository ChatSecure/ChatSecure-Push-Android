package org.chatsecure.pushsecure.response;

import android.support.annotation.NonNull;

/**
 * Created by davidbrodsky on 6/23/15.
 */
public class TokenList extends List {

    public final @NonNull
    java.util.List<PushToken> results;

    protected TokenList(int count, @NonNull String next, @NonNull String previous, @NonNull java.util.List<PushToken> tokens) {
        super(count, next, previous);
        this.results = tokens;
    }
}

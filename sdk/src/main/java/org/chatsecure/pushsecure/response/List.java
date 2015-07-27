package org.chatsecure.pushsecure.response;

import android.support.annotation.NonNull;

/**
 * Base class for list responses. Subclasses must add a List named "results" of appropriate type
 *
 * Created by davidbrodsky on 7/14/15.
 */
public abstract class List {

    public final int count;
    public final String next;
    public final String previous;

    protected List(int count, @NonNull String next, @NonNull String previous) {
        this.count = count;
        this.next = next;
        this.previous = previous;
    }
}

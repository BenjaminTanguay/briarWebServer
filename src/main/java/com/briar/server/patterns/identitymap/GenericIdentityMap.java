package com.briar.server.patterns.identitymap;

import com.briar.server.constants.Constants;
import com.briar.server.exception.ObjectDeletedException;
import lombok.NonNull;

import java.util.IdentityHashMap;




public class  GenericIdentityMap<Keytype, Payload> {

    private IdentityHashMap<Keytype, ObjectWrapper<Payload>> identityMap;

    public GenericIdentityMap() {
        this.identityMap = new IdentityHashMap<Keytype, ObjectWrapper<Payload>>();
    }

    public boolean doesPayloadExists(@NonNull Keytype key) {
        return this.identityMap.containsKey(key);
    }

    public void addPayload(@NonNull Keytype key, @NonNull Payload payload) {
        ObjectWrapper<Payload> wrapper = new ObjectWrapper(payload);
        this.identityMap.put(key, wrapper);
    }

    public Payload getPayload(@NonNull Keytype key, @NonNull Constants.Lock lock) throws ObjectDeletedException {
        if (!doesPayloadExists(key)) {
            throw new ObjectDeletedException();
        }
        ObjectWrapper<Payload> wrapper = this.identityMap.get(key);

        if (wrapper.isPayloadToBeDeleted()) {
            throw new ObjectDeletedException();
        }
        switch(lock) {
            case reading:
                return startReading(wrapper);
            case writing:
                return startWriting(wrapper);
            case deleting:
                return startDeleting(key, wrapper);
            default:
                return null;
        }
    }

    public void stopReading(@NonNull Keytype key) {
        ObjectWrapper<Payload> wrapper = this.identityMap.get(key);
        wrapper.stopReading();
    }

    public void stopWriting(@NonNull Keytype key) {
        ObjectWrapper<Payload> wrapper = this.identityMap.get(key);
        wrapper.stopWriting();
    }

    private Payload startReading(ObjectWrapper<Payload> wrapper) {
        wrapper.startReading();
        return wrapper.getPayload();
    }

    private Payload startWriting(ObjectWrapper<Payload> wrapper) {
        wrapper.startWriting();
        return wrapper.getPayload();
    }

    private Payload startDeleting(Keytype key, ObjectWrapper<Payload> wrapper) {
        wrapper.startWriting();
        wrapper.startPayloadDeleting();
        Payload payload = wrapper.getPayload();
        this.identityMap.remove(key);
        return payload;
    }
}
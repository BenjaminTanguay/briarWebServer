package com.briar.server.patterns.identitymap;

import static java.lang.Thread.yield;

public class ObjectWrapper<Payload> {

        private Payload payload;
        private int nbObjectReading;
        private boolean isThreadWriting;
        private boolean isToBeDeleted;

        public ObjectWrapper(Payload payload) {
            this.payload = payload;
            this.nbObjectReading = 0;
            this.isThreadWriting = false;
            this.isToBeDeleted = false;
        }

        public Payload getPayload() {
            return this.payload;
        }

        public void setPayload(Payload payload) {
            this.payload = payload;
        }

        public boolean isPayloadToBeDeleted() {
            return this.isToBeDeleted;
        }

        public void startPayloadDeleting() {
            this.isToBeDeleted = true;
        }

        public synchronized void startReading() {
            while(this.isThreadWriting) {
                yield();
            }
            ++this.nbObjectReading;
        }

        public synchronized void startWriting() {
            while(this.nbObjectReading != 0 || this.isThreadWriting) {
                yield();
            }
            startReading();
            this.isThreadWriting = true;
        }

        public synchronized void stopReading() {
            --this.nbObjectReading;
        }

        public synchronized void stopWriting() {
            this.isThreadWriting = false;
        }

}

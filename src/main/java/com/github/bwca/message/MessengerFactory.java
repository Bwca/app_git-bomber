package com.github.bwca.message;

public class MessengerFactory {
    private final static Messenger messenger;

    private MessengerFactory() {
    }

    static {
        try {
            messenger = new Messenger();
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred in creating singleton messenger instance");
        }
    }

    public static Messenger getInstance() {
        return messenger;
    }
}

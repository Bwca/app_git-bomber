package com.github.bwca;

import com.github.bwca.config.ConfigLoader;
import com.github.bwca.config.Config;
import com.github.bwca.message.Messenger;
import com.github.bwca.message.MessengerFactory;

import java.io.IOException;

/**
 * Hello world!
 */
public class App {
    private static final Messenger messenger = MessengerFactory.getInstance();

    public static void main(String[] args) {
        final ConfigLoader configLoader = new ConfigLoader();
        final Config config;
        try {
            config = configLoader.loadConfig();
            messenger.success(config.toString());

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

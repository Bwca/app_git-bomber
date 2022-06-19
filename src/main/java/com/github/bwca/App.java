package com.github.bwca;

import com.github.bwca.commitBomber.BombingResult;
import com.github.bwca.commitBomber.CommitBomber;
import com.github.bwca.config.ConfigLoader;
import com.github.bwca.config.Config;
import com.github.bwca.message.Messenger;
import com.github.bwca.message.MessengerFactory;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;

/**
 * Commit bomber app!
 */
public class App {
    private static final Messenger messenger = MessengerFactory.getInstance();

    public static void main(String[] args) {
        final ConfigLoader configLoader = new ConfigLoader();
        final Config config;
        final CommitBomber commitBomber;
        try {
            config = configLoader.loadConfig();
            commitBomber = new CommitBomber(config);
            BombingResult bombingResult = commitBomber.bomb();
            messenger.success("Bombing finished, "
                    + bombingResult.getDays() + " days affected, "
                    + bombingResult.getCommits() + " commits added in total.");

        } catch (IOException | GitAPIException e) {
            System.out.println(e.getMessage());
        }
    }
}

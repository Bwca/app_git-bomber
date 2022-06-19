package com.github.bwca.commitBomber;

import com.github.bwca.config.Config;
import com.github.bwca.message.Messenger;
import com.github.bwca.message.MessengerFactory;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;

import java.io.*;
import java.time.ZoneId;
import java.util.Date;
import java.time.LocalDate;
import java.util.OptionalLong;
import java.util.Random;
import java.util.stream.Collectors;

public class CommitBomber {
    private final Config config;
    private final Messenger messenger = MessengerFactory.getInstance();
    private final Git git;

    public CommitBomber(Config config) {
        this.config = config;
        git = new Git(config.repository);
    }

    public BombingResult bomb() throws GitAPIException, IOException {
        BombingResult bombingResult = new BombingResult();
        getBranchForBombing(config.branchToBomb);

        File file = createBombingFile();

        PersonIdent defaultCommitter = new PersonIdent(git.getRepository());
        RandomAccessFile writer = new RandomAccessFile(file.getPath(), "rw");

        for (LocalDate d : config.dates.from.datesUntil(config.dates.to)
                .collect(Collectors.toList())) {
            bombingResult.addDay();
            messenger.info("Preparing to bomb day " + d.toString());
            long addedCommits = polluteFileWithRubbish(d, defaultCommitter, writer);
            bombingResult.addCommits(addedCommits);
        }

        writer.close();
        return bombingResult;
    }

    private long polluteFileWithRubbish(LocalDate localDate, PersonIdent defaultCommitter, RandomAccessFile writer) throws IOException, GitAPIException {
        Date date = java.util.Date.from(localDate.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
        PersonIdent committer = new PersonIdent(defaultCommitter, date);


        OptionalLong randomCommitNumber = new Random().longs(config.commits.from, config.commits.to)
                .findFirst();
        long numberOfCommitsForTheGivenDate = randomCommitNumber.isPresent() ? randomCommitNumber.getAsLong() : config.commits.from;

        messenger.info(numberOfCommitsForTheGivenDate + " commits prepared for the day");

        final Random random = new Random();
        final int millisInDay = 24 * 60 * 60 * 1000;
        for (long n = 0; n < numberOfCommitsForTheGivenDate; n++) {
            date.setTime(date.getTime() + (long) random.nextInt(millisInDay));
            writer.seek(0);
            writer.writeBytes(date.toString());

            git.add()
                    .addFilepattern(config.fileName)
                    .call();

            git.commit()
                    .setCommitter(committer)
                    .setMessage("")
                    .call();
        }

        messenger.success("Ausgebombt!");
        return numberOfCommitsForTheGivenDate;

    }

    private File createBombingFile() throws IOException {
        messenger.info("Trying to create file for bombing...");
        File file = new File(git.getRepository().getDirectory().getParent(), config.fileName);
        if (file.createNewFile()) {
            messenger.success("File created!");
        } else {
            messenger.warn("File already exists, its contents are going to be overwritten!");
        }
        return file;
    }

    private void getBranchForBombing(String branchName) throws GitAPIException {
        messenger.info("Trying to checkout the branch...");
        try {
            git.checkout()
                    .setName(branchName)
                    .call();
            messenger.success("Successfully checked out " + branchName);
        } catch (GitAPIException e) {
            messenger.info("Checkout failed, trying to create the branch.");

            git.branchCreate()
                    .setName(branchName)
                    .call();

            messenger.success("Successfully created " + branchName);

            git.checkout()
                    .setName(branchName)
                    .call();
            messenger.success("Successfully checked out " + branchName);
        }
    }
}

package com.github.bwca.config;

import com.github.bwca.message.Messenger;
import com.github.bwca.message.MessengerFactory;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.regex.Pattern;

public class ConfigLoader {

    private final Messenger messenger = MessengerFactory.getInstance();

    public Config loadConfig() throws IOException {
        Repository repository = loadGitRepo();
        messenger.success("Looks good, found the repo");

        messenger.info("Current branch is " + repository.getBranch());

        String branchToBomb = branchToBomb();
        messenger.info("Alight, we're bombing " + branchToBomb);

        Range<LocalDate> days = getDays();
        messenger.success("Bombing range set from " + days.from + " to " + days.to);

        Range<Long> commits = getCommitsRange();
        messenger.success("Commits range set from " + commits.from + " to " + commits.to);

        String fileName = getBombFileName();

        return new Config(
                repository, branchToBomb, days, commits, fileName
        );
    }

    private String getBombFileName() {
        String defaultFileName = "dis-is-da-file-for-da-bombin!";
        String userEnteredFileName = messenger.prompt("Enter file name for commit bombing, or leave blank for default file name, which is " + defaultFileName);
        return Optional.ofNullable(userEnteredFileName).orElse(defaultFileName);
    }

    private String branchToBomb() {
        String branchName;

        String promptLine = "Enter branch name to bomb";
        Pattern branchNamePattern = Pattern.compile("[A-Za-z0-9\\-_]+");
        while (true) {
            try {
                branchName = messenger.prompt(promptLine);
                boolean isOkayNameForABranch = branchNamePattern.matcher(branchName).matches();
                if (isOkayNameForABranch) {
                    return branchName;
                }
                throw new IOException();
            } catch (IOException e) {
                promptLine = null;
                messenger.error("You dillweed, that's not a valid name for a branch! Try again.");
            }
        }
    }

    private Repository loadGitRepo() {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        builder.setMustExist(true);

        String promptLine = "Enter git repo path";
        while (true) {
            String repoPath = messenger.prompt(promptLine);
            repoPath = repoPath.replaceAll("\\\\", "/");

            if (!repoPath.endsWith(".git")) {
                repoPath = repoPath.endsWith("/") ? repoPath.concat(".git") : repoPath.concat("/.git");
            }
            try {
                return builder.setGitDir(new File(repoPath))
                        .build();
            } catch (IOException e) {
                promptLine = null;
                messenger.error("That's not a valid git repo path, you dumbass, try again.");
            }
        }
    }

    private Range<LocalDate> getDays() {
        LocalDate startDate;
        LocalDate endDate;
        String promptLine = "Enter the start date for bombing, in YYYY-MM-DD format";
        while (true) {
            try {
                startDate = LocalDate.parse(messenger.prompt(promptLine));
                break;
            } catch (DateTimeParseException e) {
                promptLine = null;
                messenger.error("That's not a proper date, you jackass, try again");
            }
        }

        promptLine = "Enter the end date for bombing, in YYYY-MM-DD format";
        while (true) {
            try {
                endDate = LocalDate.parse(messenger.prompt(promptLine));
                if (startDate.isBefore(endDate)) {
                    break;
                } else {
                    promptLine = null;
                    messenger.error("End date cannot be before the start date, duh, try again");
                }

            } catch (DateTimeParseException e) {
                promptLine = null;
                messenger.error("That's not a proper date, you jackass, try again");
            }
        }

        return new Range<>(startDate, endDate);
    }

    private Range<Long> getCommitsRange() {
        long min = enterNumber(0);
        long max = enterNumber(min);
        return new Range<>(min, max);
    }

    private long enterNumber(long min) {
        long n;
        String promptLine = "Enter a number, min " + min + ", max " + Long.MAX_VALUE;
        while (true) {
            try {
                n = Long.parseLong(messenger.prompt(promptLine));
                if (n >= min) {
                    return n;
                } else {
                    promptLine = null;
                    messenger.error("The range is " + min + "-" + Long.MAX_VALUE + ", now try again");
                }

            } catch (DateTimeParseException e) {
                promptLine = null;
                messenger.error("That's not a proper number, try again");
            }
        }
    }
}

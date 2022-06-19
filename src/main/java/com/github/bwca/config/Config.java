package com.github.bwca.config;

import org.eclipse.jgit.lib.Repository;

import java.time.LocalDate;

public class Config {
    public final Repository repository;
    public final String branchToBomb;
    public final Range<LocalDate> dates;
    public final Range<Long> commits;
    public final String fileName;

    public Config(Repository repository, String branchToBomb, Range<LocalDate> dates, Range<Long> commits, String fileName) {
        this.repository = repository;
        this.branchToBomb = branchToBomb;
        this.dates = dates;
        this.commits = commits;
        this.fileName = fileName;
    }

}

package com.zyy.demo.entities;

import java.util.Objects;

public class CommitPair {
    public CommitModel commit1;
    public CommitModel commit2;

    public CommitModel getCommit1() {
        return commit1;
    }

    public void setCommit1(CommitModel commit1) {
        this.commit1 = commit1;
    }

    public CommitModel getCommit2() {
        return commit2;
    }

    public void setCommit2(CommitModel commit2) {
        this.commit2 = commit2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommitPair that = (CommitPair) o;
        return (Objects.equals(commit1.getId(), that.commit1.getId()) && Objects.equals(commit2.getId(), that.commit2.getId())) || (Objects.equals(commit1.getId(), that.commit2.getId()) && Objects.equals(commit2.getId(), that.commit1.getId()));
    }

}

package com.mwguy.vgit.ci.dsl

class Job {
    public String name
    public List<String> commands = []

    Job(String name) {
        this.name = name
    }

    void sh(String command) {
        commands << command
    }
}

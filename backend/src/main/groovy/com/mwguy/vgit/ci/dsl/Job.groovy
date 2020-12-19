package com.mwguy.vgit.ci.dsl

class Job {
    public List<String> commands = []

    void sh(String command) {
        commands << command
    }
}

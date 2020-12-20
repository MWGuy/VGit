package com.mwguy.vgit.ci.dsl

import groovy.transform.NamedParam

class Stage {
    public String name
    public List<Job> jobs = []

    Stage(String name) {
        this.name = name
    }

    void job(@NamedParam("name") String name, @DelegatesTo(Job) Closure closure) {
        closure.delegate = new Job(name)
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()

        jobs << (closure.delegate as Job)
    }
}

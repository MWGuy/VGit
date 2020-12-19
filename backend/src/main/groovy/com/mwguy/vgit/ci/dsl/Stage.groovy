package com.mwguy.vgit.ci.dsl

import groovy.transform.NamedParam

class Stage {
    public Map<String, Job> jobs = [:]

    void job(@NamedParam("name") String name, @DelegatesTo(Job) Closure closure) {
        def job = new Job();

        closure.delegate = job
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()

        jobs.put(name, job)
    }
}

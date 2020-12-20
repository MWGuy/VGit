package com.mwguy.vgit.ci.dsl

import groovy.transform.NamedParam

class Pipeline {
    public Map<String, String> environment = [:]
    public List<Stage> stages = []

    void environment(@DelegatesTo(Map) Closure closure) {
        environment.with(closure)
    }

    void stage(@NamedParam("name") String name, @DelegatesTo(Stage) Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = new Stage(name)
        closure.call()

        stages << (closure.delegate as Stage)
    }
}

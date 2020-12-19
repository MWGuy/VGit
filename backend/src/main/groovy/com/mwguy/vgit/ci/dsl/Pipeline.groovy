package com.mwguy.vgit.ci.dsl

import groovy.transform.NamedParam

class Pipeline {
    public Map<String, String> environment = [:]
    public Map<String, Stage> stages = [:]

    void environment(@DelegatesTo(Map) Closure closure) {
        environment.with(closure)
    }

    void stage(@NamedParam("name") String name, @DelegatesTo(Stage) Closure closure) {
        def stage = new Stage();
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = stage
        closure.call()

        stages.put(name, stage)
    }
}

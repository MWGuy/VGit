package com.mwguy.vgit.ci.dsl

class CI {
    public Pipeline pipeline

    void pipeline(@DelegatesTo(value = Pipeline) Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_ONLY
        closure.delegate = pipeline = new Pipeline()
        closure.call()
    }
}

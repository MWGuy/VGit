package com.mwguy.vgit.controllers.graphql;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PingQueryResolver implements GraphQLQueryResolver {
    public String ping() {
        log.info("Sending pong ....");
        return "pong";
    }
}

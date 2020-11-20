package com.mwguy.vgit.controllers.graphql;

import graphql.kickstart.tools.GraphQLQueryResolver;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PingQueryResolver implements GraphQLQueryResolver {
    @GraphQLQuery(name = "ping")
    public String ping() {
        log.info("Sending pong ....");
        return "pong";
    }
}

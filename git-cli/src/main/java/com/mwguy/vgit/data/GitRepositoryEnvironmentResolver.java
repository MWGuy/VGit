package com.mwguy.vgit.data;

import java.util.Collections;
import java.util.Map;

public interface GitRepositoryEnvironmentResolver {
    GitRepositoryEnvironmentResolver EMPTY = Collections::emptyMap;
    Map<String, String> resolve();
}

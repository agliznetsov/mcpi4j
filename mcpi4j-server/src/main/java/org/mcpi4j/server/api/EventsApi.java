package org.mcpi4j.server.api;

import java.util.List;

public interface EventsApi {
    void chatPost(String line);

    void clearEvents();

    List<BlockHit> getBlockHits();

    List<String> getChatPosts();
}
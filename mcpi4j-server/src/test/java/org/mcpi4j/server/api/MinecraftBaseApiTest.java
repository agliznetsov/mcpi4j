package org.mcpi4j.server.api;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MinecraftBaseApiTest {
    @Test
    public void range() {
        String[] source = new String[] {"a", "b", "c", "d"};
        String[] target = MinecraftBaseApi.range(source, 1, 2);
        assertEquals(2, target.length);
        assertEquals("b", target[0]);
        assertEquals("c", target[1]);
    }
}

package org.mcpi4j.server.api;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class MinecraftBaseApi implements MinecraftApi {
    private static final Logger log = Logger.getLogger(MinecraftBaseApi.class);

    private Map<String, Function<String[], Object>> handlers = new HashMap<>();

    public MinecraftBaseApi() {
        initHandlers();
    }

    @Override
    public String handleCommand(String methodName, String... args) {
        try {
            Function<String[], Object> handler = handlers.get(methodName);
            if (handler != null) {
                Object response = handler.apply(args);
                return response == null ? null : response.toString();
            } else {
                return defaultHandler(methodName, args);
            }
        } catch (Exception e) {
            log.error("Method handler failed", e);
            return "Fail";
        }
    }

    protected abstract WorldApi world();

    protected abstract EventsApi events();

    protected abstract PlayerApi player();

    protected String defaultHandler(String methodName, String[] args) {
        return "Fail";
    }

    private void initHandlers() {
        handlers.put("world.getBlock", this::worldGetBlock);
        handlers.put("world.getBlocks", this::worldGetBlocks);
        handlers.put("world.getBlockWithData", this::worldGetBlockWithData);
        handlers.put("world.setBlock", this::worldSetBlock);
        handlers.put("world.setBlocks", this::worldSetBlocks);
        handlers.put("world.getHeight", this::worldGetHeight);
        handlers.put("world.setSign", this::worldSetSign);
        handlers.put("world.spawnEntity", this::worldSpawnEntity);

        handlers.put("chat.post", this::chatPost);
        handlers.put("events.clear", this::eventsClear);
        handlers.put("events.block.hits", this::eventsBlockHits);
        handlers.put("events.chat.posts", this::eventsChatPosts);

        handlers.put("player.getTile", this::playerGetTile);
        handlers.put("player.setTile", this::playerSetTile);
        handlers.put("player.getAbsPos", this::playerGetAbsPos);
        handlers.put("player.setAbsPos", this::playerSetAbsPos);
        handlers.put("player.getPos", this::playerGetPos);
        handlers.put("player.setPos", this::playerSetPos);
//        handlers.put("player.getDirection", this::defaultHandler);
//        handlers.put("player.setDirection", this::defaultHandler);
//        handlers.put("player.getRotation", this::defaultHandler);
//        handlers.put("player.setRotation", this::defaultHandler);
//        handlers.put("player.getPitch", this::defaultHandler);
//        handlers.put("player.setPitch", this::defaultHandler);
//
//        handlers.put("entity.getTile", this::defaultHandler);
//        handlers.put("entity.setTile", this::defaultHandler);
//        handlers.put("entity.getPos", this::defaultHandler);
//        handlers.put("entity.setPos", this::defaultHandler);
//        handlers.put("entity.setDirection", this::defaultHandler);
//        handlers.put("entity.getDirection", this::defaultHandler);
//        handlers.put("entity.getRotation", this::defaultHandler);
//        handlers.put("entity.setRotation", this::defaultHandler);
//        handlers.put("entity.getPitch", this::defaultHandler);
//        handlers.put("entity.setPitch", this::defaultHandler);
//        handlers.put("entity.getName", this::defaultHandler);
    }


    // ============================================ World

    private Object worldGetBlock(String[] args) {
        return world().getBlock(BlockLocation.parse(args));
    }

    private Object worldGetBlocks(String[] args) {
        List<Integer> blocks = world().getBlocks(BlockLocation.parse(range(args, 0, 2)), BlockLocation.parse(range(args, 3, 5)));
        return String.join(",", blocks.stream().map(Object::toString).collect(Collectors.toList()));
    }

    private Object worldGetBlockWithData(String[] args) {
        return world().getBlockWithData(BlockLocation.parse(args));
    }

    private Object worldSetBlock(String[] args) {
        int type = Integer.parseInt(args[3]);
        byte data = args.length > 4 ? Byte.parseByte(args[4]) : (byte) 0;
        world().setBlock(BlockLocation.parse(args), type, data);
        return null;
    }

    private Object worldSetBlocks(String[] args) {
        int type = Integer.parseInt(args[3]);
        byte data = args.length > 4 ? Byte.parseByte(args[4]) : (byte) 0;
        world().setBlocks(
                BlockLocation.parse(range(args, 0, 2)),
                BlockLocation.parse(range(args, 3, 5)),
                type,
                data
        );
        return null;
    }

    private Object worldGetHeight(String[] args) {
        return world().getHeight(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
    }

    private Object worldSetSign(String[] args) {
        int type = Integer.parseInt(args[3]);
        byte data = Byte.parseByte(args[4]);
        String[] lines = range(args, 5, args.length - 1);
        world().setSign(BlockLocation.parse(args), type, data, lines);
        return null;
    }

    private Object worldSpawnEntity(String[] args) {
        int type = Integer.parseInt(args[3]);
        return world().spawnEntity(BlockLocation.parse(args), type);
    }

    // ====================================== Events

    private Object chatPost(String[] args) {
        events().chatPost(String.join(",", args));
        return null;
    }

    private Object eventsClear(String[] args) {
        events().clearEvents();
        return null;
    }

    private Object eventsBlockHits(String[] args) {
        List<BlockHit> hits = events().getBlockHits();
        return String.join("|", hits.stream().map(Object::toString).collect(Collectors.toList()));
    }

    private Object eventsChatPosts(String[] args) {
        return String.join("|", events().getChatPosts());
    }

    // ==================================== Player

    private Object playerGetTile(String[] args) {
        return player().getTile();
    }

    private Object playerSetTile(String[] args) {
        player().setTile(BlockLocation.parse(args));
        return null;
    }

    private Object playerGetAbsPos(String[] args) {
        return player().getAbsPos();
    }

    private Object playerSetAbsPos(String[] args) {
        player().setAbsPos(Location.parse(args));
        return null;
    }

    private Object playerGetPos(String[] args) {
        return player().getPos();
    }

    private Object playerSetPos(String[] args) {
        player().setPos(Location.parse(args));
        return null;
    }


    protected static String[] range(String[] args, int from, int to) {
        int length = (to + 1 - from);
        String[] dest = new String[length];
        System.arraycopy(args, from, dest, 0, length);
        return dest;
    }

}

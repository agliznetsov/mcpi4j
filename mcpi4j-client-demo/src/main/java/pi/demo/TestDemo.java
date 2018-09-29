package pi.demo;

import pi.Block;
import pi.Color;
import pi.Minecraft;
import pi.Vec;

import static pi.Block.*;

/**
 * @author Daniel Frisk, twitter:danfrisk
 */
public class TestDemo {
    public static void main(String[] args) throws Exception {
        Minecraft mc = Minecraft.connect(args);

        mc.postToChat("Hello, world!");

        addBlocks(mc);
//        movePlayer(mc);
    }

    private static void addBlocks(Minecraft mc) throws Exception {
        Vec vec = mc.player.getPosition();
        mc.setBlock(Vec.xyz(vec.x + 4, vec.y, vec.z), STONE);
        for (int x = 0; x < 16; x++) {
            System.out.println(x);
            mc.setBlock(Vec.xyz(vec.x + 5 + x, vec.y, vec.z), Block.wool(Color.fromInt(0)));
        }
    }

    private static void movePlayer(Minecraft mc) throws Exception {
        for (int i = 0; i < 30; i++) {
            Vec vec = mc.player.getPosition();
            System.out.println("Player position: " + vec);
            mc.player.setPosition(Vec.xyz(vec.x, vec.y + 1, vec.z));
            Thread.sleep(100);
        }
    }
}

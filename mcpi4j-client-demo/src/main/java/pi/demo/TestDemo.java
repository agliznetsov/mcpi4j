package pi.demo;

import pi.Block;
import pi.Minecraft;
import pi.Vec;

import static pi.Block.IRON_BLOCK;

/**
 *
 * @author Daniel Frisk, twitter:danfrisk
 */
public class TestDemo {
    public static void main(String[] args) throws Exception {
        Minecraft mc = Minecraft.connect(args);

//        int[] playerIds = mc.getPlayerEntityIds();
//        System.out.println("Players: " + playerIds.length);

        mc.postToChat("Hello, world!");

//        for (int i = 0; i < 8; i++) {
//            for (int j = 0; j < 8; j++) {
//                mc.setBlock(Vec.xyz(i, 2, j), IRON_BLOCK);
//            }
//        }

        mc.setBlock(Vec.xyz(0, 0, 0), IRON_BLOCK);
        Block block = mc.getBlock(Vec.xyz(0, 0, 0));
        System.out.println("Zero block: " + block);

        block = mc.getBlock(Vec.xyz(0, 0, 10000));
        System.out.println("Air block: " + block);

        System.out.println("Press any key");
        System.in.read();
    }
}

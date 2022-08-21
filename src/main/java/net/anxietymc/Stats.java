package net.anxietymc;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

public class Stats {
    // increments when placing any block

    private static int blocksPlaced = 0;

    // increments blocksPlaced by 1, after 7 calls it is set to 0 and extra code is called
    public static void incrementAndUpdatePlacementCount() {
        if (blocksPlaced++ == 7) {
            MinecraftClient client = MinecraftClient.getInstance();

            // null check
            if (client.player != null && client.world != null)  {
                int blockCount = 0;
                boolean lava = false;
                BlockPos playerPos = client.player.getBlockPos(); // player's current position when placing block
                BlockPos currentPos = client.player.getBlockPos(); // block's current position while iterating down
                for (int i = client.player.getBlockPos().getY(); i > client.world.getDimension().minY(); i--) { // iterate through all the blocks under the player
                    if (!(client.world.getBlockState(currentPos).getBlock().equals(Blocks.AIR) || client.world.getBlockState(currentPos).getBlock().equals(Blocks.CAVE_AIR) || client.world.getBlockState(currentPos).getBlock().equals(Blocks.VOID_AIR))) { // air block check
                        if (!client.world.getBlockState(currentPos).getBlock().equals(Blocks.LAVA)) { // lava check
                            blockCount++;
                        } else {
                            boolean canSee = true; // true = all blocks above lava to currentPosition are air or lava
                            for (int j = currentPos.getY(); j < playerPos.getY(); j++) { // iterate up between t
                                if (!(client.world.getBlockState(currentPos).getBlock().equals(Blocks.AIR) || client.world.getBlockState(currentPos).getBlock().equals(Blocks.CAVE_AIR) || client.world.getBlockState(currentPos).getBlock().equals(Blocks.VOID_AIR) || client.world.getBlockState(currentPos).getBlock().equals(Blocks.LAVA))) { // air / lava check
                                    canSee = false;
                                    break;
                                }
                            }

                            if (canSee) {
                                lava = true;
                            }
                        }
                    }
                    currentPos = new BlockPos(currentPos.getX(), currentPos.getY() - 1, currentPos.getZ()); // update currentPosition
                }

                if (blockCount < 10 || lava) { // non-air blocks under player less than 10 or if lava is under the player
                    client.world.playSound(client.player, client.player.getBlockPos(), SoundEvents.AMBIENT_CAVE, SoundCategory.AMBIENT, 1.0F, 1.0F); // play cave ambient sound
                    client.player.sendMessage(Constants.randomAnxietyMessage()); // get and send random anxiety message from Constants.ANXIETY_MESSAGES
                }
            }
            blocksPlaced = 0; // reset blocksPlaced
        }
    }
}

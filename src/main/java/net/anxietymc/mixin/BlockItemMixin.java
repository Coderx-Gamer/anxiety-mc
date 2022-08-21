package net.anxietymc.mixin;

import net.anxietymc.Constants;
import net.anxietymc.Stats;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
    // shadow methods

    @Shadow @Nullable protected abstract BlockState getPlacementState(ItemPlacementContext context);
    @Shadow @Nullable public abstract ItemPlacementContext getPlacementContext(ItemPlacementContext context);
    @Shadow protected abstract boolean postPlacement(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state);
    @Shadow protected abstract BlockState placeFromNbt(BlockPos pos, World world, ItemStack stack, BlockState state);
    @Shadow protected abstract SoundEvent getPlaceSound(BlockState state);
    @Shadow protected abstract boolean place(ItemPlacementContext context, BlockState blockState);

    // insert incrementAndUpdatePlacementCount() call (random)

    @Inject(at = @At("HEAD"), method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;", cancellable = true)
    public void place0(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (!context.canPlace()) {
             cir.setReturnValue(ActionResult.FAIL);
        } else {
            ItemPlacementContext itemPlacementContext = getPlacementContext(context);
            if (itemPlacementContext == null) {
                cir.setReturnValue(ActionResult.FAIL);
            } else {
                BlockState blockState = getPlacementState(itemPlacementContext);
                if (blockState == null) {
                    cir.setReturnValue(ActionResult.FAIL);
                } else if (!place(itemPlacementContext, blockState)) {
                    cir.setReturnValue(ActionResult.FAIL);
                } else {
                    BlockPos blockPos = itemPlacementContext.getBlockPos();
                    World world = itemPlacementContext.getWorld();
                    PlayerEntity playerEntity = itemPlacementContext.getPlayer();
                    ItemStack itemStack = itemPlacementContext.getStack();
                    BlockState blockState2 = world.getBlockState(blockPos);
                    if (blockState2.isOf(blockState.getBlock())) {
                        blockState2 = placeFromNbt(blockPos, world, itemStack, blockState2);
                        postPlacement(blockPos, world, playerEntity, itemStack, blockState2);
                        blockState2.getBlock().onPlaced(world, blockPos, blockState2, playerEntity, itemStack);

                        // inserted code

                        if (world.isClient) {
                            if (new Random().nextInt(6) == 0) {
                                Stats.incrementAndUpdatePlacementCount();
                            }
                        }

                        // end inserted code

                        if (playerEntity instanceof ServerPlayerEntity) {
                            Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity)playerEntity, blockPos, itemStack);
                        }
                    }

                    BlockSoundGroup blockSoundGroup = blockState2.getSoundGroup();
                    world.playSound(playerEntity, blockPos, getPlaceSound(blockState2), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0F) / 2.0F, blockSoundGroup.getPitch() * 0.8F);
                    world.emitGameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Emitter.of(playerEntity, blockState2));
                    if (playerEntity == null || !playerEntity.getAbilities().creativeMode) {
                        itemStack.decrement(1);
                    }

                    cir.setReturnValue(ActionResult.success(world.isClient));
                }
            }
        }
        cir.cancel();
    }
}

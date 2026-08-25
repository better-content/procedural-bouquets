package com.bettercontent.proceduralbouquets.block;

import com.bettercontent.proceduralbouquets.blockentity.BouquetGridBlockEntity;
import com.bettercontent.proceduralbouquets.config.ModCommonConfig;
import com.bettercontent.proceduralbouquets.data.BouquetEntry;
import com.bettercontent.proceduralbouquets.registry.ModItems;
import com.bettercontent.proceduralbouquets.registry.ModTags;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.network.NetworkHooks;

public class BouquetGridBlock extends BaseEntityBlock {
    public static final double TRAY_HEIGHT = 2.0D / 16.0D;
    private static final VoxelShape SHAPE = box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);

    public BouquetGridBlock(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
        return false;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BouquetGridBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof BouquetGridBlockEntity be)) {
            return InteractionResult.PASS;
        }

        ItemStack held = player.getItemInHand(hand);
        boolean emptyHand = held.isEmpty();

        if (player.isShiftKeyDown() && emptyHand) {
            if (player instanceof ServerPlayer serverPlayer) {
                NetworkHooks.openScreen(serverPlayer, be, pos);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (hit.getDirection() != Direction.UP) {
            return InteractionResult.PASS;
        }

        if (!isValidFlower(held)) {
            return InteractionResult.PASS;
        }

        int x = gridCoord(hit.getLocation().x - pos.getX());
        int z = gridCoord(hit.getLocation().z - pos.getZ());

        if (be.getAt(x, z).isPresent()) {
            if (!(player.isShiftKeyDown() && ModCommonConfig.ALLOW_REPLACEMENT_WHEN_SNEAKING.get())) {
                return InteractionResult.CONSUME;
            }
            if (level.isClientSide) {
                return InteractionResult.SUCCESS;
            }
            be.removeAt(x, z).ifPresent(old -> {
                if (!player.isCreative()) {
                    popResource(level, pos, new ItemStack(net.minecraft.core.registries.BuiltInRegistries.ITEM.get(old.itemId())));
                }
            });
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        ResourceLocation id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(held.getItem());
        long seed = pos.asLong() ^ player.getUUID().getLeastSignificantBits() ^ level.getGameTime() ^ id.hashCode() ^ (x * 31L + z);
        int rot = (int) Math.floorMod(seed, 4L);
        float scale = 0.85F + (Math.floorMod(seed >>> 2, 21L) / 100.0F);

        BouquetEntry entry = new BouquetEntry(id, x, z, rot, scale, 0);
        if (!be.addEntry(entry)) {
            return InteractionResult.CONSUME;
        }

        boolean consume = !player.isCreative() || ModCommonConfig.CONSUME_FLOWERS_IN_CREATIVE.get();
        if (consume) {
            held.shrink(1);
        }

        be.markUpdated();
        level.playSound(null, pos, SoundEvents.GRASS_PLACE, SoundSource.BLOCKS, 0.7F, 0.9F + level.random.nextFloat() * 0.2F);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static int gridCoord(double local) {
        return Mth.clamp((int) Math.floor(local * 16.0D), 0, 15);
    }

    public static boolean isValidFlower(ItemStack stack) {
        return !stack.isEmpty() && (stack.is(ModTags.Items.BOUQUET_FLOWERS) || stack.is(ItemTags.FLOWERS));
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = new ArrayList<>();
        if (params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof BouquetGridBlockEntity be) {
            if (!be.getEntriesView().isEmpty()) {
                drops.add(be.createBouquetStack());
            } else {
                drops.add(new ItemStack(ModItems.BOUQUET_GRID_ITEM.get()));
            }
            return drops;
        }
        return super.getDrops(state, params);
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return new ItemStack(ModItems.BOUQUET_GRID_ITEM.get());
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState();
    }
}

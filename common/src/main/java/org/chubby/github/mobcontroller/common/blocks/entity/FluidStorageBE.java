package org.chubby.github.mobcontroller.common.blocks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.chubby.github.mobcontroller.common.blocks.entity.fluid.FluidContainer;
import org.chubby.github.mobcontroller.common.blocks.entity.fluid.IFluidContainerBlock;
import org.jetbrains.annotations.Nullable;

public class FluidStorageBE extends BlockEntity implements IFluidContainerBlock {

    private FluidContainer fluidContainer;
    private final long capacity;

    public FluidStorageBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        this(type, pos, state, FluidContainer.BUCKET_CAPACITY * 16);
    }

    public FluidStorageBE(BlockEntityType<?> type, BlockPos pos, BlockState state, long capacity) {
        super(type, pos, state);
        this.capacity = capacity;
        this.fluidContainer = FluidContainer.create(capacity, this::onFluidChanged);
    }

    @Override
    public @Nullable FluidContainer getFluidContainer() {
        return this.fluidContainer;
    }

    public InteractionResult onUse(Player player, InteractionHand hand, Direction face) {
        InteractionResult bottleResult = this.interactWithBottle(player, hand, this.worldPosition);
        if (bottleResult != InteractionResult.PASS) {
            return bottleResult;
        }

        ItemInteractionResult platformResult = this.performPlatformInteraction(player, hand, this.worldPosition, face);
        return platformResult == ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION ?
                InteractionResult.PASS :
                platformResult.result();
    }

    private void onFluidChanged(FluidContainer container) {
        this.setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            container.sync(this);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        this.fluidContainer.save(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (this.fluidContainer == null) {
            this.fluidContainer = FluidContainer.create(this.capacity, this::onFluidChanged);
        }
        this.fluidContainer.load(tag, registries);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        this.saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        this.loadAdditional(tag, lookupProvider);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public boolean isEmpty() {
        return this.fluidContainer.isEmpty();
    }

    public boolean isFull() {
        return this.fluidContainer.getStoredAmount() >= this.fluidContainer.getCapacity();
    }

    public long getCapacity() {
        return this.capacity;
    }

    public long getStoredAmount() {
        return this.fluidContainer.getStoredAmount();
    }

    public float getFillPercentage() {
        if (this.capacity <= 0) return 0.0f;
        return (float) this.getStoredAmount() / this.capacity;
    }
}
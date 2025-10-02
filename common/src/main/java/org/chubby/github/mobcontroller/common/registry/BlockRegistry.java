package org.chubby.github.mobcontroller.common.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.chubby.github.mobcontroller.common.blocks.*;
import org.chubby.github.mobcontroller.platform.services.Services;
import org.chubby.github.mobcontroller.util.Utils;

import java.util.function.Supplier;

public class BlockRegistry
{

    public static final Supplier<Block> NEURAL_INTERFACE_STATION = registerBlock("neural_interface_station",()->new NeuralInterfaceStation(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));
    public static final Supplier<Block> ELECTROLYTIC_DIFFUSER = registerBlock("electrolytic_diffuser",()->new ElectrolyticDiffuser(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));
    public static final Supplier<Block> SCEPTIC_TANK = registerBlock("sceptic_tank",()->new ScepticTank(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));
    public static final Supplier<Block> QUARTZ_GLASS = registerBlock("quartz_glass",()->new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).strength(0.9f)));

    public static Supplier<Block> registerBlock (String name, Supplier<Block> sup)
    {
        Supplier<Block> toReg = Services.REGISTRY_HELPER().registerBlock(Utils.resource(name),sup);
        Services.REGISTRY_HELPER().registerItem(Utils.resource(name),()-> new BlockItem(toReg.get(),new Item.Properties()));
        return toReg;
    }
    public static void init(){}
}

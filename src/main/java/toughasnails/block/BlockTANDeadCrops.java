/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package toughasnails.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import toughasnails.api.config.SeasonsOption;
import toughasnails.api.config.SyncedConfig;
import toughasnails.api.season.Season;
import toughasnails.api.season.SeasonHelper;
import toughasnails.api.temperature.Temperature;
import toughasnails.api.temperature.TemperatureHelper;

public class BlockTANDeadCrops extends BlockBush
{
    private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D);
    
    public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 7);
    
    public BlockTANDeadCrops()
    {
        this.setHardness(0.0F);
        this.setSoundType(SoundType.PLANT);
        this.setTickRandomly(true);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, 0));
        this.disableStats();
    }
    
    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        super.updateTick(worldIn, pos, state, rand);
        
//        if( worldIn.rand.nextInt(20) != 0 )
//            return;
        
        if( SyncedConfig.getBooleanValue(SeasonsOption.ENABLE_SEASONS) ) {
            Season season = SeasonHelper.getSeasonData(worldIn).getSubSeason().getSeason();
            if( season == Season.WINTER )
                return;
        }
        
//        if( !TemperatureHelper.isPosClimatisedForTemp(worldIn, pos, new Temperature(1)) )
//            return;
        
        BlockPos down = pos.down();
        IBlockState soil = worldIn.getBlockState(down);
        Block soilBlock = soil.getBlock();
        if( soilBlock instanceof BlockFarmland ) {
//            BlockFarmland farmLand = (BlockFarmland)soilBlock;
            int moisture = soil.getValue(BlockFarmland.MOISTURE);
            if( moisture > 0 ) {
                int type = state.getValue(TYPE);
                IBlockState newState = SeasonHelper.getCropFromType(type);
                if( newState != null )
                    worldIn.setBlockState(pos, newState, 2);
            }
        }
    }
    
    @Override
    protected boolean canSustainBush(IBlockState state)
    {
        return state.getBlock() == Blocks.FARMLAND;
    }
    
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return BOUNDING_BOX;
    }
    
    @Override
    public int quantityDropped(Random random)
    {
        return 1;
    }
    
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        int type = state.getValue(TYPE);
        IBlockState cropState = SeasonHelper.getCropFromType(type);
        if( cropState == null )
            return Items.AIR;
        return cropState.getBlock().getItemDropped(cropState, rand, 0);
    }
    
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(TYPE, meta);
    }
    
    @Override
    public int getMetaFromState(IBlockState state)
    {
        int meta = state.getValue(TYPE);
        return meta;
    }
    
    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {TYPE});
    }
}

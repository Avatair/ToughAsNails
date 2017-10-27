/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package toughasnails.api.season;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBeetroot;
import net.minecraft.block.BlockCarrot;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockPotato;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import toughasnails.api.config.SeasonsOption;
import toughasnails.api.config.SyncedConfig;
import toughasnails.api.config.GameplayOption;

public class SeasonHelper 
{
    public static ISeasonDataProvider dataProvider;

    /** 
     * Obtains data about the state of the season cycle in the world. This works both on
     * the client and the server.
     */
    public static ISeasonData getSeasonData(World world)
    {
        ISeasonData data;

        if (!world.isRemote)
        {
            data = dataProvider.getServerSeasonData(world);
        }
        else
        {
            data = dataProvider.getClientSeasonData();
        }

        return data;
    }
    
    /**
     * Checks if the season provided allows snow to fall at a certain
     * biome temperature.
     * 
     * @param season The season to check
     * @param temperature The biome temperature to check
     * @return True if suitable, otherwise false
     */
    public static boolean canSnowAtTempInSeason(Season season, float temperature)
    {
        //If we're in winter, the temperature can be anything equal to or below 0.7
        return temperature < 0.15F || (season == Season.WINTER && temperature <= 0.7F && SyncedConfig.getBooleanValue(SeasonsOption.ENABLE_SEASONS));
    }

    private static float modifyTemperature(float temperature, Biome biome, Season season ) {
		if( biome == Biomes.PLAINS && season == Season.WINTER ) {
			temperature -= 0.1F;
		}
		
		return temperature;
    }
    
    public static float getModifiedTemperatureForBiome(Biome biome, Season season) {
		float temperature = biome.getTemperature();
		return modifyTemperature(temperature, biome, season);
    }
    
    public static float getModifiedFloatTemperatureAtPos(Biome biome, BlockPos pos, Season season) {
		float temperature = biome.getFloatTemperature(pos);
		return modifyTemperature(temperature, biome, season);
    }

	public static float getSeasonFloatTemperature(Biome biome, BlockPos pos, Season season) {
        if ( getModifiedTemperatureForBiome(biome, season) <= 0.7F && season == Season.WINTER && SyncedConfig.getBooleanValue(SeasonsOption.ENABLE_SEASONS))
        {
            return 0.0F;
        }
        else
        {
            return biome.getFloatTemperature(pos);
        }
	}
	
    public interface ISeasonDataProvider
    {
        ISeasonData getServerSeasonData(World world);
        ISeasonData getClientSeasonData();
    }

    public static IBlockState getCropFromType(int type)
    {
        switch(type) {
        case 1:
            return Blocks.WHEAT.getDefaultState();
        case 2:
            return Blocks.POTATOES.getDefaultState();
        case 3:
            return Blocks.CARROTS.getDefaultState();
        case 4:
            return Blocks.PUMPKIN_STEM.getDefaultState();
        case 5:
            return Blocks.MELON_STEM.getDefaultState();
        case 6:
            return Blocks.BEETROOTS.getDefaultState();
        }
        return null;
    }

    public static int getTypeFromCrop(Block block)
    {
        if( block == Blocks.BEETROOTS )
            return 6;
        if( block == Blocks.MELON_STEM )
            return 5;
        if( block == Blocks.PUMPKIN_STEM )
            return 4;
        if( block == Blocks.CARROTS  )
            return 3;
        if( block == Blocks.POTATOES )
            return 2;
        if( block == Blocks.WHEAT )
            return 1;
        return 0;
    }
}

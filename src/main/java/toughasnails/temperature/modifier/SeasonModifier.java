/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package toughasnails.temperature.modifier;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import toughasnails.api.config.SeasonsOption;
import toughasnails.api.config.SyncedConfig;
import toughasnails.api.season.Season.SubSeason;
import toughasnails.api.season.SeasonHelper;
import toughasnails.api.temperature.Temperature;
import toughasnails.api.config.GameplayOption;
import toughasnails.init.ModConfig;
import toughasnails.temperature.TemperatureDebugger;
import toughasnails.temperature.TemperatureDebugger.Modifier;

public class SeasonModifier extends TemperatureModifier
{
    public SeasonModifier(TemperatureDebugger debugger) 
    {
        super(debugger);
    }

    @Override
    public Temperature modifyTarget(World world, EntityPlayer player, Temperature temperature) 
    {
        int temperatureLevel = temperature.getRawValue();
        SubSeason season = SeasonHelper.getSeasonData(world).getSubSeason();
        
        boolean isIndoor = checkIndoor(world, player);
        
        if (!(SyncedConfig.getBooleanValue(SeasonsOption.ENABLE_SEASONS)))
        {
        	season = SubSeason.MID_SUMMER;
        }
        
        debugger.start(Modifier.SEASON_TARGET, temperatureLevel);
        
        if (world.provider.isSurfaceWorld())
        {
        	int delta = 0;
	        switch (season)
	        {
	        case EARLY_SPRING:
	            delta = ModConfig.temperature.earlySpringModifier;
                break;

			case MID_SPRING:
				delta = ModConfig.temperature.midSpringModifier;
				break;
                
	        case LATE_SPRING:
	        	delta = ModConfig.temperature.lateSpringModifier;
                break;
                
	        case EARLY_SUMMER:
	        	delta = ModConfig.temperature.earlySummerModifier;
	            break;
	            
	        case MID_SUMMER:
	        	delta = ModConfig.temperature.midSummerModifier;
	            break;
	            
	        case LATE_SUMMER:
	        	delta = ModConfig.temperature.lateSummerModifier;
	            break;
	            
	        case EARLY_AUTUMN:
	        	delta = ModConfig.temperature.earlyAutumnModifier;
	            break;

			case MID_AUTUMN:
				delta = ModConfig.temperature.midAutumnModifier;
				break;
	            
	        case LATE_AUTUMN:
	        	delta = ModConfig.temperature.lateAutumnModifier;
	            break;
	            
	        case EARLY_WINTER:
	        	delta = ModConfig.temperature.earlyWinterModifier;
	            break;
	            
	        case MID_WINTER:
	        	delta = ModConfig.temperature.midWinterModifier;
	            break;
	            
	        case LATE_WINTER:
	        	delta = ModConfig.temperature.lateWinterModifier;
                break;
	            
	        default:
	            break;
	        }
	        
	        delta -= ModConfig.temperature.lateSpringModifier;		// mid summer temperature is used as reference
	        if( isIndoor )
	        	delta /= 2;
	        
	        temperatureLevel += delta + ModConfig.temperature.lateSpringModifier;
        }
        debugger.end(temperatureLevel);
        
        return new Temperature(temperatureLevel);
    }

	private boolean checkIndoor(World world, EntityPlayer player) {
		// TODO more logic please!
		
		return checkUnderOverhang(world, player);
	}

	private boolean checkUnderOverhang(World world, EntityPlayer player) {
		BlockPos pos = player.getPosition();
		for( int iX = -1; iX <= 1; iX ++ ) {
			for( int iZ = -1; iZ <= 1; iZ ++ ) {
				if( world.getHeight(pos.getX() + iX, pos.getZ() + iZ) < pos.getY() + 2 )
					return false;
			}
		}
		
		return true;
	}

}

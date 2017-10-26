package toughasnails.temperature.modifier;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import toughasnails.api.season.Season;
import toughasnails.api.season.SeasonHelper;
import toughasnails.api.temperature.Temperature;
import toughasnails.init.ModConfig;
import toughasnails.temperature.TemperatureDebugger;
import toughasnails.temperature.TemperatureDebugger.Modifier;
import toughasnails.util.BiomeUtils;
import toughasnails.util.GeoUtils;

public class TimeModifier extends TemperatureModifier
{
    public TimeModifier(TemperatureDebugger debugger)
    {
        super(debugger);
    }

    @Override
    public Temperature modifyTarget(World world, EntityPlayer player, Temperature temperature)
    {
        Biome biome = world.getBiome(player.getPosition());
        long worldTime = world.getWorldTime();
        
        boolean isIndoor = GeoUtils.checkIndoor(world, player);
//        int amountUnderground = GeoUtils.getAmountUnderground(world, player);
        
        Season season = SeasonHelper.getSeasonData(world).getSubSeason().getSeason();
        float extremityModifier = BiomeUtils.getBiomeTempExtremity(biome, season);
        //Reaches the highest point during the middle of the day and at midnight. Normalized to be between -1 and 1
        float timeNorm = getNormForTime(worldTime);
        
        int temperatureLevel = temperature.getRawValue();
        int newTemperatureLevel = temperatureLevel;

        debugger.start(Modifier.TIME_TARGET, newTemperatureLevel);
        
        if (world.provider.isSurfaceWorld())
        {
        	float delta = getTemperatureDelta(timeNorm, extremityModifier);
/*        	if( amountUnderground > 0 ) {
        		float timeNormUnderground = getNormForTime(0);	// Peak time
        		float deltaIndoor = getTemperatureDelta(timeNormUnderground, extremityModifier);
        		
        		float factor = MathHelper.clamp((float)amountUnderground / 64, 0.0f, 1.0f);
        		delta = delta + (deltaIndoor - delta) * factor;
        	}
        	else */
        	if( isIndoor ) {
//        		float timeNormIndoor = getNormForTime(6000);	// Peak time
//        		float deltaIndoor = getTemperatureDelta(timeNormIndoor, extremityModifier);
        		
//        		delta = delta + (deltaIndoor - delta) * 0.5f;
        	    delta *= 0.5;
        	}
        	newTemperatureLevel += delta;
        }
        
        debugger.end(newTemperatureLevel);
        
        return new Temperature(newTemperatureLevel);
    }
    
    private float getNormForTime(long worldTime) {
    	return (-Math.abs(((worldTime + 6000) % 24000.0F) - 12000.0F) + 6000.0F) / 6000.0F;
    }
    
    private float getTemperatureDelta(float timeNorm, float extremityModifier) {
    	return ModConfig.temperature.timeModifier * timeNorm * extremityModifier * ModConfig.temperature.timeExtremityMultiplier/* (Math.max(1.0F, extremityModifier * ModConfig.temperature.timeExtremityMultiplier))*/;
    }
}

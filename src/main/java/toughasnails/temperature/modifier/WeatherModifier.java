package toughasnails.temperature.modifier;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import toughasnails.api.season.Season;
import toughasnails.api.season.SeasonHelper;
import toughasnails.api.temperature.Temperature;
import toughasnails.init.ModConfig;
import toughasnails.temperature.TemperatureDebugger;
import toughasnails.temperature.TemperatureDebugger.Modifier;

public class WeatherModifier extends TemperatureModifier
{
    public WeatherModifier(TemperatureDebugger debugger)
    {
        super(debugger);
    }
    
    @Override
    public Temperature modifyTarget(World world, EntityPlayer player, Temperature temperature)
    {
        int temperatureLevel = temperature.getRawValue();
        int newTemperatureLevel = temperatureLevel;
        
        BlockPos playerPos = player.getPosition();
        Season season = SeasonHelper.getSeasonData(world).getSubSeason().getSeason();
        float biomeTemperature = SeasonHelper.getSeasonFloatTemperature(world.getBiome(playerPos), playerPos, season);
        boolean enabledSnow = SeasonHelper.canSnowAtTempInSeason(season, biomeTemperature);
        
        if (player.isWet())
        {
            debugger.start(Modifier.WET_TARGET, newTemperatureLevel);
            newTemperatureLevel += ModConfig.temperature.wetModifier;
            debugger.end(newTemperatureLevel);
        }
        
        if (world.isRaining() && world.canSeeSky(playerPos) && enabledSnow)
        {
            debugger.start(Modifier.SNOW_TARGET, newTemperatureLevel);
            newTemperatureLevel += ModConfig.temperature.snowModifier;
            debugger.end(newTemperatureLevel);
        }

        return new Temperature(newTemperatureLevel);
    }
}

package toughasnails.temperature.modifier;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import toughasnails.api.season.Season;
import toughasnails.api.season.SeasonHelper;
import toughasnails.api.temperature.Temperature;
import toughasnails.init.ModConfig;
import toughasnails.temperature.TemperatureDebugger;
import toughasnails.temperature.TemperatureDebugger.Modifier;
import toughasnails.util.BiomeUtils;

public class BiomeModifier extends TemperatureModifier
{
    public static final int MAX_TEMP_OFFSET = 10;
    
    public BiomeModifier(TemperatureDebugger debugger)
    {
        super(debugger);
    }

    @Override
    public Temperature modifyTarget(World world, EntityPlayer player, Temperature temperature)
    {
        Biome biome = world.getBiome(player.getPosition());
        Biome biomeNorth = world.getBiome(player.getPosition().add(0, 0, -10));
        Biome biomeSouth = world.getBiome(player.getPosition().add(0, 0, 10));
        Biome biomeEast = world.getBiome(player.getPosition().add(10, 0, 0));
        Biome biomeWest = world.getBiome(player.getPosition().add(-10, 0, 0));
        
        Season season = SeasonHelper.getSeasonData(world).getSubSeason().getSeason();
        float biomeTemp = ((BiomeUtils.getBiomeTempNorm(biome, season) + BiomeUtils.getBiomeTempNorm(biomeNorth, season) + BiomeUtils.getBiomeTempNorm(biomeSouth, season) + BiomeUtils.getBiomeTempNorm(biomeEast, season) + BiomeUtils.getBiomeTempNorm(biomeWest, season)) / 5.0F);
        
        //Denormalize, multiply by the max temp offset, add to the current temp
        int newTemperatureLevel = temperature.getRawValue() + (int)Math.round((biomeTemp * 2.0F - 1.0F) * ModConfig.temperature.maxBiomeTempOffset);
        
        debugger.start(Modifier.BIOME_TEMPERATURE_TARGET, temperature.getRawValue());
        debugger.end(newTemperatureLevel);
        
        return new Temperature(newTemperatureLevel);
    }
}

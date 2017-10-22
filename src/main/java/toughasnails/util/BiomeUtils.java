package toughasnails.util;

import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import toughasnails.api.season.Season;
import toughasnails.api.season.SeasonHelper;

public class BiomeUtils 
{
    /**Get biome temperature on a scale of 0 to 1, 0 freezing and 1 boiling hot
     * @param season **/
    public static float getBiomeTempNorm(Biome biome, Season season)
    {
    	float temperature = SeasonHelper.getModifiedTemperatureForBiome(biome, season);
        return MathHelper.clamp(temperature, 0.0F, 1.35F) / 1.35F;
    }
    
    /**Get the biome temperature's level of extremity from 0 to 1, 0 least extreme and 1 most extreme*/
    public static float getBiomeTempExtremity(Biome biome, Season season)
    {
        return Math.abs(getBiomeTempNorm(biome, season) * 2.0F - 1.0F);
    }
}

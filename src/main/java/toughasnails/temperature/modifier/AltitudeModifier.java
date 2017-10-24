package toughasnails.temperature.modifier;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import toughasnails.api.temperature.Temperature;
import toughasnails.init.ModConfig;
import toughasnails.temperature.TemperatureDebugger;
import toughasnails.temperature.TemperatureDebugger.Modifier;
import toughasnails.util.GeoUtils;

public class AltitudeModifier extends TemperatureModifier
{
    public AltitudeModifier(TemperatureDebugger debugger)
    {
        super(debugger);
    }

    @Override
    public Temperature modifyTarget(World world, EntityPlayer player, Temperature temperature)
    {
        int temperatureLevel = temperature.getRawValue();
        int newTemperatureLevel = temperatureLevel;

        debugger.start(Modifier.ALTITUDE_TARGET, newTemperatureLevel);
        
        int amountUnderground = GeoUtils.getAmountUnderground(world, player);
        
        if (world.provider.isSurfaceWorld())
        {
        	newTemperatureLevel -= MathHelper.abs(MathHelper.floor(((64 - player.posY) / 64) * ModConfig.temperature.altitudeModifier) + 1);
        	newTemperatureLevel += MathHelper.floor(((float)amountUnderground / 64) * (10 + ModConfig.temperature.altitudeModifier));	// TODO: Make a config out of it! 
        }
        
        debugger.end(newTemperatureLevel);
        
        return new Temperature(newTemperatureLevel);
    }
}

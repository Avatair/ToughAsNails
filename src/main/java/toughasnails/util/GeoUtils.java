package toughasnails.util;

import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;

public class GeoUtils {
	public static boolean checkIndoor(World world, EntityPlayer player) {
		// TODO more logic please!
	    
	    BlockPos playerPos = player.getPosition();
        MutableBlockPos pos = new MutableBlockPos();
        for( int iX = -1; iX <= 1; iX ++ ) {
            for( int iZ = -1; iZ <= 1; iZ ++ ) {
                pos.setPos(playerPos.getX() + iX,  0,  playerPos.getZ() + iZ);
                int iY = world.getHeight(pos.getX(), pos.getZ());
                while( -- iY >= playerPos.getY() + 2 ) {
                    if( iY <= 0 )
                        break;
                    pos.setY(iY);
                    IBlockState state = world.getBlockState(pos);
                    if( state.getBlock() instanceof BlockLeaves )
                        continue;
                    if( state.isOpaqueCube() )
                        break;
                }
                if( iY < playerPos.getY() + 2 )
                    return false;
            }
        }
        
        return true;
	}
	
	public static int getAmountUnderground(World world, EntityPlayer player) {
		WorldProvider worldProvider = world.provider;
		if( player.getPosition().getY() > worldProvider.getHorizon() )
			return 0;
		int amountUnderground = getAmountUnderOverhang(world, player);
		if( amountUnderground <= 5 )
			return 0;
		return amountUnderground;
	}

	public static int getAmountUnderOverhang(World world, EntityPlayer player) {
		int minUnderOverhang = Integer.MAX_VALUE;
		BlockPos pos = player.getPosition();
		for( int iX = -1; iX <= 1; iX ++ ) {
			for( int iZ = -1; iZ <= 1; iZ ++ ) {
				int amountUnderground = world.getHeight(pos.getX() + iX, pos.getZ() + iZ) - (pos.getY() + 2); 	// Player has a height of 2!
//				if( world.getHeight(pos.getX() + iX, pos.getZ() + iZ) < pos.getY() + 2 )
//					return false;
				if( amountUnderground <= 0 )
					return 0;
				if( minUnderOverhang > amountUnderground )
					minUnderOverhang = amountUnderground;				
			}
		}
		
		return minUnderOverhang;
	}
}

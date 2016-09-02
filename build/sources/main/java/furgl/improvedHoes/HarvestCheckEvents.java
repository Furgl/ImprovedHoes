package furgl.improvedHoes;

import net.minecraft.block.BlockCrops;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HarvestCheckEvents 
{
	private int radius;

	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(BlockEvent.BreakEvent event)
	{
		if (!event.getWorld().isRemote && event.getPlayer().getHeldItem(EnumHand.MAIN_HAND) != null && ImprovedHoes.isRegisteredHoe(event.getPlayer().getHeldItem(EnumHand.MAIN_HAND)) && event.getState().getBlock() instanceof BlockCrops && !event.getPlayer().isSneaking())
		{
			if (((BlockCrops) event.getState().getBlock()).canGrow(event.getWorld(), event.getPos(), event.getWorld().getBlockState(event.getPos()), false))
				event.setCanceled(true);
			radius = ImprovedHoes.calculateRadius(event.getPlayer().getHeldItem(EnumHand.MAIN_HAND));
			for (int x=-radius; x<=radius; x++)
			{
				for (int z=-radius; z<=radius; z++)
				{
					if (event.getWorld().getBlockState(event.getPos().add(x, 0, z)).getBlock() instanceof BlockCrops && !((BlockCrops) event.getWorld().getBlockState(event.getPos().add(x, 0, z)).getBlock()).canGrow(event.getWorld(), event.getPos().add(x, 0, z), event.getWorld().getBlockState(event.getPos().add(x, 0, z)), false))
					{
						BlockCrops crop = (BlockCrops) event.getWorld().getBlockState(event.getPos().add(x, 0, z)).getBlock();
						crop.harvestBlock(event.getWorld(), event.getPlayer(), event.getPos().add(x, 0, z), event.getWorld().getBlockState(event.getPos().add(x, 0, z)), (TileEntity)null, null);
						event.getWorld().setBlockToAir(event.getPos().add(x, 0, z));
						event.getWorld().scheduleBlockUpdate(event.getPos().add(x, 0, z), event.getWorld().getBlockState(event.getPos().add(x, 0, z)).getBlock(), 0, 1);//markBlockForUpdate(event.getPos().add(x, 0, z));
					}
				}
			}
		}
	}
}

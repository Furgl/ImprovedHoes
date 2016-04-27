package furgl.improvedHoes;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.BlockCrops;
import net.minecraftforge.event.world.BlockEvent;

public class HarvestCheckEvents 
{
	private int radius;

	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(BlockEvent.BreakEvent event)
	{
		if (!event.world.isRemote && !(event.getPlayer().getHeldItem() == null) && ImprovedHoes.isRegisteredHoe(event.getPlayer().getHeldItem()) && event.block instanceof BlockCrops && !event.getPlayer().isSneaking())
		{
			if (((BlockCrops) event.block).func_149851_a(event.world, event.x, event.y, event.z, false))
				event.setCanceled(true);
			radius = ImprovedHoes.calculateRadius(event.getPlayer().getHeldItem());
			for (int x=-radius; x<=radius; x++)
			{
				for (int z=-radius; z<=radius; z++)
				{
					if (event.world.getBlock(event.x+x, event.y, event.z+z) instanceof BlockCrops && !((BlockCrops) event.world.getBlock(event.x+x, event.y, event.z+z)).func_149851_a(event.world, event.x+x, event.y, event.z+z, false))
					{
						BlockCrops crop = (BlockCrops) event.world.getBlock(event.x+x, event.y, event.z+z);
						crop.harvestBlock(event.world, event.getPlayer(), event.x+x, event.y, event.z+z, event.blockMetadata);
						event.world.setBlockToAir(event.x+x, event.y, event.z+z);
						event.world.markBlockForUpdate(event.x+x, event.y, event.z+z);
					}
				}
			}
		}
	}
}

package furgl.improvedHoes;

import net.minecraft.block.BlockCrops;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HarvestCheckEvents 
{
	private int radius;

	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(BlockEvent.BreakEvent event)
	{
		if (!event.world.isRemote && event.getPlayer().getHeldItem() != null && ImprovedHoes.isRegisteredHoe(event.getPlayer().getHeldItem()) && event.state.getBlock() instanceof BlockCrops && !event.getPlayer().isSneaking())
		{
			if (((BlockCrops) event.state.getBlock()).canGrow(event.world, event.pos, event.world.getBlockState(event.pos), false))
				event.setCanceled(true);
			radius = ImprovedHoes.calculateRadius(event.getPlayer().getHeldItem());
			for (int x=-radius; x<=radius; x++)
			{
				for (int z=-radius; z<=radius; z++)
				{
					if (event.world.getBlockState(event.pos.add(x, 0, z)).getBlock() instanceof BlockCrops && !((BlockCrops) event.world.getBlockState(event.pos.add(x, 0, z)).getBlock()).canGrow(event.world, event.pos.add(x, 0, z), event.world.getBlockState(event.pos.add(x, 0, z)), false))
					{
						BlockCrops crop = (BlockCrops) event.world.getBlockState(event.pos.add(x, 0, z)).getBlock();
						crop.harvestBlock(event.world, event.getPlayer(), event.pos.add(x, 0, z), event.world.getBlockState(event.pos.add(x, 0, z)), null);
						event.world.setBlockToAir(event.pos.add(x, 0, z));
						event.world.markBlockForUpdate(event.pos.add(x, 0, z));
					}
				}
			}
		}
	}
}

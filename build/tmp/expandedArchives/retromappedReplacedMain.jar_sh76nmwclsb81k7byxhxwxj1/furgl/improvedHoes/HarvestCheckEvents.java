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
		if (!event.getWorld().field_72995_K && event.getPlayer().func_184586_b(EnumHand.MAIN_HAND) != null && ImprovedHoes.isRegisteredHoe(event.getPlayer().func_184586_b(EnumHand.MAIN_HAND)) && event.getState().func_177230_c() instanceof BlockCrops && !event.getPlayer().func_70093_af())
		{
			if (((BlockCrops) event.getState().func_177230_c()).func_176473_a(event.getWorld(), event.getPos(), event.getWorld().func_180495_p(event.getPos()), false))
				event.setCanceled(true);
			radius = ImprovedHoes.calculateRadius(event.getPlayer().func_184586_b(EnumHand.MAIN_HAND));
			for (int x=-radius; x<=radius; x++)
			{
				for (int z=-radius; z<=radius; z++)
				{
					if (event.getWorld().func_180495_p(event.getPos().func_177982_a(x, 0, z)).func_177230_c() instanceof BlockCrops && !((BlockCrops) event.getWorld().func_180495_p(event.getPos().func_177982_a(x, 0, z)).func_177230_c()).func_176473_a(event.getWorld(), event.getPos().func_177982_a(x, 0, z), event.getWorld().func_180495_p(event.getPos().func_177982_a(x, 0, z)), false))
					{
						BlockCrops crop = (BlockCrops) event.getWorld().func_180495_p(event.getPos().func_177982_a(x, 0, z)).func_177230_c();
						crop.func_180657_a(event.getWorld(), event.getPlayer(), event.getPos().func_177982_a(x, 0, z), event.getWorld().func_180495_p(event.getPos().func_177982_a(x, 0, z)), (TileEntity)null, null);
						event.getWorld().func_175698_g(event.getPos().func_177982_a(x, 0, z));
						event.getWorld().func_180497_b(event.getPos().func_177982_a(x, 0, z), event.getWorld().func_180495_p(event.getPos().func_177982_a(x, 0, z)).func_177230_c(), 0, 1);//markBlockForUpdate(event.getPos().add(x, 0, z));
					}
				}
			}
		}
	}
}

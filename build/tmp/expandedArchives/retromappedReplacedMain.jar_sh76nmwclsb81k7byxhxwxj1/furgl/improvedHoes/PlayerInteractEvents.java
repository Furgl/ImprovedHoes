package furgl.improvedHoes;

import java.lang.reflect.Method;

import net.minecraft.block.BlockCrops;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerInteractEvents 
{
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(PlayerInteractEvent event)
	{
		if (!event.getWorld().field_72995_K && (event.getEntityPlayer().func_184586_b(EnumHand.MAIN_HAND) == null || !ImprovedHoes.isRegisteredHoe(event.getEntityPlayer().func_184586_b(EnumHand.MAIN_HAND))) && !event.getEntityPlayer().func_70093_af())
		{
			//Copied from UseHoeEvents.rightClickCrops()
			if (event.getWorld().func_180495_p(event.getPos()).func_177230_c() instanceof BlockCrops)
			{
				if (event.getWorld().func_180495_p(event.getPos()).func_177230_c() instanceof BlockCrops && !((BlockCrops) event.getWorld().func_180495_p(event.getPos()).func_177230_c()).func_176473_a(event.getWorld(), event.getPos(), event.getWorld().func_180495_p(event.getPos()), false))
				{
					BlockCrops crop = (BlockCrops) event.getWorld().func_180495_p(event.getPos()).func_177230_c();
					crop.func_180657_a(event.getWorld(), event.getEntityPlayer(), event.getPos(), event.getWorld().func_180495_p(event.getPos()), null, null);
					try
					{
						Method method = crop.getClass().getDeclaredMethod("getSeed");
						method.setAccessible(true);
						Item seed = (Item) method.invoke(crop);
						if (event.getEntityPlayer().field_71075_bZ.field_75098_d || event.getEntityPlayer().field_71071_by.func_174925_a(seed, -1, 1, null) == 1/*event.getEntityPlayer().inventory.consumeInventoryItem(seed)*/)
						{
							event.getEntityPlayer().field_71069_bz.func_75142_b();
							event.getWorld().func_180501_a(event.getPos(), crop.func_176203_a(0), 0);
						}
						else
							event.getWorld().func_175698_g(event.getPos());
						event.getWorld().func_180497_b(event.getPos(), event.getWorld().func_180495_p(event.getPos()).func_177230_c(), 0, 1);//event.getWorld().markBlockForUpdate(event.getPos());
					}
					catch(Exception e)
					{
						try 
						{
							Method method = crop.getClass().getDeclaredMethod("func_149866_i");
							method.setAccessible(true);
							Item seed = (Item) method.invoke(crop);
							if (event.getEntityPlayer().field_71075_bZ.field_75098_d || event.getEntityPlayer().field_71071_by.func_174925_a(seed, -1, 1, null) == 1/*event.getEntityPlayer().inventory.consumeInventoryItem(seed)*/)
							{
								event.getEntityPlayer().field_71069_bz.func_75142_b();
								event.getWorld().func_180501_a(event.getPos(), crop.func_176203_a(0), 0);
							}
							else
								event.getWorld().func_175698_g(event.getPos());
							event.getWorld().func_180497_b(event.getPos(), event.getWorld().func_180495_p(event.getPos()).func_177230_c(), 0, 1);//event.getWorld().markBlockForUpdate(event.getPos());
						}
						catch(Exception ex)
						{
							ex.printStackTrace();
							System.out.println("[Improved Hoes] Error during onPlayerInteractEvent(). Please report this to the mod maker.");
							Method[] methods = crop.getClass().getDeclaredMethods();
							for (int i=0; i<methods.length; i++)
								System.out.println("Method "+i+": "+methods[i]);
						}
					}
				}
			}
		}
	}
}

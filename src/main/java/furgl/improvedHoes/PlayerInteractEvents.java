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
		if (!event.getWorld().isRemote && (event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND) == null || !ImprovedHoes.isRegisteredHoe(event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND))) && !event.getEntityPlayer().isSneaking())
		{
			//Copied from UseHoeEvents.rightClickCrops()
			if (event.getWorld().getBlockState(event.getPos()).getBlock() instanceof BlockCrops)
			{
				if (event.getWorld().getBlockState(event.getPos()).getBlock() instanceof BlockCrops && !((BlockCrops) event.getWorld().getBlockState(event.getPos()).getBlock()).canGrow(event.getWorld(), event.getPos(), event.getWorld().getBlockState(event.getPos()), false))
				{
					BlockCrops crop = (BlockCrops) event.getWorld().getBlockState(event.getPos()).getBlock();
					crop.harvestBlock(event.getWorld(), event.getEntityPlayer(), event.getPos(), event.getWorld().getBlockState(event.getPos()), null, null);
					try
					{
						Method method = crop.getClass().getDeclaredMethod("getSeed");
						method.setAccessible(true);
						Item seed = (Item) method.invoke(crop);
						if (event.getEntityPlayer().capabilities.isCreativeMode || event.getEntityPlayer().inventory.clearMatchingItems(seed, -1, 1, null) == 1/*event.getEntityPlayer().inventory.consumeInventoryItem(seed)*/)
						{
							event.getEntityPlayer().inventoryContainer.detectAndSendChanges();
							event.getWorld().setBlockState(event.getPos(), crop.getStateFromMeta(0), 0);
						}
						else
							event.getWorld().setBlockToAir(event.getPos());
						event.getWorld().scheduleBlockUpdate(event.getPos(), event.getWorld().getBlockState(event.getPos()).getBlock(), 0, 1);//event.getWorld().markBlockForUpdate(event.getPos());
					}
					catch(Exception e)
					{
						try 
						{
							Method method = crop.getClass().getDeclaredMethod("func_149866_i");
							method.setAccessible(true);
							Item seed = (Item) method.invoke(crop);
							if (event.getEntityPlayer().capabilities.isCreativeMode || event.getEntityPlayer().inventory.clearMatchingItems(seed, -1, 1, null) == 1/*event.getEntityPlayer().inventory.consumeInventoryItem(seed)*/)
							{
								event.getEntityPlayer().inventoryContainer.detectAndSendChanges();
								event.getWorld().setBlockState(event.getPos(), crop.getStateFromMeta(0), 0);
							}
							else
								event.getWorld().setBlockToAir(event.getPos());
							event.getWorld().scheduleBlockUpdate(event.getPos(), event.getWorld().getBlockState(event.getPos()).getBlock(), 0, 1);//event.getWorld().markBlockForUpdate(event.getPos());
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

package furgl.improvedHoes;

import java.lang.reflect.Method;

import net.minecraft.block.BlockCrops;
import net.minecraft.item.Item;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerInteractEvents 
{
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(PlayerInteractEvent event)
	{
		if (!event.world.isRemote && (event.entityPlayer.getHeldItem() == null || !ImprovedHoes.isRegisteredHoe(event.entityPlayer.getHeldItem())) && !event.entityPlayer.isSneaking())
		{
			//Copied from UseHoeEvents.rightClickCrops()
			if (event.world.getBlockState(event.pos).getBlock() instanceof BlockCrops)
			{
				if (event.world.getBlockState(event.pos).getBlock() instanceof BlockCrops && !((BlockCrops) event.world.getBlockState(event.pos).getBlock()).canGrow(event.world, event.pos, event.world.getBlockState(event.pos), false))
				{
					BlockCrops crop = (BlockCrops) event.world.getBlockState(event.pos).getBlock();
					crop.harvestBlock(event.world, event.entityPlayer, event.pos, event.world.getBlockState(event.pos), null);
					try
					{
						Method method = crop.getClass().getDeclaredMethod("getSeed");
						method.setAccessible(true);
						Item seed = (Item) method.invoke(crop);
						if (event.entityPlayer.capabilities.isCreativeMode || event.entityPlayer.inventory.consumeInventoryItem(seed))
						{
							event.entityPlayer.inventoryContainer.detectAndSendChanges();
							event.world.setBlockState(event.pos, crop.getStateFromMeta(0), 0);
						}
						else
							event.world.setBlockToAir(event.pos);
						event.world.markBlockForUpdate(event.pos);
					}
					catch(Exception e)
					{
						try 
						{
							Method method = crop.getClass().getDeclaredMethod("func_149866_i");
							method.setAccessible(true);
							Item seed = (Item) method.invoke(crop);
							if (event.entityPlayer.capabilities.isCreativeMode || event.entityPlayer.inventory.consumeInventoryItem(seed))
							{
								event.entityPlayer.inventoryContainer.detectAndSendChanges();
								event.world.setBlockState(event.pos, crop.getStateFromMeta(0), 0);
							}
							else
								event.world.setBlockToAir(event.pos);
							event.world.markBlockForUpdate(event.pos);
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

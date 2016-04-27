package furgl.improvedHoes;

import java.lang.reflect.Method;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.BlockCrops;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class PlayerInteractEvents 
{
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(PlayerInteractEvent event)
	{
		if (!event.world.isRemote && (event.entityPlayer.getHeldItem() == null || !(event.entityPlayer.getHeldItem().getItem() instanceof ItemHoe)) && !event.entityPlayer.isSneaking())
		{
			//Copied from UseHoeEvents.rightClickCrops()
			if (event.world.getBlock(event.x, event.y, event.z) instanceof BlockCrops)
			{
				if (event.world.getBlock(event.x, event.y, event.z) instanceof BlockCrops && !((BlockCrops) event.world.getBlock(event.x, event.y, event.z)).func_149851_a(event.world, event.x, event.y, event.z, false))
				{
					BlockCrops crop = (BlockCrops) event.world.getBlock(event.x, event.y, event.z);
					crop.harvestBlock(event.world, event.entityPlayer, event.x, event.y, event.z, event.world.getBlockMetadata(event.x, event.y, event.z));
					try
					{
						Method method = crop.getClass().getDeclaredMethod("func_149866_i");
						method.setAccessible(true);
						Item seed = (Item) method.invoke(crop);
						if (event.entityPlayer.capabilities.isCreativeMode || event.entityPlayer.inventory.consumeInventoryItem(seed))
						{
							event.entityPlayer.inventoryContainer.detectAndSendChanges();
							event.world.setBlockMetadataWithNotify(event.x, event.y, event.z, 0, 0);
						}
						else
							event.world.setBlockToAir(event.x, event.y, event.z);
						event.world.markBlockForUpdate(event.x, event.y, event.z);
					}
					catch(Exception e)
					{
						e.printStackTrace();
						System.out.println("[Improved Hoes] Error during onPlayerInteractEvent(). Please report this to the mod maker.");
					}
				}
			}
		}
	}
}

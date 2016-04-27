package furgl.improvedHoes;

import java.lang.reflect.Method;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.event.entity.player.UseHoeEvent;

public class UseHoeEvents 
{
	private int radius;

	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(UseHoeEvent event)
	{
		if (!event.world.isRemote && ImprovedHoes.isRegisteredHoe(event.current) && !event.entityPlayer.isSneaking())
		{
			radius = ImprovedHoes.calculateRadius(event.current);
			if (event.world.getBlock(event.x, event.y, event.z) instanceof BlockCrops)
				rightClickCrop(event);
			else
				rightClickDirt(event);
			event.setCanceled(true);
		}
	}

	private void rightClickDirt(UseHoeEvent event) 
	{
		for (int x=-radius; x<=radius; x++)
		{
			for (int z=-radius; z<=radius; z++)
			{
				onItemUse(event, x, z);
				event.world.markBlockForUpdate(event.x+x, event.y, event.z+z);
			}
		}
	}

	private boolean onItemUse(UseHoeEvent event, int x, int z) 
	{
		if(event.current.getItemDamage() == 0 && !(x == -radius && z == -radius) && !event.entityPlayer.capabilities.isCreativeMode)
			return false;

		if (!event.entityPlayer.canPlayerEdit(event.x+x, event.y, event.z+z, 0, event.current))
		{
			return false;
		}
		else
		{
			/*UseHoeEvent event = new UseHoeEvent(event.entityPlayer, event.current, event.world, event.x+x, event.y, event.z+z);
	            if (MinecraftForge.EVENT_BUS.post(event))
	            {
	                return false;
	            }*/

			if (event.getResult() == Result.ALLOW)
			{
				event.current.damageItem(1, event.entityPlayer);
				return true;
			}

			Block block = event.world.getBlock(event.x+x, event.y, event.z+z);

			if (/*p_77648_7_ != 0 && */event.world.getBlock(event.x+x, event.y + 1, event.z+z).isAir(event.world, event.x+x, event.y + 1, event.z+z) && (block == Blocks.grass || block == Blocks.dirt))
			{
				Block block1 = Blocks.farmland;
				event.world.playSoundEffect((double)((float)event.x+x + 0.5F), (double)((float)event.y + 0.5F), (double)((float)event.z+z + 0.5F), block1.stepSound.getStepResourcePath(), (block1.stepSound.getVolume() + 1.0F) / 2.0F, block1.stepSound.getPitch() * 0.8F);

				if (event.world.isRemote)
				{
					return true;
				}
				else
				{
					event.world.setBlock(event.x+x, event.y, event.z+z, block1);
					event.current.damageItem(1, event.entityPlayer);
					return true;
				}
			}
			else
			{
				return false;
			}
		}
	}

	private void rightClickCrop(UseHoeEvent event) 
	{
		for (int x=-radius; x<=radius; x++)
		{
			for (int z=-radius; z<=radius; z++)
			{
				if (event.world.getBlock(event.x+x, event.y, event.z+z) instanceof BlockCrops)
				{
					if (event.world.getBlock(event.x+x, event.y, event.z+z) instanceof BlockCrops && !((BlockCrops) event.world.getBlock(event.x+x, event.y, event.z+z)).func_149851_a(event.world, event.x+x, event.y, event.z+z, false))
					{
						BlockCrops crop = (BlockCrops) event.world.getBlock(event.x+x, event.y, event.z+z);
						crop.harvestBlock(event.world, event.entityPlayer, event.x+x, event.y, event.z+z, event.world.getBlockMetadata(event.x+x, event.y, event.z+z));
						try
						{
							Method method = crop.getClass().getDeclaredMethod("func_149866_i");
							method.setAccessible(true);
							Item seed = (Item) method.invoke(crop);
							if (event.entityPlayer.capabilities.isCreativeMode || event.entityPlayer.inventory.consumeInventoryItem(seed))
							{
								event.entityPlayer.inventoryContainer.detectAndSendChanges();
								event.world.setBlockMetadataWithNotify(event.x+x, event.y, event.z+z, 0, 0);
							}
							else
								event.world.setBlockToAir(event.x+x, event.y, event.z+z);
							event.world.markBlockForUpdate(event.x+x, event.y, event.z+z);
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
}

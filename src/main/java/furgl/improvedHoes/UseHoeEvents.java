package furgl.improvedHoes;

import java.lang.reflect.Method;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class UseHoeEvents 
{
	private int radius;

	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(UseHoeEvent event)
	{
		if (!event.getWorld().isRemote && ImprovedHoes.isRegisteredHoe(event.getCurrent()) && !event.getEntityPlayer().isSneaking())
		{
			radius = ImprovedHoes.calculateRadius(event.getCurrent());
			if (event.getWorld().getBlockState(event.getPos()).getBlock() instanceof BlockCrops)
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
				if (onItemUse(event, x, z))
					event.getWorld().scheduleBlockUpdate(event.getPos().add(x, 0, z), event.getWorld().getBlockState(event.getPos().add(x, 0, z)).getBlock(), 0, 1);//event.getWorld().markBlockForUpdate(event.getPos().add(x, 0, z));
			}
		}
	}

	@SuppressWarnings("deprecation")
	private boolean onItemUse(UseHoeEvent event, int x, int z) 
	{
		if(event.getCurrent().getItemDamage() == 0 && !(x == -radius && z == -radius) && !event.getEntityPlayer().capabilities.isCreativeMode)
			return false;
		int[] TYPE_LOOKUP = new int[BlockDirt.DirtType.values().length];
		TYPE_LOOKUP[BlockDirt.DirtType.DIRT.ordinal()] = 1;
		TYPE_LOOKUP[BlockDirt.DirtType.COARSE_DIRT.ordinal()] = 2;

		//Copied from ItemHoe.onItemUse() (to avoid infinite loop with forge event)
		if (!event.getEntityPlayer().canPlayerEdit(event.getPos().add(x, 0, z).offset(EnumFacing.UP), EnumFacing.UP, event.getCurrent()))
			return false;
		else
		{
			/* int hook = net.minecraftforge.event.ForgeEventFactory.onHoeUse(event.getCurrent(), event.getEntityPlayer(), event.getWorld(), event.getPos().add(x, 0, z));
        if (hook != 0) return hook > 0;*/
			IBlockState iblockstate = event.getWorld().getBlockState(event.getPos().add(x, 0, z));
			Block block = iblockstate.getBlock();
			if (EnumFacing.UP != EnumFacing.DOWN && event.getWorld().isAirBlock(event.getPos().add(x, 0, z).up()))
			{
				if (block == Blocks.GRASS)
					return useHoe(event.getCurrent(), event.getEntityPlayer(), event.getWorld(), event.getPos().add(x, 0, z), Blocks.FARMLAND.getStateFromMeta(1));
				else if (block == Blocks.DIRT)
				{
					switch (TYPE_LOOKUP[((BlockDirt.DirtType)iblockstate.getValue(BlockDirt.VARIANT)).ordinal()])
					{
					case 1:
						return useHoe(event.getCurrent(), event.getEntityPlayer(), event.getWorld(), event.getPos().add(x, 0, z), Blocks.FARMLAND.getStateFromMeta(1));
					case 2:
						return useHoe(event.getCurrent(), event.getEntityPlayer(), event.getWorld(), event.getPos().add(x, 0, z), Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT));
					}
				}
			}
			return false;
		}
	}

	private boolean useHoe(ItemStack stack, EntityPlayer player, World worldIn, BlockPos pos, IBlockState state)
	{
		worldIn.playSound(player, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);

		if (!worldIn.isRemote)
		{
			worldIn.setBlockState(pos, state, 4);
			stack.damageItem(1, player);
		}
		return true;
	}

	private void rightClickCrop(UseHoeEvent event) 
	{
		for (int x=-radius; x<=radius; x++)
		{
			for (int z=-radius; z<=radius; z++)
			{
				if (event.getWorld().getBlockState(event.getPos().add(x, 0, z)).getBlock() instanceof BlockCrops && !((BlockCrops) event.getWorld().getBlockState(event.getPos().add(x, 0, z)).getBlock()).canGrow(event.getWorld(), event.getPos().add(x, 0, z), event.getWorld().getBlockState(event.getPos().add(x, 0, z)), false))
				{
					BlockCrops crop = (BlockCrops) event.getWorld().getBlockState(event.getPos().add(x, 0, z)).getBlock();
					crop.harvestBlock(event.getWorld(), event.getEntityPlayer(), event.getPos().add(x, 0, z), event.getWorld().getBlockState(event.getPos().add(x, 0, z)), null, null);
					try
					{
						Method method = crop.getClass().getDeclaredMethod("getSeed");
						method.setAccessible(true);
						Item seed = (Item) method.invoke(crop);
						if (event.getEntityPlayer().capabilities.isCreativeMode || event.getEntityPlayer().inventory.clearMatchingItems(seed, -1, 1, null) == 1/*event.getEntityPlayer().inventory.consumeInventoryItem(seed)*/)
						{
							event.getEntityPlayer().inventoryContainer.detectAndSendChanges();
							event.getWorld().notifyBlockUpdate(event.getPos().add(x, 0, z), event.getWorld().getBlockState(event.getPos().add(x, 0, z)), crop.getStateFromMeta(0), 0);
							event.getWorld().setBlockState(event.getPos().add(x, 0, z), crop.getStateFromMeta(0), 0);
						}
						else
							event.getWorld().setBlockToAir(event.getPos().add(x, 0, z));
					}
					catch(Exception e)
					{
						try 
						{
							Method method = crop.getClass().getDeclaredMethod("func_149866_i");
							method.setAccessible(true);
							Item seed = (Item) method.invoke(crop);
							if (event.getEntityPlayer().capabilities.isCreativeMode || event.getEntityPlayer().inventory.clearMatchingItems(seed, -1, 1, null) == 1/*consumeInventoryItem(seed)*/)
							{
								event.getEntityPlayer().inventoryContainer.detectAndSendChanges();
								event.getWorld().notifyBlockUpdate(event.getPos().add(x, 0, z), event.getWorld().getBlockState(event.getPos().add(x, 0, z)), crop.getStateFromMeta(0), 0);
								event.getWorld().setBlockState(event.getPos().add(x, 0, z), crop.getStateFromMeta(0), 0);
							}
							else
								event.getWorld().setBlockToAir(event.getPos().add(x, 0, z));
						}
						catch(Exception ex)
						{
							ex.printStackTrace();
							System.out.println("[Improved Hoes] Error during onPlayerInteractEvent(). Please report this to the mod maker.");
						}
					}
				}
			}
		}
	}
}

package furgl.improvedHoes;

import java.lang.reflect.Method;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
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
		if (!event.world.isRemote && ImprovedHoes.isRegisteredHoe(event.current) && !event.entityPlayer.isSneaking())
		{
			radius = ImprovedHoes.calculateRadius(event.current);
			if (event.world.getBlockState(event.pos).getBlock() instanceof BlockCrops)
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
				event.world.markBlockForUpdate(event.pos.add(x, 0, z));
			}
		}
	}

	private boolean onItemUse(UseHoeEvent event, int x, int z) 
	{
		if(event.current.getItemDamage() == 0 && !(x == -radius && z == -radius) && !event.entityPlayer.capabilities.isCreativeMode)
			return false;
		try
		{
			Method useHoe = ItemHoe.class.getDeclaredMethod("useHoe", ItemStack.class, EntityPlayer.class, World.class, BlockPos.class, IBlockState.class);
			useHoe.setAccessible(true);
			int[] TYPE_LOOKUP = new int[BlockDirt.DirtType.values().length];
			TYPE_LOOKUP[BlockDirt.DirtType.DIRT.ordinal()] = 1;
			TYPE_LOOKUP[BlockDirt.DirtType.COARSE_DIRT.ordinal()] = 2;

			//Copied from ItemHoe.onItemUse() (to avoid infinite loop with forge event)
			if (!event.entityPlayer.canPlayerEdit(event.pos.add(x, 0, z).offset(EnumFacing.UP), EnumFacing.UP, event.current))
			{
				return false;
			}
			else
			{
				/* int hook = net.minecraftforge.event.ForgeEventFactory.onHoeUse(event.current, event.entityPlayer, event.world, event.pos.add(x, 0, z));
            if (hook != 0) return hook > 0;*/

				IBlockState iblockstate = event.world.getBlockState(event.pos.add(x, 0, z));
				Block block = iblockstate.getBlock();

				if (EnumFacing.UP != EnumFacing.DOWN && event.world.isAirBlock(event.pos.add(x, 0, z).up()))
				{
					if (block == Blocks.grass)
					{
						return (Boolean) useHoe.invoke(event.current.getItem(), event.current, event.entityPlayer, event.world, event.pos.add(x, 0, z), Blocks.farmland.getDefaultState());
					}

					if (block == Blocks.dirt)
					{
						switch (TYPE_LOOKUP[((BlockDirt.DirtType)iblockstate.getValue(BlockDirt.VARIANT)).ordinal()])
						{
						case 1:
							return (Boolean) useHoe.invoke(event.current.getItem(), event.current, event.entityPlayer, event.world, event.pos.add(x, 0, z), Blocks.farmland.getDefaultState());
						case 2:
							return (Boolean) useHoe.invoke(event.current.getItem(), event.current, event.entityPlayer, event.world, event.pos.add(x, 0, z), Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT));
						}
					}
				}

				return false;
			}
		}
		catch(Exception e)
		{
			try
			{
				Method useHoe = ItemHoe.class.getDeclaredMethod("func_179232_a", ItemStack.class, EntityPlayer.class, World.class, BlockPos.class, IBlockState.class);
				useHoe.setAccessible(true);
				int[] TYPE_LOOKUP = new int[BlockDirt.DirtType.values().length];
				TYPE_LOOKUP[BlockDirt.DirtType.DIRT.ordinal()] = 1;
				TYPE_LOOKUP[BlockDirt.DirtType.COARSE_DIRT.ordinal()] = 2;

				//Copied from ItemHoe.onItemUse() (to avoid infinite loop with forge event)
				if (!event.entityPlayer.canPlayerEdit(event.pos.add(x, 0, z).offset(EnumFacing.UP), EnumFacing.UP, event.current))
				{
					return false;
				}
				else
				{
					/* int hook = net.minecraftforge.event.ForgeEventFactory.onHoeUse(event.current, event.entityPlayer, event.world, event.pos.add(x, 0, z));
	            if (hook != 0) return hook > 0;*/

					IBlockState iblockstate = event.world.getBlockState(event.pos.add(x, 0, z));
					Block block = iblockstate.getBlock();

					if (EnumFacing.UP != EnumFacing.DOWN && event.world.isAirBlock(event.pos.add(x, 0, z).up()))
					{
						if (block == Blocks.grass)
						{
							return (Boolean) useHoe.invoke(event.current.getItem(), event.current, event.entityPlayer, event.world, event.pos.add(x, 0, z), Blocks.farmland.getDefaultState());
						}

						if (block == Blocks.dirt)
						{
							switch (TYPE_LOOKUP[((BlockDirt.DirtType)iblockstate.getValue(BlockDirt.VARIANT)).ordinal()])
							{
							case 1:
								return (Boolean) useHoe.invoke(event.current.getItem(), event.current, event.entityPlayer, event.world, event.pos.add(x, 0, z), Blocks.farmland.getDefaultState());
							case 2:
								return (Boolean) useHoe.invoke(event.current.getItem(), event.current, event.entityPlayer, event.world, event.pos.add(x, 0, z), Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT));
							}
						}
					}

					return false;
				}
			}
			catch(Exception ex)
			{
				e.printStackTrace();
				System.out.println("[Improved Hoes] Error during rightClickDirt(). Please report this to the mod maker.");
				Method[] methods = ItemHoe.class.getDeclaredMethods();
				for (int i=0; i<methods.length; i++)
					System.out.println("Method "+i+": "+methods[i]);
			}
		}

		return false;
	}

	private void rightClickCrop(UseHoeEvent event) 
	{
		for (int x=-radius; x<=radius; x++)
		{
			for (int z=-radius; z<=radius; z++)
			{
				if (event.world.getBlockState(event.pos.add(x, 0, z)).getBlock() instanceof BlockCrops && !((BlockCrops) event.world.getBlockState(event.pos.add(x, 0, z)).getBlock()).canGrow(event.world, event.pos.add(x, 0, z), event.world.getBlockState(event.pos.add(x, 0, z)), false))
				{
					BlockCrops crop = (BlockCrops) event.world.getBlockState(event.pos.add(x, 0, z)).getBlock();
					crop.harvestBlock(event.world, event.entityPlayer, event.pos.add(x, 0, z), event.world.getBlockState(event.pos.add(x, 0, z)), null);
					try
					{
						Method method = crop.getClass().getDeclaredMethod("getSeed");
						method.setAccessible(true);
						Item seed = (Item) method.invoke(crop);
						if (event.entityPlayer.capabilities.isCreativeMode || event.entityPlayer.inventory.consumeInventoryItem(seed))
						{
							event.entityPlayer.inventoryContainer.detectAndSendChanges();
							event.world.setBlockState(event.pos.add(x, 0, z), crop.getStateFromMeta(0), 0);
						}
						else
							event.world.setBlockToAir(event.pos.add(x, 0, z));
						event.world.markBlockForUpdate(event.pos.add(x, 0, z));
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
								event.world.setBlockState(event.pos.add(x, 0, z), crop.getStateFromMeta(0), 0);
							}
							else
								event.world.setBlockToAir(event.pos.add(x, 0, z));
							event.world.markBlockForUpdate(event.pos.add(x, 0, z));
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

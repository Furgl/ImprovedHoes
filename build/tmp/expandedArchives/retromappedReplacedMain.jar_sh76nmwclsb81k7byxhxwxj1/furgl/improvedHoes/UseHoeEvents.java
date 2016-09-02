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
		if (!event.getWorld().field_72995_K && ImprovedHoes.isRegisteredHoe(event.getCurrent()) && !event.getEntityPlayer().func_70093_af())
		{
			radius = ImprovedHoes.calculateRadius(event.getCurrent());
			if (event.getWorld().func_180495_p(event.getPos()).func_177230_c() instanceof BlockCrops)
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
					event.getWorld().func_180497_b(event.getPos().func_177982_a(x, 0, z), event.getWorld().func_180495_p(event.getPos().func_177982_a(x, 0, z)).func_177230_c(), 0, 1);//event.getWorld().markBlockForUpdate(event.getPos().add(x, 0, z));
			}
		}
	}

	private boolean onItemUse(UseHoeEvent event, int x, int z) 
	{
		if(event.getCurrent().func_77952_i() == 0 && !(x == -radius && z == -radius) && !event.getEntityPlayer().field_71075_bZ.field_75098_d)
			return false;
		int[] TYPE_LOOKUP = new int[BlockDirt.DirtType.values().length];
		TYPE_LOOKUP[BlockDirt.DirtType.DIRT.ordinal()] = 1;
		TYPE_LOOKUP[BlockDirt.DirtType.COARSE_DIRT.ordinal()] = 2;

		//Copied from ItemHoe.onItemUse() (to avoid infinite loop with forge event)
		if (!event.getEntityPlayer().func_175151_a(event.getPos().func_177982_a(x, 0, z).func_177972_a(EnumFacing.UP), EnumFacing.UP, event.getCurrent()))
			return false;
		else
		{
			/* int hook = net.minecraftforge.event.ForgeEventFactory.onHoeUse(event.getCurrent(), event.getEntityPlayer(), event.getWorld(), event.getPos().add(x, 0, z));
        if (hook != 0) return hook > 0;*/
			IBlockState iblockstate = event.getWorld().func_180495_p(event.getPos().func_177982_a(x, 0, z));
			Block block = iblockstate.func_177230_c();
			if (EnumFacing.UP != EnumFacing.DOWN && event.getWorld().func_175623_d(event.getPos().func_177982_a(x, 0, z).func_177984_a()))
			{
				if (block == Blocks.field_150349_c)
					return useHoe(event.getCurrent(), event.getEntityPlayer(), event.getWorld(), event.getPos().func_177982_a(x, 0, z), Blocks.field_150458_ak.func_176203_a(1));
				else if (block == Blocks.field_150346_d)
				{
					switch (TYPE_LOOKUP[((BlockDirt.DirtType)iblockstate.func_177229_b(BlockDirt.field_176386_a)).ordinal()])
					{
					case 1:
						return useHoe(event.getCurrent(), event.getEntityPlayer(), event.getWorld(), event.getPos().func_177982_a(x, 0, z), Blocks.field_150458_ak.func_176203_a(1));
					case 2:
						return useHoe(event.getCurrent(), event.getEntityPlayer(), event.getWorld(), event.getPos().func_177982_a(x, 0, z), Blocks.field_150346_d.func_176223_P().func_177226_a(BlockDirt.field_176386_a, BlockDirt.DirtType.DIRT));
					}
				}
			}
			return false;
		}
	}

	private boolean useHoe(ItemStack stack, EntityPlayer player, World worldIn, BlockPos pos, IBlockState state)
	{
		worldIn.func_184133_a(player, pos, SoundEvents.field_187693_cj, SoundCategory.BLOCKS, 1.0F, 1.0F);

		if (!worldIn.field_72995_K)
		{
			worldIn.func_180501_a(pos, state, 4);
			stack.func_77972_a(1, player);
		}
		return true;
	}

	private void rightClickCrop(UseHoeEvent event) 
	{
		for (int x=-radius; x<=radius; x++)
		{
			for (int z=-radius; z<=radius; z++)
			{
				if (event.getWorld().func_180495_p(event.getPos().func_177982_a(x, 0, z)).func_177230_c() instanceof BlockCrops && !((BlockCrops) event.getWorld().func_180495_p(event.getPos().func_177982_a(x, 0, z)).func_177230_c()).func_176473_a(event.getWorld(), event.getPos().func_177982_a(x, 0, z), event.getWorld().func_180495_p(event.getPos().func_177982_a(x, 0, z)), false))
				{
					BlockCrops crop = (BlockCrops) event.getWorld().func_180495_p(event.getPos().func_177982_a(x, 0, z)).func_177230_c();
					crop.func_180657_a(event.getWorld(), event.getEntityPlayer(), event.getPos().func_177982_a(x, 0, z), event.getWorld().func_180495_p(event.getPos().func_177982_a(x, 0, z)), null, null);
					try
					{
						Method method = crop.getClass().getDeclaredMethod("getSeed");
						method.setAccessible(true);
						Item seed = (Item) method.invoke(crop);
						if (event.getEntityPlayer().field_71075_bZ.field_75098_d || event.getEntityPlayer().field_71071_by.func_174925_a(seed, -1, 1, null) == 1/*event.getEntityPlayer().inventory.consumeInventoryItem(seed)*/)
						{
							event.getEntityPlayer().field_71069_bz.func_75142_b();
							event.getWorld().func_184138_a(event.getPos().func_177982_a(x, 0, z), event.getWorld().func_180495_p(event.getPos().func_177982_a(x, 0, z)), crop.func_176203_a(0), 0);
							event.getWorld().func_180501_a(event.getPos().func_177982_a(x, 0, z), crop.func_176203_a(0), 0);
						}
						else
							event.getWorld().func_175698_g(event.getPos().func_177982_a(x, 0, z));
					}
					catch(Exception e)
					{
						try 
						{
							Method method = crop.getClass().getDeclaredMethod("func_149866_i");
							method.setAccessible(true);
							Item seed = (Item) method.invoke(crop);
							if (event.getEntityPlayer().field_71075_bZ.field_75098_d || event.getEntityPlayer().field_71071_by.func_174925_a(seed, -1, 1, null) == 1/*consumeInventoryItem(seed)*/)
							{
								event.getEntityPlayer().field_71069_bz.func_75142_b();
								event.getWorld().func_184138_a(event.getPos().func_177982_a(x, 0, z), event.getWorld().func_180495_p(event.getPos().func_177982_a(x, 0, z)), crop.func_176203_a(0), 0);
								event.getWorld().func_180501_a(event.getPos().func_177982_a(x, 0, z), crop.func_176203_a(0), 0);
							}
							else
								event.getWorld().func_175698_g(event.getPos().func_177982_a(x, 0, z));
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

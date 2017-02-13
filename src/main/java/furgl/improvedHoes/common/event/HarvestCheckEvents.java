package furgl.improvedHoes.common.event;

import furgl.improvedHoes.common.ImprovedHoes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HarvestCheckEvents 
{
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(BlockEvent.BreakEvent event)
	{
		if (event.getPlayer().getHeldItem(EnumHand.MAIN_HAND) != null && ImprovedHoes.isRegisteredHoe(event.getPlayer().getHeldItem(EnumHand.MAIN_HAND)) && !event.getPlayer().isSneaking())
		{
			ItemStack hoe = event.getPlayer().getHeldItem(EnumHand.MAIN_HAND);
			Block block = event.getState().getBlock();
			int radius = ImprovedHoes.calculateRadius(event.getPlayer().getHeldItem(EnumHand.MAIN_HAND));

			if (!event.getWorld().isRemote && block instanceof BlockCrops) {
				if (((BlockCrops) block).canGrow(event.getWorld(), event.getPos(), event.getWorld().getBlockState(event.getPos()), false))
					event.setCanceled(true);
				for (int x=-radius; x<=radius; x++)
				{
					for (int z=-radius; z<=radius; z++)
					{
						if (event.getWorld().getBlockState(event.getPos().add(x, 0, z)).getBlock() instanceof BlockCrops && !((BlockCrops) event.getWorld().getBlockState(event.getPos().add(x, 0, z)).getBlock()).canGrow(event.getWorld(), event.getPos().add(x, 0, z), event.getWorld().getBlockState(event.getPos().add(x, 0, z)), false))
						{
							BlockCrops crop = (BlockCrops) event.getWorld().getBlockState(event.getPos().add(x, 0, z)).getBlock();
							crop.harvestBlock(event.getWorld(), event.getPlayer(), event.getPos().add(x, 0, z), event.getWorld().getBlockState(event.getPos().add(x, 0, z)), (TileEntity)null, hoe);
							event.getWorld().setBlockToAir(event.getPos().add(x, 0, z));
							event.getWorld().scheduleBlockUpdate(event.getPos().add(x, 0, z), event.getWorld().getBlockState(event.getPos().add(x, 0, z)).getBlock(), 0, 1);
						}
					}
				}
			}
			else if ((block instanceof BlockBush || block instanceof BlockLeaves || block instanceof BlockVine) && !(block instanceof BlockSapling)) {
				if (!event.getWorld().isRemote)
					for (int x=-radius; x<radius; ++x)
						for (int y=-radius; y<radius; ++y)
							for (int z=-radius; z<radius; ++z) {
								IBlockState state = event.getWorld().getBlockState(event.getPos().add(x, y, z));
								if ((state.getBlock() instanceof BlockBush || state.getBlock() instanceof BlockLeaves || state.getBlock() instanceof BlockVine) && 
										!(state.getBlock() instanceof BlockSapling)) {
									if (event.getWorld().rand.nextInt(5) == 0)
										hoe.damageItem(1, event.getPlayer());
									state.getBlock().harvestBlock(event.getWorld(), event.getPlayer(), event.getPos().add(x, y, z), state, null, hoe);
									state.getBlock().removedByPlayer(state, event.getWorld(), event.getPos().add(x, y, z), event.getPlayer(), true);
								}
							}
				hoe.damageItem(1, event.getPlayer());
			}
		}
	}

	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(PlayerEvent.BreakSpeed event) {
		if (event.getEntityPlayer() != null && event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND) != null && 
				ImprovedHoes.isRegisteredHoe(event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND)) &&
				((event.getState().getBlock() instanceof BlockBush || event.getState().getBlock() instanceof BlockLeaves || event.getState().getBlock() instanceof BlockVine) && 
						!(event.getState().getBlock() instanceof BlockSapling)))
			event.setNewSpeed(Math.max(6f, event.getOriginalSpeed()));
	}
}

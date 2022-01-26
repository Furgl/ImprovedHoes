package furgl.improvedHoes;

import furgl.improvedHoes.config.Config;
import furgl.improvedHoes.utils.Utils;
import furgl.improvedHoes.utils.Utils.Range;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.CropBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

public class EventHandler {

	public static void init() {

		// left-click block
		AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
			try {
				if (player != null && hand != null) {
					ItemStack stack = player.getStackInHand(hand);
					Range range = Utils.getRange(player, stack);
					// break crops in range
					if (Config.leftClickWithHoeToBreak && range != null && Utils.harvestCropsInRange(range, stack, pos, world, player, hand, false))
						return ActionResult.SUCCESS;
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return ActionResult.PASS;
		});

		// right-click block
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			try {
				if (world instanceof ServerWorld serverWorld && player != null && hand != null && hitResult.getBlockPos() != null) {
					ItemStack stack = player.getStackInHand(hand);
					Range range = Utils.getRange(player, stack);
					BlockPos pos = hitResult.getBlockPos();
					Block block = serverWorld.getBlockState(pos).getBlock();

					// harvest crops in range
					if (Config.rightClickWithHoeToHarvest && range != null && block instanceof CropBlock &&
							Utils.harvestCropsInRange(range, stack, pos, serverWorld, player, hand, true))
						return ActionResult.SUCCESS;
					// till blocks in range
					else if (Config.rightClickWithHoeToTill && range != null && Utils.TILLED_BLOCKS.containsKey(block) &&
							Utils.tillBlocksInRange(range, stack, pos, serverWorld, player, hand))
						return ActionResult.SUCCESS;
					// harvest crop
					else if (Config.rightClickToHarvest && range == null && block instanceof CropBlock &&
							(Config.workWhileSneaking || !player.isSneaking()) &&
							Utils.harvestBlock(pos, serverWorld, player, true, stack, hand, false))
						return ActionResult.SUCCESS;
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return ActionResult.PASS;
		});

	}

}
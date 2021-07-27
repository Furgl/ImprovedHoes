package furgl.improvedHoes.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;

import furgl.improvedHoes.config.Config;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Utils {

	/**Copied from HoeItem.class - map of block to what happens when they're tilled*/
	public static Map<Block, Pair<Predicate<ItemUsageContext>, Consumer<ItemUsageContext>>> TILLED_BLOCKS;
	public static Method getSeedsItem;

	static {
		Field tilledBlocksField = null;
		try {
			tilledBlocksField = HoeItem.class.getDeclaredField("TILLED_BLOCKS");
			getSeedsItem = CropBlock.class.getDeclaredMethod("getSeedsItem");
		} catch (Exception e) {
			try {
				tilledBlocksField = HoeItem.class.getDeclaredField("field_8023");
				getSeedsItem = CropBlock.class.getDeclaredMethod("method_9832");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		tilledBlocksField.setAccessible(true);
		getSeedsItem.setAccessible(true);
		try {
			TILLED_BLOCKS = (Map<Block, Pair<Predicate<ItemUsageContext>, Consumer<ItemUsageContext>>>) tilledBlocksField.get(null);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static enum Range {
		_1x1(0, Items.WOODEN_HOE),
		_3x3(1, Items.STONE_HOE),
		_5x5(2, Items.IRON_HOE, Items.GOLDEN_HOE),
		_7x7(3, Items.DIAMOND_HOE),
		_9x9(4, Items.NETHERITE_HOE);

		public final int radius;
		public final ArrayList<Item> defaultItems;
		public ArrayList<Item> items = Lists.newArrayList();

		private Range(int radius, Item... defaultItems) {
			this.radius = radius;
			this.defaultItems = Lists.newArrayList(defaultItems);
		}

		/**Get the range (or null) of this item*/
		@Nullable
		public static Range getRange(Item item) {
			Range[] ranges = Range.values();
			for (int i=ranges.length-1; i>=0; --i) {
				Range range = ranges[i];
				if (range.items.contains(item))
					return range;
			}
			return null;
		}

	}

	/**Get Range for this item (and make sure this is server-side and player is not in spectator mode)*/
	@Nullable
	public static Range getRange(PlayerEntity player, ItemStack stack) {
		if (player != null && stack != null && !stack.isEmpty() &&
				!player.world.isClient && !player.isSpectator() && (Config.workWhileSneaking || !player.isSneaking()))
			return Range.getRange(stack.getItem());
		else
			return null;
	}

	/**Attempt to damage this itemstack
	 * @return if item was damaged*/
	public static void attemptDamage(int amount, ItemStack stack, PlayerEntity player, Hand hand) {
		if (player instanceof ServerPlayerEntity && !player.isCreative() && !player.isSpectator())
			stack.damage(amount, player, p -> p.sendToolBreakStatus(hand));
	}

	/**Break crops (only fully grown) in range, if clicking on crop
	 * @return if break event should be cancelled*/
	public static boolean harvestCropsInRange(Range range, ItemStack stack, BlockPos originalPos, World world, PlayerEntity player, Hand hand, boolean replant) throws Exception {
		BlockState originalState = world.getBlockState(originalPos);
		Block originalBlock = originalState.getBlock();

		if (!world.isClient && originalBlock instanceof CropBlock) {
			for (int x=-range.radius; x<=range.radius; x++)
				for (int z=-range.radius; z<=range.radius; z++) {
					BlockPos pos = originalPos.add(x, 0, z);
					harvestBlock(pos, world, player, replant, stack, hand, true);
				}
			return !((CropBlock) originalBlock).isMature(originalState);
		}
		return false;
	}

	/**Till blocks in range, if clicking on tillable block
	 * @return if break event should be cancelled*/
	public static boolean tillBlocksInRange(Range range, ItemStack stack, BlockPos originalPos, World world, PlayerEntity player, Hand hand) {
		BlockState originalState = world.getBlockState(originalPos);
		Block originalBlock = originalState.getBlock();

		if (!world.isClient && TILLED_BLOCKS.containsKey(originalBlock)) {
			for (int x=-range.radius; x<=range.radius; x++)
				for (int z=-range.radius; z<=range.radius; z++) {
					BlockPos pos = originalPos.add(x, 0, z);
					BlockState state = world.getBlockState(pos);
					Block block = state.getBlock();

					Pair<Predicate<ItemUsageContext>, Consumer<ItemUsageContext>> pair = (Pair)TILLED_BLOCKS.get(block);
					if (pair != null) {
						Predicate<ItemUsageContext> predicate = (Predicate)pair.getFirst();
						Consumer<ItemUsageContext> consumer = (Consumer)pair.getSecond();
						ItemUsageContext context = new ItemUsageContext(player, hand, new BlockHitResult(new Vec3d(pos.getX()+0.5d, pos.getY(), pos.getZ()+0.5d), Direction.UP, pos, false));
						if (predicate.test(context)) {
							world.playSound(null, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
							consumer.accept(context);
							attemptDamage(1, stack, player, hand);
						}
					}
				}
			return true;
		}
		return false;
	}

	/**Try to harvest this crop block
	 * @return if break event should be cancelled*/
	public static boolean harvestBlock(BlockPos pos, World world, PlayerEntity player, boolean replant, ItemStack stack, @Nullable Hand hand, boolean damageItem) throws Exception {
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		// if harvestable
		if (block instanceof CropBlock && ((CropBlock) block).isMature(state)) {
			// check if player has seeds for replanting
			if (replant && !player.isCreative()) {
				ItemConvertible itemConvertible = (ItemConvertible) getSeedsItem.invoke(block);
				if (itemConvertible != null) {
					Item item = itemConvertible.asItem();
					if (player.getInventory().remove(inventoryStack -> inventoryStack != null && inventoryStack.getItem() == item, 1, player.getInventory()) != 1)
						replant = false;
				}
			}
			// break block
			CropBlock.dropStacks(state, world, pos, (BlockEntity)null, player, stack);
			if (replant && Config.replantOnHarvest)
				world.setBlockState(pos, ((CropBlock) block).getDefaultState(), Block.NOTIFY_LISTENERS);
			else
				world.removeBlock(pos, false);
			// damage hoe
			if (damageItem)
				attemptDamage(1, stack, player, hand);
			return true;
		}
		return false;
	}

}
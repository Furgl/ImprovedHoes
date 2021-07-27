package furgl.improvedHoes.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import furgl.improvedHoes.config.Config;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(FarmlandBlock.class)
public abstract class FarmlandBlockMixin {

	@Inject(method = "onLandedUpon", at = @At("HEAD"), cancellable = true) 
	private void onLandedUpon(World world, BlockPos pos, Entity entity, float distance, CallbackInfo ci) {
		if (Config.preventTrampling)
			ci.cancel();
	}
	
}
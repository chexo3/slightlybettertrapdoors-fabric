package me.chexo3.sbtfabric.mixin.hatchet;

import net.minecraft.block.BlockBase;
import net.minecraft.item.tool.Hatchet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(Hatchet.class)
public class EffectiveBlocksMixin {
	@Shadow
	private static BlockBase[] effectiveBlocks;
	@Inject(method = "<clinit>", at = @At("RETURN"))
	private static void addTrapdoorToHatchetBlocks(CallbackInfo ci) {
		effectiveBlocks = Arrays.copyOf(effectiveBlocks, effectiveBlocks.length + 1);
		effectiveBlocks[effectiveBlocks.length - 1] = BlockBase.TRAPDOOR;
	}
}
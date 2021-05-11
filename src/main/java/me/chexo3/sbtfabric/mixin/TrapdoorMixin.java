package me.chexo3.sbtfabric.mixin;

import net.minecraft.block.BlockBase;
import net.minecraft.block.Trapdoor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Living;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.level.Level;
import net.minecraft.util.maths.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Trapdoor.class)
public class TrapdoorMixin extends BlockBase {
    private boolean doPlayerBasedRotation;
    //Dummy constructor, do not touch!
    private TrapdoorMixin(int i, Material m) {
        super(i, m);
        throw new AssertionError("Don't touch my dummy constructors");
    }

    /*
    We have two methods here, allowPlacementOnTransparent, and allowUnsupportedPlacement. The first redirects the call to
    canSuffocate so that canPlaceAt will allow placement on non-solid blocks. The second one saves the return value of
    canPlaceAt so we know if the vanilla placement code should run instead of our new code.
    */

    @Redirect(method="canPlaceAt(Lnet/minecraft/level/Level;IIII)Z", at=@At(value = "INVOKE", target = "Lnet/minecraft/level/Level;canSuffocate(III)Z"))
    private boolean allowPlacementOnTransparent(Level level, int x, int y, int z) { return true; }

    @Inject(method="canPlaceAt(Lnet/minecraft/level/Level;IIII)Z", at=@At("RETURN"), cancellable=true)
    private void allowUnsupportedPlacement(Level level, int x, int y, int z, int meta, CallbackInfoReturnable<Boolean> cir) {
        doPlayerBasedRotation = !(cir.getReturnValue());
        cir.setReturnValue(true);
    }

    //Prevent trapdoors from popping off when the block behind them is non-solid.
    @Redirect(method="onAdjacentBlockUpdate(Lnet/minecraft/level/Level;IIII)V", at=@At(value = "INVOKE", target = "Lnet/minecraft/level/Level;canSuffocate(III)Z"))
    private boolean preventTrapdoorBreakage(Level level, int x, int y, int z) {
        return true;
    }

    //Correct trapdoor direction/facing with borrowed dispenser/furnace code. Uses different values for meta, so be careful if you reuse it.
    @Override
    public void afterPlaced(Level level, int x, int y, int z, Living living) {
        if (doPlayerBasedRotation) {
            int rotation = MathHelper.floor((double) (living.yaw * 4.0F / 360.0F) + 0.5D) & 3;
            int[] magic = {0, 3, 1, 2};
            level.setTileMeta(x, y, z, magic[rotation]);
        }
    }
}
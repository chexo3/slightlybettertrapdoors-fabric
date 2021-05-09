package me.chexo3.sbtfabric.mixin;

import net.minecraft.block.BlockBase;
import net.minecraft.block.Trapdoor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Living;
import net.minecraft.level.Level;
import net.minecraft.util.maths.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Trapdoor.class)
public class TrapdoorMixin extends BlockBase {

    //Dummy constructor, do not touch!
    private TrapdoorMixin(int i, Material m) {
        super(i, m);
        throw new AssertionError("Don't touch my dummy constructors");
    }

    //Allow the player to place a trapdoor. Does not prevent trapdoor from breaking when unsupported.
    @Inject(method="canPlaceAt(Lnet/minecraft/level/Level;IIII)Z", at=@At("RETURN"), cancellable=true)
    private void makeTrapdoorPlaceable(Level level, int x, int y, int z, int meta, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(super.canPlaceAt(level, x, y, z, meta));
    }

    //Prevent trapdoors from popping off when the block behind them is non-solid.
    @Redirect(method="onAdjacentBlockUpdate", at=@At(value = "INVOKE", target = "Lnet/minecraft/level/Level;canSuffocate(III)Z"))
    private boolean preventTrapdoorBreakage(Level level, int x, int y, int z) {
        return true;
    }

    //Correct trapdoor direction/facing with borrowed dispenser code. Uses different values for meta, so be careful if you reuse it.
    @Override
    public void afterPlaced(Level level, int x, int y, int z, Living living) {
        int rotation = MathHelper.floor((double)(living.yaw * 4.0F / 360.0F) + 0.5D) & 3;
        switch(rotation) {
            case 0:
                level.setTileMeta(x, y, z, 0);
                break;
            case 1:
                level.setTileMeta(x, y, z, 3);
                break;
            case 2:
                level.setTileMeta(x, y, z, 1);
                break;
            case 3:
                level.setTileMeta(x, y, z, 2);
        }
    }
}
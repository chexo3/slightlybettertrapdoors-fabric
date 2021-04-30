package me.chexo3.sbtfabric.mixin.trapdoor;

import net.minecraft.block.BlockBase;
import net.minecraft.block.Trapdoor;
import net.minecraft.block.material.Material;
import net.minecraft.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Trapdoor.class)
public class CanPlaceAtMixin extends BlockBase {
    /*
    Dummy constructor to appease javac
    Apparently, convention is to throw an
    AssertionError if somehow instantiated.
    */
    private CanPlaceAtMixin(int i, Material m) {
        super(i, m);
        throw new AssertionError("Don't touch my dummy constructors");
    }

    //Allow the player to place a trapdoor. Does not prevent trapdoor from breaking when unsupported.
    @Inject(method="canPlaceAt(Lnet/minecraft/level/Level;IIII)Z", at=@At("RETURN"), cancellable=true)
    private void makeTrapdoorPlaceable(Level level, int x, int y, int z, int meta, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(super.canPlaceAt(level, x, y, z, meta));
    }
}
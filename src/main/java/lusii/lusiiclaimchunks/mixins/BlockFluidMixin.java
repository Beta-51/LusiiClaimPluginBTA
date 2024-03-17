package lusii.lusiiclaimchunks.mixins;

import lusii.lusiiclaimchunks.LusiiClaimChunks;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockFluid;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockFluid.class, remap = false)
public class BlockFluidMixin extends Block {
	public BlockFluidMixin(String key, int id, Material material) {
		super(key, id, material);
	}
	//@Inject(method = "checkForHarden", at = @At("HEAD"), cancellable = true)
	//private void checkForHarden(World world, int x, int y, int z, CallbackInfo ci) {
//
	//	Chunk chunk = world.getChunkFromBlockCoords(x,z);
	//	int cx = chunk.xPosition;
	//	int cz = chunk.zPosition;
	//	if (LusiiClaimChunks.map.get(new LusiiClaimChunks.IntPair(cx, cz)) != null) {
	//		ci.cancel();
	//		return;
	//	}
	//}

}

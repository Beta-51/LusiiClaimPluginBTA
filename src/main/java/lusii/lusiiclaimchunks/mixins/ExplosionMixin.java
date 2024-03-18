package lusii.lusiiclaimchunks.mixins;

import lusii.lusiiclaimchunks.LusiiClaimChunks;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.world.Explosion;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.ChunkPosition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;

@Mixin(value = Explosion.class, remap = false)
public class ExplosionMixin {
	@Shadow
	protected World worldObj;
	@Shadow
	public Entity exploder;


	@Redirect(method = "calculateBlocksToDestroy()V", at = @At(value = "INVOKE", target = "Ljava/util/Set;add(Ljava/lang/Object;)Z"))
	private boolean preventClaimedDestruction(Set instance, Object e){
		ChunkPosition position = (ChunkPosition)e;
		LusiiClaimChunks.IntPair intPair = new LusiiClaimChunks.IntPair(worldObj.getChunkFromBlockCoords(position.x,position.z).xPosition,worldObj.getChunkFromBlockCoords(position.x,position.z).zPosition);
		if (LusiiClaimChunks.map.get(intPair) == null || (this.exploder != null && this.exploder.world.dimension.id != 0)) {
			instance.add(e);
			return true;
		}
		return false;
	}

}

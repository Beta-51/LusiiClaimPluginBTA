package lusii.lusiiclaimchunks.mixins;

import lusii.lusiiclaimchunks.LusiiClaimChunks;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.Explosion;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.ChunkPosition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mixin(value = Explosion.class, remap = false)
public class ExplosionMixin {
	@Shadow
	public World worldObj;

	@Shadow
	public double explosionX;
	@Shadow
	public double explosionY;
	@Shadow
	public double explosionZ;
	@Shadow
	public Entity exploder;
	@Shadow
	public float explosionSize;


	@Shadow
	public Set<ChunkPosition> destroyedBlockPositions = new HashSet();

	@Overwrite
	protected void calculateBlocksToDestroy() {
		int i = 16;

		for(int j = 0; j < i; ++j) {
			for(int l = 0; l < i; ++l) {
				for(int j1 = 0; j1 < i; ++j1) {
					if (j == 0 || j == i - 1 || l == 0 || l == i - 1 || j1 == 0 || j1 == i - 1) {
						double d = (double)((float)j / ((float)i - 1.0F) * 2.0F - 1.0F);
						double d1 = (double)((float)l / ((float)i - 1.0F) * 2.0F - 1.0F);
						double d2 = (double)((float)j1 / ((float)i - 1.0F) * 2.0F - 1.0F);
						double d3 = Math.sqrt(d * d + d1 * d1 + d2 * d2);
						d /= d3;
						d1 /= d3;
						d2 /= d3;
						float f1 = this.explosionSize * (0.7F + this.worldObj.rand.nextFloat() * 0.6F);
						double d5 = this.explosionX;
						double d7 = this.explosionY;
						double d9 = this.explosionZ;

						for(float f2 = 0.3F; !(f1 <= 0.0F); f1 -= f2 * 0.75F) {
							int j4 = MathHelper.floor_double(d5);
							int k4 = MathHelper.floor_double(d7);
							int l4 = MathHelper.floor_double(d9);
							int i5 = this.worldObj.getBlockId(j4, k4, l4);
							if (i5 > 0) {
								f1 -= (Block.blocksList[i5].getBlastResistance(this.exploder) + 0.3F) * f2;
							}

							if (f1 > 0.0F) {
								LusiiClaimChunks.IntPair intPair = new LusiiClaimChunks.IntPair(worldObj.getChunkFromBlockCoords(j4,l4).xPosition,worldObj.getChunkFromBlockCoords(j4,l4).zPosition);
								if (LusiiClaimChunks.map.get(intPair) == null) {
									this.destroyedBlockPositions.add(new ChunkPosition(j4, k4, l4));
								}
							}

							d5 += d * (double)f2;
							d7 += d1 * (double)f2;
							d9 += d2 * (double)f2;
						}
					}
				}
			}
		}

	}

}

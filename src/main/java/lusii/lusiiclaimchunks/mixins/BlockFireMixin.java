package lusii.lusiiclaimchunks.mixins;

import lusii.lusiiclaimchunks.LusiiClaimChunks;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockFire;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Random;

@Mixin(value = BlockFire.class, remap = false)
public class BlockFireMixin extends Block {
	@Shadow
	private static final int[] chanceToEncourageFire;
	@Shadow
	private static final int[] abilityToCatchFire;

	static {
		chanceToEncourageFire = new int[Block.blocksList.length];
		abilityToCatchFire = new int[Block.blocksList.length];
	}

	public BlockFireMixin(String key, int id, Material material) {
		super(key, id, material);
	}


	@Shadow
	public void setBurnResult(World world, int x, int y, int z) {

	}

	@Unique
	public String ownerFromCoords(World world, Integer x, Integer z) {
		Chunk chunk = world.getChunkFromBlockCoords(x, z);
		LusiiClaimChunks.IntPair intPair = new LusiiClaimChunks.IntPair(chunk.xPosition,chunk.zPosition);
		return LusiiClaimChunks.getOwner(intPair);
	}

	/**
	 * @author
	 * @reason
	 */
	@Overwrite
	public void updateTick(World world, int x, int y, int z, Random rand) {
		Block blockUnder = world.getBlock(x, y - 1, z);
		boolean infiniBurn = blockUnder != null && blockUnder.hasTag(BlockTags.INFINITE_BURN);


		if (!this.canFirePersist(world, x, y, z)) {
			this.setBurnResult(world, x, y, z);
		}

		if (!infiniBurn && world.getCurrentWeather() != null && world.getCurrentWeather().isPrecipitation && (world.canBlockBeRainedOn(x, y, z) || world.canBlockBeRainedOn(x - 1, y, z) || world.canBlockBeRainedOn(x + 1, y, z) || world.canBlockBeRainedOn(x, y, z - 1) || world.canBlockBeRainedOn(x, y, z + 1))) {
			this.setBurnResult(world, x, y, z);
		} else {
			int meta = world.getBlockMetadata(x, y, z);
			if (meta < 15) {
				world.setBlockMetadata(x, y, z, meta + rand.nextInt(3) / 2);
			}

			world.scheduleBlockUpdate(x, y, z, this.id, this.tickRate());
			if (!infiniBurn && !this.canNeighborCatchFire(world, x, y, z)) {
				if (!world.isBlockNormalCube(x, y - 1, z) || meta > 3) {
					this.setBurnResult(world, x, y, z);
				}

			} else if (!infiniBurn && !this.canBlockCatchFire(world, x, y - 1, z) && meta == 15 && rand.nextInt(4) == 0) {
				this.setBurnResult(world, x, y, z);
			} else {
				if (ownerFromCoords(world, x+1,z) == "-" || world.dimension.id != 0) {
					this.tryToCatchBlockOnFire(world, x + 1, y, z, 300, rand, meta);
				}
				if (ownerFromCoords(world, x-1,z) == "-" || world.dimension.id != 0) {
					this.tryToCatchBlockOnFire(world, x - 1, y, z, 300, rand, meta);
				}
				if (ownerFromCoords(world, x,z) == "-" || world.dimension.id != 0) {
					this.tryToCatchBlockOnFire(world, x, y - 1, z, 250, rand, meta);
					this.tryToCatchBlockOnFire(world, x, y + 1, z, 250, rand, meta);
				}
				if (ownerFromCoords(world, x,z-1) == "-" || world.dimension.id != 0) {
					this.tryToCatchBlockOnFire(world, x, y, z - 1, 300, rand, meta);
				}
				if (ownerFromCoords(world, x,z+1) == "-" || world.dimension.id != 0) {
					this.tryToCatchBlockOnFire(world, x, y, z + 1, 300, rand, meta);
				}

				for(int x1 = x - 1; x1 <= x + 1; ++x1) {
					for(int z1 = z - 1; z1 <= z + 1; ++z1) {
						for(int y1 = y - 1; y1 <= y + 4; ++y1) {
							if (x1 != x || y1 != y || z1 != z) {
								int a = 100;
								if (y1 > y + 1) {
									a += (y1 - (y + 1)) * 100;
								}

								int b = this.getChanceOfNeighborsEncouragingFire(world, x1, y1, z1);
								if (b > 0) {
									int c = (b + 40) / (meta + 30);
									if (c > 0 && rand.nextInt(a) <= c && (world.getCurrentWeather() == null || !world.getCurrentWeather().isPrecipitation || !world.canBlockBeRainedOn(x1, y1, z1)) && !world.canBlockBeRainedOn(x1 - 1, y1, z) && !world.canBlockBeRainedOn(x1 + 1, y1, z1) && !world.canBlockBeRainedOn(x1, y1, z1 - 1) && !world.canBlockBeRainedOn(x1, y1, z1 + 1) && this.getBurnResultId(world, x1, y1, z1) == 0 && ownerFromCoords(world, x1, z1) == "-") {
										world.setBlockAndMetadataWithNotify(x1, y1, z1, this.id, Math.min(meta + rand.nextInt(5) / 4, 15));
									}
								}
							}
						}
					}
				}

			}
		}
	}

	@Shadow
	protected int getBurnResultId(World world, int x, int y, int z) {
		return 0;
	}

	@Shadow
	public boolean canBlockCatchFire(WorldSource iblockaccess, int i, int j, int k) {
		return true;
	}

	@Shadow
	private boolean canNeighborCatchFire(World world, int x, int y, int z) {
		return true;
	}

	@Shadow
	private int getChanceOfNeighborsEncouragingFire(World world, int i, int j, int k) {
		return 0;
	}

	@Shadow
	private void tryToCatchBlockOnFire(World world, int x, int y, int z, int chance, Random random, int meta) {

	}

	@Shadow
	public boolean canFirePersist(World world, int x, int y, int z) {
		return true;
	}

}

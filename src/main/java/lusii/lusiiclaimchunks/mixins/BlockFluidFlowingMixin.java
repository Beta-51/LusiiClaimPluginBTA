package lusii.lusiiclaimchunks.mixins;

import lusii.lusiiclaimchunks.LusiiClaimChunks;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockFluid;
import net.minecraft.core.block.BlockFluidFlowing;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Objects;
import java.util.Random;
@Mixin(value = BlockFluidFlowing.class, remap = false)
public class BlockFluidFlowingMixin extends BlockFluid {

	private MinecraftServer mcServer;

	@Shadow
	int numAdjacentSources = 0;
	@Shadow
	boolean[] isOptimalFlowDirection = new boolean[4];
	@Shadow
	int[] flowCost = new int[4];


	public BlockFluidFlowingMixin(String key, int id, Material material) {
		super(key, id, material);
	}

	public void updateTick(World world, int x, int y, int z, Random rand) {
		int localFlowDecay = this.getDepth(world, x, y, z);
		byte flowDecayMod = 1;
		if (this.blockMaterial == Material.lava && world.dimension != Dimension.nether) {
			flowDecayMod = 2;
		}

		int id;
		int k1;
		if (localFlowDecay > 0) {
			id = -100;
			this.numAdjacentSources = 0;
			id = this.getSmallestFlowDecay(world, x - 1, y, z, id);
			id = this.getSmallestFlowDecay(world, x + 1, y, z, id);
			id = this.getSmallestFlowDecay(world, x, y, z - 1, id);
			id = this.getSmallestFlowDecay(world, x, y, z + 1, id);
			k1 = id + flowDecayMod;
			if (k1 >= 8 || id < 0) {
				k1 = -1;
			}

			if (this.getDepth(world, x, y + 1, z) >= 0) {
				int flowDecayAbove = this.getDepth(world, x, y + 1, z);
				if (flowDecayAbove >= 8) {
					k1 = flowDecayAbove;
				} else {
					k1 = flowDecayAbove + 8;
				}
			}

			if (this.numAdjacentSources >= 2 && this.blockMaterial == Material.water) {
				if (world.getBlockMaterial(x, y - 1, z).isSolid()) {
					k1 = 0;
				} else if (world.getBlockMaterial(x, y - 1, z) == this.blockMaterial && world.getBlockMetadata(x, y - 1, z) == 0) {
					k1 = 0;
				}
			}

			if (k1 != localFlowDecay) {
				localFlowDecay = k1;
				if (localFlowDecay < 0) {
					world.setBlockWithNotify(x, y, z, 0);
				} else {
					world.setBlockMetadataWithNotify(x, y, z, localFlowDecay);
					world.scheduleBlockUpdate(x, y, z, this.id, this.tickRate());
					world.notifyBlocksOfNeighborChange(x, y, z, this.id);
				}
			} else {
				this.setFluidStill(world, x, y, z);
			}
		} else {
			this.setFluidStill(world, x, y, z);
		}

		if (this.liquidCanDisplaceBlock(world, x, y - 1, z)) {
			id = world.getBlockId(x, y - 1, z);
			k1 = world.getBlockMetadata(x, y - 1, z);
			if (id > 0) {
				Block.blocksList[id].dropBlockWithCause(world, EnumDropCause.WORLD, x, y - 1, z, k1, (TileEntity)null);
			}

			if (localFlowDecay >= 8) {
				world.setBlockAndMetadataWithNotify(x, y - 1, z, this.id, localFlowDecay);
			} else {
				world.setBlockAndMetadataWithNotify(x, y - 1, z, this.id, localFlowDecay + 8);
			}
		} else if (localFlowDecay >= 0 && (localFlowDecay == 0 || this.blockBlocksFlow(world, x, y - 1, z))) {
			boolean[] aflag = this.getOptimalFlowDirections(world, x, y, z);
			k1 = localFlowDecay + flowDecayMod;
			if (localFlowDecay >= 8) {
				k1 = 1;
			}

			if (k1 >= 8) {
				return;
			}

			if (aflag[0]) {
				Chunk chunk = world.getChunkFromBlockCoords(x, z);
				Chunk chunkNew = world.getChunkFromBlockCoords(x-1, z);
				LusiiClaimChunks.IntPair intPair = new LusiiClaimChunks.IntPair(chunk.xPosition,chunk.zPosition);
				LusiiClaimChunks.IntPair intPairNew = new LusiiClaimChunks.IntPair(chunkNew.xPosition,chunkNew.zPosition);
				String owner1 = LusiiClaimChunks.getOwner(intPair);
				String owner2 = LusiiClaimChunks.getOwner(intPairNew);
				if (Objects.equals(LusiiClaimChunks.getOwner(intPairNew), "-") || Objects.equals(owner1, owner2) || world.dimension.id != 0) {
					this.flowIntoBlock(world, x - 1, y, z, k1);
				}

			}
			if (aflag[1]) {
				Chunk chunk = world.getChunkFromBlockCoords(x, z);
				Chunk chunkNew = world.getChunkFromBlockCoords(x+1, z);
				LusiiClaimChunks.IntPair intPair = new LusiiClaimChunks.IntPair(chunk.xPosition,chunk.zPosition);
				LusiiClaimChunks.IntPair intPairNew = new LusiiClaimChunks.IntPair(chunkNew.xPosition,chunkNew.zPosition);
				String owner1 = LusiiClaimChunks.getOwner(intPair);
				String owner2 = LusiiClaimChunks.getOwner(intPairNew);
				if (Objects.equals(LusiiClaimChunks.getOwner(intPairNew), "-") || Objects.equals(owner1, owner2) || world.dimension.id != 0) {
					this.flowIntoBlock(world, x + 1, y, z, k1);
				}
			}
			if (aflag[2]) {
				Chunk chunk = world.getChunkFromBlockCoords(x, z);
				Chunk chunkNew = world.getChunkFromBlockCoords(x, z-1);
				LusiiClaimChunks.IntPair intPair = new LusiiClaimChunks.IntPair(chunk.xPosition,chunk.zPosition);
				LusiiClaimChunks.IntPair intPairNew = new LusiiClaimChunks.IntPair(chunkNew.xPosition,chunkNew.zPosition);
				String owner1 = LusiiClaimChunks.getOwner(intPair);
				String owner2 = LusiiClaimChunks.getOwner(intPairNew);
				if (Objects.equals(LusiiClaimChunks.getOwner(intPairNew), "-") || Objects.equals(owner1, owner2) || world.dimension.id != 0) {
					this.flowIntoBlock(world, x, y, z - 1, k1);
				}
			}
			if (aflag[3]) {
				Chunk chunk = world.getChunkFromBlockCoords(x, z);
				Chunk chunkNew = world.getChunkFromBlockCoords(x, z+1);
				LusiiClaimChunks.IntPair intPair = new LusiiClaimChunks.IntPair(chunk.xPosition,chunk.zPosition);
				LusiiClaimChunks.IntPair intPairNew = new LusiiClaimChunks.IntPair(chunkNew.xPosition,chunkNew.zPosition);
				String owner1 = LusiiClaimChunks.getOwner(intPair);
				String owner2 = LusiiClaimChunks.getOwner(intPairNew);
				if (Objects.equals(LusiiClaimChunks.getOwner(intPairNew), "-") || Objects.equals(owner1, owner2) || world.dimension.id != 0) {
					this.flowIntoBlock(world, x, y, z + 1, k1);
				}
			}
		}
	}

	@Shadow
	private boolean[] getOptimalFlowDirections(World world, int i, int j, int k) {
		return new boolean[]{true};
	}

	@Shadow
	private boolean blockBlocksFlow(World world, int x, int y, int z) {
		return true;
	}

	@Shadow
	private boolean liquidCanDisplaceBlock(World world, int i, int j, int k) {
		return true;
	}

	@Shadow
	private void setFluidStill(World world, int x, int y, int z) {

	}

	@Shadow
	protected int getSmallestFlowDecay(World world, int x, int y, int z, int currentFlowDecay) {
		return 0;
	}

	@Shadow
	private void flowIntoBlock(World world, int i, int j, int k, int l) {

	}

}

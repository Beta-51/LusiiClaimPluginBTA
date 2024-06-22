package lusii.lusiiclaimchunks.mixins;

import lusii.lusiiclaimchunks.LusiiClaimChunks;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.logic.PistonDirections;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.piston.BlockPistonBase;
import net.minecraft.core.block.piston.BlockPistonMoving;
import net.minecraft.core.block.piston.TileEntityPistonMoving;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Objects;

@Mixin(value = BlockPistonBase.class, remap = false)
public class BlockPistonBaseMixin extends Block {
	@Shadow
	private boolean isSticky;

	public BlockPistonBaseMixin(String key, int id, Material material) {
		super(key, id, material);
	}
	@Shadow
	private static boolean isPushable(int id, World world, int x, int y, int z, boolean canDestroy) {
		return true;
	}

	/**
	 * @author
	 * @reason
	 */
	@Overwrite
	public void triggerEvent(World world, int x, int y, int z, int index, int data) {
		int direction = data;
		if (direction >= 6) {
			direction = 0;
		}

		if (index == 0) {
			if (this.tryExtend(world, x, y, z, direction)) {
				world.setBlockMetadataWithNotify(x, y, z, direction | 8);
				world.playSoundEffect((Entity) null, SoundCategory.WORLD_SOUNDS, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5, "tile.piston.out", 0.5F, world.rand.nextFloat() * 0.25F + 0.6F);
			}
		} else if (index == 1) {
			TileEntity tileentity = world.getBlockTileEntity(x + PistonDirections.xOffset[direction], y + PistonDirections.yOffset[direction], z + PistonDirections.zOffset[direction]);
			if (tileentity != null && tileentity instanceof TileEntityPistonMoving) {
				((TileEntityPistonMoving) tileentity).finalTick();
			}

			world.setBlockAndMetadata(x, y, z, Block.pistonMoving.id, direction);
			world.setBlockTileEntity(x, y, z, BlockPistonMoving.createTileEntity(this.id, direction, direction, false, true));
			if (this.isSticky) {
				Chunk chunk = world.getChunkFromBlockCoords(x, z);
				LusiiClaimChunks.IntPair intPair = new LusiiClaimChunks.IntPair(chunk.xPosition,chunk.zPosition);
				String owner1 = LusiiClaimChunks.getOwner(intPair);
				int x1 = x + PistonDirections.xOffset[direction] * 2;
				int y1 = y + PistonDirections.yOffset[direction] * 2;
				int z1 = z + PistonDirections.zOffset[direction] * 2;
				int id1 = world.getBlockId(x1, y1, z1);
				int meta1 = world.getBlockMetadata(x1, y1, z1);
				boolean flag = false;
				Chunk chunkNew = world.getChunkFromBlockCoords(x1, z1);
				LusiiClaimChunks.IntPair intPairNew = new LusiiClaimChunks.IntPair(chunkNew.xPosition,chunkNew.zPosition);
				String owner2 = LusiiClaimChunks.getOwner(intPairNew);

				if (id1 == Block.pistonMoving.id) {
					TileEntity tileentity1 = world.getBlockTileEntity(x1, y1, z1);
					if (tileentity1 != null && tileentity1 instanceof TileEntityPistonMoving) {
						TileEntityPistonMoving tileentitypiston = (TileEntityPistonMoving) tileentity1;
						if (tileentitypiston.getDirection() == direction && tileentitypiston.isExtending()) {
							tileentitypiston.finalTick();
							id1 = tileentitypiston.getMovedId();
							meta1 = tileentitypiston.getMovedData();
							flag = true;
						}
					}
				}

				if (!flag && id1 > 0 && isPushable(id1, world, x1, y1, z1, false) && (Block.blocksList[id1].getPistonPushReaction() == 0 || id1 == Block.pistonBase.id || id1 == Block.pistonBaseSticky.id) && (owner2 == "-" || Objects.equals(owner1, owner2) || world.dimension.id != 0)) {
					world.setBlockWithNotify(x1, y1, z1, 0);
					x += PistonDirections.xOffset[direction];
					y += PistonDirections.yOffset[direction];
					z += PistonDirections.zOffset[direction];
					world.setBlockAndMetadata(x, y, z, Block.pistonMoving.id, meta1);
					world.setBlockTileEntity(x, y, z, BlockPistonMoving.createTileEntity(id1, meta1, direction, false, false));
				} else if (!flag) {
					world.markBlockNeedsUpdate(x1,y1,z1); // Fixing a client side bug where the block would disappear when failing to be pulled by sticky piston
					world.setBlockWithNotify(x + PistonDirections.xOffset[direction], y + PistonDirections.yOffset[direction], z + PistonDirections.zOffset[direction], 0);
				}
			} else {
				world.setBlockWithNotify(x + PistonDirections.xOffset[direction], y + PistonDirections.yOffset[direction], z + PistonDirections.zOffset[direction], 0);
			}

			world.playSoundEffect((Entity) null, SoundCategory.WORLD_SOUNDS, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5, "tile.piston.in", 0.5F, world.rand.nextFloat() * 0.15F + 0.6F);
		}
	}

	@Shadow
	private boolean tryExtend(World world, int x, int y, int z, int direction) {
		return true;
	}


	/**
	 * @author
	 * @reason
	 */
	@Overwrite
	private static boolean canPushLine(World world, int x, int y, int z, int direction) {
		int xo = x + PistonDirections.xOffset[direction % 6];
		int yo = y + PistonDirections.yOffset[direction % 6];
		int zo = z + PistonDirections.zOffset[direction % 6];
		int i = 0;
		Chunk chunk = world.getChunkFromBlockCoords(x, z);
		LusiiClaimChunks.IntPair intPair = new LusiiClaimChunks.IntPair(chunk.xPosition,chunk.zPosition);
		String owner1 = LusiiClaimChunks.getOwner(intPair);

		while(i < 13) {
			if (yo > 0 && yo < world.getHeightBlocks() - 1) {
				int id = world.getBlockId(xo, yo, zo);
				if (id == 0) {
					break;
				}



				if (isPushable(id, world, xo, yo, zo, true)) {
					if (Block.blocksList[id].getPistonPushReaction() != 1) {
						if (i == 12) {
							return false;
						}

						xo += PistonDirections.xOffset[direction % 6];
						yo += PistonDirections.yOffset[direction % 6];
						zo += PistonDirections.zOffset[direction % 6];
						Chunk chunkNew = world.getChunkFromBlockCoords(xo, zo);
						LusiiClaimChunks.IntPair intPairNew = new LusiiClaimChunks.IntPair(chunkNew.xPosition,chunkNew.zPosition);
						String owner2 = LusiiClaimChunks.getOwner(intPairNew);
						if (Objects.equals(LusiiClaimChunks.getOwner(intPairNew), "-") || Objects.equals(owner1, owner2) || world.dimension.id != 0) {
							++i;
							continue;
						} else {
							return false;
						}
					}
					break;
				}

				if (i != 1 || id != Block.obsidian.id && id != Block.bedrock.id) {
					return false;
				}

				int x2 = x + PistonDirections.xOffset[direction % 6];
				int y2 = y + PistonDirections.yOffset[direction % 6];
				int z2 = z + PistonDirections.zOffset[direction % 6];
				world.playSoundEffect((EntityPlayer)null, 2001, x2, y2, z2, world.getBlockId(x2, y2, z2));
				Block.blocksList[world.getBlockId(x2, y2, z2)].dropBlockWithCause(world, EnumDropCause.SILK_TOUCH, x2, y2, z2, world.getBlockMetadata(x2, y2, z2), (TileEntity)null);
				world.setBlockWithNotify(x2, y2, z2, 0);
				break;
			}

			return false;
		}

		return true;
	}
}

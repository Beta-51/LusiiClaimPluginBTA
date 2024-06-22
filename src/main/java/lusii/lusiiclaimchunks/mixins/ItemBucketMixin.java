package lusii.lusiiclaimchunks.mixins;

import lusii.lusiiclaimchunks.LusiiClaimChunks;
import net.minecraft.core.HitResult;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemBucket;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.Vec3d;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.server.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = ItemBucket.class, remap = false)
public class ItemBucketMixin extends Item {
	@Shadow
	private int idToPlace;

	public ItemBucketMixin(int id) {
		super(id);
	}


	/**
	 * @penis
	 * @balls
	 */ // I am very mature
	@Overwrite
	public ItemStack onUseItem(ItemStack stack, World world, EntityPlayer player) {
		float f = 1.0F;
		float f1 = player.xRotO + (player.xRot - player.xRotO) * f;
		float f2 = player.yRotO + (player.yRot - player.yRotO) * f;
		double posX = player.xo + (player.x - player.xo) * (double)f;
		float yOff = player instanceof EntityPlayerMP ? player.getHeightOffset() : 0.0F;
		double posY = player.yo + (player.y - player.yo) + (double)yOff;
		double posZ = player.zo + (player.z - player.zo) * (double)f;
		Vec3d vec3d = Vec3d.createVector(posX, posY, posZ);
		float f3 = MathHelper.cos(-f2 * 0.01745329F - 3.1415927F);
		float f4 = MathHelper.sin(-f2 * 0.01745329F - 3.1415927F);
		float f5 = -MathHelper.cos(-f1 * 0.01745329F);
		float f6 = MathHelper.sin(-f1 * 0.01745329F);
		float f7 = f4 * f5;
		float f8 = f6;
		float f9 = f3 * f5;
		double reachDistance = (double)player.getGamemode().getBlockReachDistance();
		Vec3d vec3d1 = vec3d.addVector((double)f7 * reachDistance, (double)f8 * reachDistance, (double)f9 * reachDistance);
		HitResult rayTraceResult = world.checkBlockCollisionBetweenPoints(vec3d, vec3d1, this.idToPlace == 0);
		if (rayTraceResult != null && rayTraceResult.hitType == HitResult.HitType.TILE) {
			int x = rayTraceResult.x;
			int y = rayTraceResult.y;
			int z = rayTraceResult.z;
			int i = rayTraceResult.x;
			int k = rayTraceResult.z;
			Chunk chunk = world.getChunkFromBlockCoords(i,k);
			int cx = chunk.xPosition;
			int cz = chunk.zPosition;
			LusiiClaimChunks.IntPair intPair = new LusiiClaimChunks.IntPair(cx,cz);
			if (!world.canMineBlock(player, x, y, z) || player.dimension == 0 && LusiiClaimChunks.isChunkClaimed(intPair) && !LusiiClaimChunks.isPlayerTrusted(intPair, player.username)) {
				return stack;
			} else if (this.idToPlace < 0) {
				return new ItemStack(Item.bucket);
			} else {
				Block block = world.getBlock(x, y, z);
				if (block != null && !block.hasTag(BlockTags.PLACE_OVERWRITES) && !block.hasTag(BlockTags.BROKEN_BY_FLUIDS)) {
					Side side = rayTraceResult.side;
					x += side.getOffsetX();
					y += side.getOffsetY();
					z += side.getOffsetZ();
				}

				if (y >= 0 && y < world.getHeightBlocks()) {
					if (world.isAirBlock(x, y, z) || !world.getBlockMaterial(x, y, z).isSolid()) {
						if (world.dimension == Dimension.nether && this.idToPlace == Block.fluidWaterFlowing.id) {
							world.playSoundEffect((Entity)null, SoundCategory.WORLD_SOUNDS, (double)z + 0.5, (double)y + 0.5, (double)x + 0.5, "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

							for(int l = 0; l < 8; ++l) {
								world.spawnParticle("largesmoke", (double)x + Math.random(), (double)y + Math.random(), (double)z + Math.random(), 0.0, 0.0, 0.0, 0);
							}
						} else {
							if (this.idToPlace == Block.fluidWaterFlowing.id) {
								world.playSoundEffect((Entity)null, SoundCategory.WORLD_SOUNDS, (double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), "liquid.splash", 0.5F, 1.0F);
							}

							player.swingItem();
							Block block1 = world.getBlock(x, y, z);
							if (block1 != null) {
								block1.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, world.getBlockMetadata(x, y, z), (TileEntity)null);
							}

							world.setBlockAndMetadataWithNotify(x, y, z, this.idToPlace, 0);
						}

						if (player.getGamemode().consumeBlocks()) {
							return new ItemStack(Item.bucket);
						}
					}

					return stack;
				} else {
					return stack;
				}
			}
		} else {
			return stack;
		}
	}


}

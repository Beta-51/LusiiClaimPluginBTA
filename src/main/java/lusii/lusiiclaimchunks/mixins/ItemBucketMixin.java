package lusii.lusiiclaimchunks.mixins;

import lusii.lusiiclaimchunks.LusiiClaimChunks;
import net.minecraft.core.HitResult;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemBucket;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.phys.Vec3d;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = ItemBucket.class, remap = false)
public class ItemBucketMixin extends Item {
    public ItemBucketMixin(int id) {
		super(id);
	}

	@Inject(method = "onItemRightClick(Lnet/minecraft/core/item/ItemStack;Lnet/minecraft/core/world/World;Lnet/minecraft/core/entity/player/EntityPlayer;)Lnet/minecraft/core/item/ItemStack;",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/World;checkBlockCollisionBetweenPoints(Lnet/minecraft/core/util/phys/Vec3d;Lnet/minecraft/core/util/phys/Vec3d;Z)Lnet/minecraft/core/HitResult;", shift = At.Shift.AFTER, by = 1), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private void bucketProtect(ItemStack itemstack, World world, EntityPlayer entityplayer, CallbackInfoReturnable<ItemStack> cir, float f, float f1, float f2, double d, float yOff, double d1, double d2, Vec3d vec3d, float f3, float f4, float f5, float f6, float f7, float f8, float f9, double d3, Vec3d vec3d1){		HitResult movingobjectposition = world.checkBlockCollisionBetweenPoints(vec3d, vec3d1, true);
		if (movingobjectposition != null &&movingobjectposition.hitType == HitResult.HitType.TILE){
			int i = movingobjectposition.x;
			int k = movingobjectposition.z;
			Chunk chunk = world.getChunkFromBlockCoords(i,k);
			int cx = chunk.xPosition;
			int cz = chunk.zPosition;
            LusiiClaimChunks.IntPair intPair = new LusiiClaimChunks.IntPair(cx,cz);
			if (entityplayer.dimension == 0 && LusiiClaimChunks.isChunkClaimed(intPair) && !LusiiClaimChunks.isPlayerTrusted(intPair, entityplayer.username)) {
				cir.setReturnValue(itemstack);
			}
		}
	}
}

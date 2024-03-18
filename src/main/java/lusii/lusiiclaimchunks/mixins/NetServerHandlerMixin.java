package lusii.lusiiclaimchunks.mixins;

import lusii.lusiiclaimchunks.LusiiClaimChunks;
import net.minecraft.core.block.Block;
import net.minecraft.core.net.ICommandListener;
import net.minecraft.core.net.handler.NetHandler;
import net.minecraft.core.net.packet.Packet10Flying;
import net.minecraft.core.net.packet.Packet14BlockDig;
import net.minecraft.core.net.packet.Packet15Place;
import net.minecraft.core.net.packet.Packet53BlockChange;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.EntityPlayerMP;
import net.minecraft.server.net.handler.NetServerHandler;
import net.minecraft.server.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

import static java.lang.Math.floor;

@Mixin(value = NetServerHandler.class,remap = false,priority = 0)
public class NetServerHandlerMixin extends NetHandler implements ICommandListener {
	@Shadow
	private MinecraftServer mcServer;
	@Shadow
	private EntityPlayerMP playerEntity;
	@Shadow
	private boolean hasMoved = true;

	@Inject(method = "handleFlying", at = @At("HEAD"))
	public void handleFlyingClaimChunk(Packet10Flying packet, CallbackInfo ci) {
		int newPosXClaimChunks = (int) floor(packet.xPosition);
		int newPosZClaimChunks = (int) floor(packet.zPosition);
		int oldPosXClaimChunks = (int) floor(this.playerEntity.x);
		int oldPosZClaimChunks = (int) floor(this.playerEntity.z);
		Chunk chunk = this.mcServer.getDimensionWorld(this.playerEntity.dimension).getChunkFromBlockCoords(oldPosXClaimChunks, oldPosZClaimChunks);
		Chunk chunkNew = this.mcServer.getDimensionWorld(this.playerEntity.dimension).getChunkFromBlockCoords(newPosXClaimChunks, newPosZClaimChunks);
		LusiiClaimChunks.IntPair intPair = new LusiiClaimChunks.IntPair(chunk.xPosition,chunk.zPosition);
		LusiiClaimChunks.IntPair intPairNew = new LusiiClaimChunks.IntPair(chunkNew.xPosition,chunkNew.zPosition);
		if (this.playerEntity.dimension == 0){
			if (LusiiClaimChunks.map.get(intPair) == null && LusiiClaimChunks.map.get(intPairNew) != null && packet.moving && this.hasMoved) {
				this.mcServer.playerList.sendChatMessageToPlayer(this.playerEntity.username, "§3Now entering §r" + LusiiClaimChunks.map.get(intPairNew).get(0) + "'s §3claim.");
			} else if (LusiiClaimChunks.map.get(intPair) != null && LusiiClaimChunks.map.get(intPairNew) == null && packet.moving && this.hasMoved) {
				this.mcServer.playerList.sendChatMessageToPlayer(this.playerEntity.username, "§3Now entering §dWilderness§3.");
			} else if (LusiiClaimChunks.map.get(intPair) != null && LusiiClaimChunks.map.get(intPairNew) != null && packet.moving && this.hasMoved) {
				if (!Objects.equals(LusiiClaimChunks.map.get(intPair).get(0), LusiiClaimChunks.map.get(intPairNew).get(0))) {
					this.mcServer.playerList.sendChatMessageToPlayer(this.playerEntity.username, "§3Now entering §r" + LusiiClaimChunks.map.get(intPairNew).get(0) + "'s §3claim.");
				}
			}
		}
	}

	@Inject(method = "handleBlockDig", at = @At("HEAD"), cancellable = true)
	public void handleBlockDigClaimChunk(Packet14BlockDig packet, CallbackInfo ci) {
		int bx = packet.xPosition;
		int bz = packet.zPosition;
		boolean allowed = false;
		WorldServer worldserver = this.mcServer.getDimensionWorld(this.playerEntity.dimension);
		Block block = worldserver.getBlock(packet.xPosition,packet.yPosition,packet.zPosition);
		LusiiClaimChunks.IntPair intPair = new LusiiClaimChunks.IntPair(this.mcServer.getDimensionWorld(this.playerEntity.dimension).getChunkFromBlockCoords(packet.xPosition,packet.zPosition).xPosition,this.mcServer.getDimensionWorld(this.playerEntity.dimension).getChunkFromBlockCoords(packet.xPosition,packet.zPosition).zPosition);
		if (block != null){
			if (this.playerEntity.dimension == 0 & packet.status == 2 || this.playerEntity.dimension == 0 & (this.playerEntity.gamemode == Gamemode.creative || this.playerEntity.getCurrentPlayerStrVsBlock(block) >= 1.0) & packet.status == 0) {
				if (LusiiClaimChunks.map.get(intPair) != null) {
					for (String name : LusiiClaimChunks.map.get(intPair)) {
						if (this.playerEntity.username.equals(name)) {
							allowed = true;
							break;
						}
					}
					if (allowed) {

					} else {
						this.mcServer.playerList.sendChatMessageToPlayer(this.playerEntity.username, "§e§lHey!§r This chunk does not belong to you!");
						this.playerEntity.playerNetServerHandler.sendPacket(new Packet53BlockChange(packet.xPosition, packet.yPosition, packet.zPosition, worldserver));
						ci.cancel();
						return;
					}
				}
			}
		}
	}
	@Inject(method = "handlePlace", at = @At("HEAD"), cancellable = true)
	public void handlePlaceChunkClaim(Packet15Place packet, CallbackInfo ci) {
		int bx = packet.xPosition;
		int bz = packet.zPosition;


		int x1 = packet.xPosition;
		int y1 = packet.yPosition;
		int z1 = packet.zPosition;
		Direction direction = packet.direction;
		x1 += direction.getOffsetX();
		y1 += direction.getOffsetY();
		z1 += direction.getOffsetZ();

		boolean allowed = false;
		LusiiClaimChunks.IntPair intPair = new LusiiClaimChunks.IntPair(this.mcServer.getDimensionWorld(this.playerEntity.dimension).getChunkFromBlockCoords(packet.xPosition,packet.zPosition).xPosition,this.mcServer.getDimensionWorld(this.playerEntity.dimension).getChunkFromBlockCoords(packet.xPosition,packet.zPosition).zPosition);
		LusiiClaimChunks.IntPair intPair2 = new LusiiClaimChunks.IntPair(this.mcServer.getDimensionWorld(this.playerEntity.dimension).getChunkFromBlockCoords(x1,z1).xPosition,this.mcServer.getDimensionWorld(this.playerEntity.dimension).getChunkFromBlockCoords(x1,z1).zPosition);
		if (this.playerEntity.dimension == 0) {
			if (LusiiClaimChunks.map.get(intPair) != null) {
				for (String name : LusiiClaimChunks.map.get(intPair)) {
					if (this.playerEntity.username.equals(name)) {
						allowed = true;
						break;
					}
				} if (allowed) {

				} else {
					int x = packet.xPosition;
					int y = packet.yPosition;
					int z = packet.zPosition;
					Direction direction2 = packet.direction;
					double xPlaced = packet.xPlaced;
					double yPlaced = packet.yPlaced;
					this.mcServer.playerList.sendChatMessageToPlayer(this.playerEntity.username, "§e§lHey!§r This chunk does not belong to you!");
					WorldServer worldserver = this.mcServer.getDimensionWorld(this.playerEntity.dimension);
					this.playerEntity.playerNetServerHandler.sendPacket(new Packet53BlockChange(x, y, z, worldserver));
					x += direction2.getOffsetX();
					y += direction2.getOffsetY();
					z += direction2.getOffsetZ();
					this.playerEntity.playerNetServerHandler.sendPacket(new Packet53BlockChange(x, y, z, worldserver));
					this.playerEntity.craftingInventory.updateInventory();
					ci.cancel();
					return;
				}
			} else if (LusiiClaimChunks.map.get(intPair2) != null) {
				for (String name : LusiiClaimChunks.map.get(intPair2)) {
					if (this.playerEntity.username.equals(name)) {
						allowed = true;
						break;
					}
				} if (allowed) {

				} else {
					int x = packet.xPosition;
					int y = packet.yPosition;
					int z = packet.zPosition;
					Direction direction2 = packet.direction;
					double xPlaced = packet.xPlaced;
					double yPlaced = packet.yPlaced;
					this.mcServer.playerList.sendChatMessageToPlayer(this.playerEntity.username, "§e§lHey!§r This chunk does not belong to you!");
					WorldServer worldserver = this.mcServer.getDimensionWorld(this.playerEntity.dimension);
					this.playerEntity.playerNetServerHandler.sendPacket(new Packet53BlockChange(x, y, z, worldserver));
					x += direction2.getOffsetX();
					y += direction2.getOffsetY();
					z += direction2.getOffsetZ();
					this.playerEntity.playerNetServerHandler.sendPacket(new Packet53BlockChange(x, y, z, worldserver));
					this.playerEntity.craftingInventory.updateInventory();
					ci.cancel();
					return;
				}
			}

		}
	}



	@Shadow
	public void log(String string) {

	}

	@Shadow
	public String getUsername() {
		return null;
	}

	@Shadow
	public boolean isServerHandler() {
		return false;
	}
}

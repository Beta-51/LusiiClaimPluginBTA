package lusii.lusiiclaimchunks;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.TomlConfigHandler;
import turniplabs.halplibe.util.toml.Toml;

import javax.annotation.Nullable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class LusiiClaimChunks implements ModInitializer {
    public static final String MOD_ID = "lusiiclaimchunk";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static HashMap<IntPair, List<String>> chunkTrustedMap = new HashMap<>();
	private static HashMap<String, Integer> claimedChunksMap = new HashMap<>();

	public static final TomlConfigHandler CONFIG;
	static {
		Toml toml = new Toml();
		toml.addCategory("ClaimUtil");
		toml.addEntry("ClaimUtil.cost", "Cost per chunk (In points)", 0);
		CONFIG = new TomlConfigHandler(MOD_ID, toml);
		cost = CONFIG.getInt("ClaimUtil.cost");
	}
	public static int cost;

    @Override
    public void onInitialize() {

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("LusiiChunksClaim.ser"))) {
			HashMap<IntPair, List<String>> reopenedMap = (HashMap<IntPair, List<String>>) ois.readObject();

			chunkTrustedMap = reopenedMap;
			System.out.println("LusiiChunksClaim reopened from disk:");
			System.out.println(reopenedMap);
		} catch (IOException | ClassNotFoundException ignored) {
		}
        LOGGER.info("LusiiClaimChunks initialized.");
    }

	protected static void saveHashMap() {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("LusiiChunksClaim.ser"))) {
			oos.writeObject(chunkTrustedMap);
			//System.out.println("HashMap saved to disk.");
		} catch (IOException ignored) {
			LOGGER.warn("Chunk claims failed to save to disk!");
		}
	}
	@Nullable
	public static List<String> getTrustedPlayersInChunk(IntPair chunkCoords){
		return chunkTrustedMap.get(chunkCoords);
	}
	public static void addTrustedPlayerToChunk(IntPair chunkCoords, String username){
		chunkTrustedMap.putIfAbsent(chunkCoords, new ArrayList<>());
		if (!chunkTrustedMap.get(chunkCoords).contains(username)){
			chunkTrustedMap.get(chunkCoords).add(username);
		}
		LusiiClaimChunks.saveHashMap();
	}
	public static void setOwnerToChunk(IntPair chunkCoords, String username){
		chunkTrustedMap.putIfAbsent(chunkCoords, new ArrayList<>());
		if (chunkTrustedMap.get(chunkCoords).contains(username)){
			chunkTrustedMap.get(chunkCoords).remove(username);
			chunkTrustedMap.get(chunkCoords).add(0, username);
		} else {
			chunkTrustedMap.get(chunkCoords).add(0, username);
		}
	}
	public static void removedPlayerFromChunk(IntPair chunkCoords, String username){
		if (!chunkTrustedMap.containsKey(chunkCoords)) return;
        chunkTrustedMap.get(chunkCoords).remove(username);
		LusiiClaimChunks.saveHashMap();
	}
	public static void deleteClaim(IntPair chunkCoords){
		chunkTrustedMap.remove(chunkCoords);
		LusiiClaimChunks.saveHashMap();
	}
	public static boolean isPlayerTrusted(IntPair chunkCoords, String username){
		if (isChunkClaimed(chunkCoords)){
			List<String> trustedNames = getTrustedPlayersInChunk(chunkCoords);
			return trustedNames.contains(username);
		}
		return false;
	}
	public static boolean isPlayerOwner(IntPair chunkCoords, String username){
		if (isChunkClaimed(chunkCoords)){
			return getTrustedPlayersInChunk(chunkCoords).get(0).equals(username);
		}
		return false;
	}
	public static boolean isChunkClaimed(IntPair chunkCoords){
		return chunkTrustedMap.containsKey(chunkCoords);
	}


	public static class IntPair implements Serializable {
		private static final long serialVersionUID = 1L; // Ensures version compatibility during deserialization
		private int x;
		private int y;

		public IntPair(int x, int y) {
			this.x = x;
			this.y = y;
		}

		// Override hashCode and equals methods for proper functioning in HashMap
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			IntPair other = (IntPair) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}
	}
}

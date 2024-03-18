package lusii.lusiiclaimchunks;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.world.chunk.ChunkPosition;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.Sys;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.License;
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
import java.util.*;


public class LusiiClaimChunks implements ModInitializer {
    public static final String MOD_ID = "lusiiclaimchunk";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static HashMap<IntPair, List<String>> chunkTrustedMap = new HashMap<>();
	public static HashMap<String, Integer> claimedChunksMap = new HashMap<>();

	public static final TomlConfigHandler CONFIG;
	static {
		Toml toml = new Toml();
		toml.addCategory("ClaimUtil");
		toml.addEntry("ClaimUtil.cost", "Cost per chunk (In points), parameter x being the number of chunks already claimed by the player", "100 * x");
		toml.addEntry("ClaimUtil.maxClaims", "Max claims a user is allowed to have. 0 = no limit", 25);
		CONFIG = new TomlConfigHandler(MOD_ID, toml);
		costEquation = CONFIG.getString("ClaimUtil.cost");
		maxClaims = CONFIG.getInt("ClaimUtil.maxClaims");

		License.iConfirmNonCommercialUse("UselessBullets");
	}
	private static String costEquation;
	public static int maxClaims;

	public static int getCost(String username){
		Argument x = new Argument("x = " + claimedChunksMap.getOrDefault(username, 0));
		Expression expression = new Expression(costEquation, x);
		return (int) expression.calculate();
	}
    @Override
    public void onInitialize() {

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("LusiiChunksClaim.ser"))) {
			HashMap<IntPair, List<String>> reopenedMap = (HashMap<IntPair, List<String>>) ois.readObject();

			chunkTrustedMap = reopenedMap;
			System.out.println("LusiiChunksClaim reopened from disk:");
			System.out.println(reopenedMap);
		} catch (IOException | ClassNotFoundException ignored) {
		}
		calculateClaimedChunks();
        LOGGER.info("LusiiClaimChunks initialized.");
    }

	protected static void saveHashMap() {
		calculateClaimedChunks();
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("LusiiChunksClaim.ser"))) {
			oos.writeObject(chunkTrustedMap);
			//System.out.println("HashMap saved to disk.");
		} catch (IOException ignored) {
			LOGGER.warn("Chunk claims failed to save to disk! This is a major issue if you do not want griefing!");
		}
	}
	protected static void calculateClaimedChunks(){
		claimedChunksMap.clear();
		for (IntPair pair : chunkTrustedMap.keySet()){
			String owner = chunkTrustedMap.get(pair).get(0);
			int val = claimedChunksMap.getOrDefault(owner, 0);
			val++;
			claimedChunksMap.put(owner, val);
		}
	}
	public static @NotNull ArrayList<String> listClaimedChunks(String username){
		ArrayList<String> claimedChunksList = new ArrayList<>();
		for (IntPair pair : chunkTrustedMap.keySet()){
			String owner = chunkTrustedMap.get(pair).get(0);
			if (Objects.equals(owner, username)) {
				claimedChunksList.add("(" + pair.x + "," + pair.y + ")");
			}
		}
		return claimedChunksList;
	}
	public static void deleteAllClaimedChunks(String username) {
		Iterator<IntPair> iterator = chunkTrustedMap.keySet().iterator(); // This method is used because i would get ConcurrentModificationException if i tried a for loop.
		while (iterator.hasNext()) {
			IntPair pair = iterator.next();
			String owner = chunkTrustedMap.get(pair).get(0);
			if (Objects.equals(owner, username)) {
				iterator.remove();
				deleteClaim(pair);
			}
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
		LusiiClaimChunks.saveHashMap();
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

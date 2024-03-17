package lusii.lusiiclaimchunks;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;
import turniplabs.halplibe.util.TomlConfigHandler;
import turniplabs.halplibe.util.toml.Toml;

import java.io.*;
import java.util.*;


public class LusiiClaimChunks implements ModInitializer, GameStartEntrypoint, RecipeEntrypoint {
    public static final String MOD_ID = "lusiiclaimchunk";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static HashMap<IntPair, List<String>> map = new HashMap<>();

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

			List<String> googy = new ArrayList<>(Collections.singletonList("tracks"));
			googy.add("arguoehapoe");

			map = reopenedMap;
			System.out.println("LusiiChunksClaim reopened from disk:");
			System.out.println(reopenedMap);
		} catch (IOException | ClassNotFoundException e) {

		}
        LOGGER.info("LusiiClaimChunks initialized.");
    }

	@Override
	public void beforeGameStart() {

	}

	@Override
	public void afterGameStart() {

	}

	@Override
	public void onRecipesReady() {

	}

	public static void saveHashMap() {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("LusiiChunksClaim.ser"))) {
			oos.writeObject(map);
			//System.out.println("HashMap saved to disk.");
		} catch (IOException e) {

		}
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

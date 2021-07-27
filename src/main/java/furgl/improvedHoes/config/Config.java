package furgl.improvedHoes.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import furgl.improvedHoes.utils.Utils;
import furgl.improvedHoes.utils.Utils.Range;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class Config {

	private static final Gson GSON = new GsonBuilder()
			.setPrettyPrinting()
			.serializeNulls()
			.create();
	private static final String FILE = "./config/improvedHoes.cfg";
	private static File file;

	public static boolean leftClickWithHoeToBreak;
	public static boolean rightClickToHarvest;
	public static boolean rightClickWithHoeToHarvest;
	public static boolean rightClickWithHoeToTill;
	public static boolean preventTrampling;
	public static boolean replantOnHarvest;
	public static boolean workWhileSneaking;

	public static void init() {
		try {
			// create file if it doesn't already exist
			file = new File(FILE);
			if (!file.exists()) {
				file.createNewFile();
				writeToFile(true);
			}
			readFromFile();
			// write current values / defaults to file
			writeToFile(false);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void readFromFile() {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			JsonObject parser = (JsonObject) JsonHelper.deserialize(reader);

			JsonElement element;
			for (Range range : Utils.Range.values()) {
				range.items.clear();
				element = parser.get("Improved Hoes ("+range.name().substring(1)+")");
				if (!element.isJsonNull() && element.isJsonArray()) {
					Iterator<JsonElement> it = element.getAsJsonArray().iterator();
					while (it.hasNext()) {
						String str = it.next().getAsString();
						Item item = Registry.ITEM.get(new Identifier(str));
						if (item != null)
							range.items.add(item);
					}
				}
				else 
					range.items = Lists.newArrayList(range.defaultItems);
			}
			
			element = parser.get("Left-Click Crops with Improved Hoe to Break in Range");
			leftClickWithHoeToBreak = element.getAsBoolean();
			
			element = parser.get("Right-Click Crops without Improved Hoe to Harvest");
			rightClickToHarvest = element.getAsBoolean();
			
			element = parser.get("Right-Click Crops with Improved Hoe to Harvest in Range");
			rightClickWithHoeToHarvest = element.getAsBoolean();
			
			element = parser.get("Right-Click Ground with Improved Hoe to Till in Range");
			rightClickWithHoeToTill = element.getAsBoolean();
		
			element = parser.get("Prevent Trampling Crops");
			preventTrampling = element.getAsBoolean();
			
			element = parser.get("Replant on Harvest");
			replantOnHarvest = element.getAsBoolean();
			
			element = parser.get("Work while Sneaking");
			workWhileSneaking = element.getAsBoolean();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void writeToFile(boolean writeDefaults) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			JsonObject obj = new JsonObject();

			for (Range range : Utils.Range.values()) {
				JsonArray array = new JsonArray();
				for (Item item : (writeDefaults ? range.defaultItems : range.items))
					array.add(Registry.ITEM.getId(item).toString());
				obj.add("Improved Hoes ("+range.name().substring(1)+")", array);
			}
			
			obj.addProperty("Left-Click Crops with Improved Hoe to Break in Range", writeDefaults ? true : leftClickWithHoeToBreak);
			obj.addProperty("Right-Click Crops without Improved Hoe to Harvest", writeDefaults ? true : rightClickToHarvest);
			obj.addProperty("Right-Click Crops with Improved Hoe to Harvest in Range", writeDefaults ? true : rightClickWithHoeToHarvest);
			obj.addProperty("Right-Click Ground with Improved Hoe to Till in Range", writeDefaults ? true : rightClickWithHoeToTill);
			obj.addProperty("Prevent Trampling Crops", writeDefaults ? true : preventTrampling);
			obj.addProperty("Replant on Harvest", writeDefaults ? true : replantOnHarvest);
			obj.addProperty("Work while Sneaking", writeDefaults ? false : workWhileSneaking);

			writer.write(GSON.toJson(obj));
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
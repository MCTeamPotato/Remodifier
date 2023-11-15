package com.teampotato.modifiers.common.config.json;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.teampotato.modifiers.ModifiersMod;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraftforge.fml.loading.FMLLoader;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class JsonConfigInitialier {

    private static final Path ROOT = FMLLoader.getGamePath().resolve("config").resolve("remodifier");
    private static final String NAME = "name", WEIGHT = "weight", ATTRIBUTES = "attributes", AMOUNTS = "amounts", OPERATION_ID = "operationId";

    private static @NotNull FileWriter writeFile(File configFile) throws IOException {
        JsonObject config = new JsonObject();
        config.addProperty(NAME, "violent");
        config.addProperty(WEIGHT, 100);
        config.addProperty(ATTRIBUTES, "minecraft:generic.attack_speed");
        config.addProperty(AMOUNTS, "0.04");
        config.addProperty(OPERATION_ID, "2");
        FileWriter fileWriter = new FileWriter(configFile);
        fileWriter.write(config.toString());
        return fileWriter;
    }

    static {
        ROOT.toFile().mkdirs();
        mkdirs("armor", "curios", "shield", "tool", "bow");
        generateExampleFile();
    }

    private static void mkdirs(String @NotNull ... strings) {
        for (String s : strings) ROOT.resolve(s).toFile().mkdirs();
    }

    private static void generateExampleFile() {
        File violent = new File(ROOT.resolve("armor").toFile(), "violent.json");
        if (!violent.exists()) {
            try {
                FileWriter writer = writeFile(violent);
                writer.close();
            } catch (Throwable throwable) {
                ModifiersMod.LOGGER.error("Error occurs during example json file generation", throwable);
            }
        }
    }

    private static final Iterable<File> BOW_JSONS = readFiles(ROOT.resolve("bow")),
            CURIOS_JSONS = readFiles(ROOT.resolve("curios")),
            ARMOR_JSONS = readFiles(ROOT.resolve("armor")),
            SHIELD_JSONS = readFiles(ROOT.resolve("shield")),
            TOOL_JSONS = readFiles(ROOT.resolve("tool"));

    public static final Iterable<? extends String> BOW_NAMES = getElements(BOW_JSONS, NAME),
            BOW_WEIGHTS = getElements(BOW_JSONS, WEIGHT),
            BOW_ATTRIBUTES = getElements(BOW_JSONS, ATTRIBUTES),
            BOW_AMOUNTS = getElements(BOW_JSONS, AMOUNTS),
            BOW_OPERATIONS_IDS = getElements(BOW_JSONS, OPERATION_ID);

    public static final Iterable<String> CURIOS_NAMES = getElements(CURIOS_JSONS, NAME),
            CURIOS_WEIGHTS = getElements(CURIOS_JSONS, WEIGHT),
            CURIOS_ATTRIBUTES = getElements(CURIOS_JSONS, ATTRIBUTES),
            CURIOS_AMOUNTS = getElements(CURIOS_JSONS, AMOUNTS),
            CURIOS_OPERATIONS_IDS = getElements(CURIOS_JSONS, OPERATION_ID);

    public static final Iterable<String> ARMOR_NAMES = getElements(ARMOR_JSONS, NAME),
            ARMOR_WEIGHTS = getElements(ARMOR_JSONS, WEIGHT),
            ARMOR_ATTRIBUTES = getElements(ARMOR_JSONS, ATTRIBUTES),
            ARMOR_AMOUNTS = getElements(ARMOR_JSONS, AMOUNTS),
            ARMOR_OPERATIONS_IDS = getElements(ARMOR_JSONS, OPERATION_ID);

    public static final Iterable<String> SHIELD_NAMES = getElements(SHIELD_JSONS, NAME),
            SHIELD_WEIGHTS = getElements(SHIELD_JSONS, WEIGHT),
            SHIELD_ATTRIBUTES = getElements(SHIELD_JSONS, ATTRIBUTES),
            SHIELD_AMOUNTS = getElements(SHIELD_JSONS, AMOUNTS),
            SHIELD_OPERATIONS_IDS = getElements(SHIELD_JSONS, OPERATION_ID);

    public static final Iterable<String> TOOL_NAMES = getElements(TOOL_JSONS, NAME),
            TOOL_WEIGHTS = getElements(TOOL_JSONS, WEIGHT),
            TOOL_ATTRIBUTES = getElements(TOOL_JSONS, ATTRIBUTES),
            TOOL_AMOUNTS = getElements(TOOL_JSONS, AMOUNTS),
            TOOL_OPERATIONS_IDS = getElements(TOOL_JSONS, OPERATION_ID);

    private static @NotNull Iterable<String> getElements(@NotNull Iterable<File> files, String element) {
        List<String> names = new LinkedList<>();
        for (File file : files) {
            try {
                FileReader fileReader = new FileReader(file);
                JsonObject configObject = new JsonParser().parse(fileReader).getAsJsonObject();
                names.add(configObject.get(element).getAsString());
            } catch (Throwable throwable) {
                ModifiersMod.LOGGER.error("Error occurs during " + file.getName() + " reading", throwable);
            }
        }
        return names;
    }

    private static @NotNull Iterable<File> readFiles(@NotNull Path path) {
        List<File> fileList = new ObjectArrayList<>();
        File folder = path.toFile();
        if (!folder.exists() || !folder.isDirectory()) return Collections.emptySet();
        File[] files = folder.listFiles();
        if (files == null) return Collections.emptySet();
        for (File file : files) {
            if (!file.isFile() || !file.getName().toLowerCase().endsWith(".json")) continue;
            fileList.add(file);
        }
        return fileList;
    }
}

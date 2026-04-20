package com.moepus.fakesight;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.caffeinemc.mods.sodium.client.gui.options.storage.OptionStorage;
import net.neoforged.fml.loading.FMLPaths;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;

public class FakeSightConfig implements OptionStorage<FakeSightConfig> {
    private static final Logger LOGGER = LoggerFactory.getLogger("fakesight");
    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .excludeFieldsWithModifiers(Modifier.PRIVATE)
            .create();

    public int requestDistance = 48;

    @Override
    public FakeSightConfig getData() {
        return this;
    }

    @Override
    public void save() {
        try {
            Files.writeString(getConfigPath(), GSON.toJson(this));
        } catch (IOException e) {
            LOGGER.error("Failed to write FakeSight config file", e);
        }
    }

    private static Path getConfigPath() {
        return FMLPaths.CONFIGDIR.get().resolve("fakesight.json");
    }

    private static FakeSightConfig loadOrCreate() {
        var path = getConfigPath();
        if (Files.exists(path)) {
            try (FileReader reader = new FileReader(path.toFile())) {
                var conf = GSON.fromJson(reader, FakeSightConfig.class);
                if (conf != null) {
                    conf.save();
                    return conf;
                } else {
                    LOGGER.error("Failed to load FakeSight config, resetting");
                }
            } catch (IOException e) {
                LOGGER.error("Could not parse FakeSight config", e);
            }
        }
        var config = new FakeSightConfig();
        config.save();
        return config;
    }

    public static FakeSightConfig CONFIG = loadOrCreate();
}
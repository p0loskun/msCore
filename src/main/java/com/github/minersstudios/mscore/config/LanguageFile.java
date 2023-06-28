package com.github.minersstudios.mscore.config;

import com.github.minersstudios.mscore.MSCore;
import com.github.minersstudios.mscore.utils.ChatUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.Locale;

@SuppressWarnings("unused")
public class LanguageFile {
    private final String sourceUrl;
    private final String languageCode;
    private final JsonObject translations;
    private File file;

    /**
     * Registry for {@link GlobalTranslator}
     */
    public static TranslationRegistry registry = TranslationRegistry.create(Key.key("ms"));

    private LanguageFile(
            @NotNull String sourceUrl,
            @NotNull String languageCode
    ) {
        this.sourceUrl = sourceUrl;
        this.languageCode = languageCode;
        this.file = this.loadFile();
        this.translations = this.loadTranslations();
    }

    /**
     * Loads the translations from the language file to {@link GlobalTranslator}
     * <br>
     * If the language file does not exist, it will be downloaded from the language repository
     *
     * @param sourceUrl    URL of the language repository
     * @param languageCode Language code
     */
    public static void loadLanguage(
            @NotNull String sourceUrl,
            @NotNull String languageCode
    ) {
        long time = System.currentTimeMillis();
        LanguageFile languageFile = new LanguageFile(sourceUrl, languageCode);

        Locale locale = Locale.US;
        languageFile.translations.entrySet().forEach(
                entry -> registry.register(entry.getKey(), locale, new MessageFormat(entry.getValue().getAsString()))
        );
        GlobalTranslator.translator().addSource(registry);

        ChatUtils.sendFine("Loaded language file: " + languageFile.file.getName() + " in " + (System.currentTimeMillis() - time) + "ms");
    }

    /**
     * Unloads all languages registered in {@link #registry} from {@link GlobalTranslator}
     */
    public static void unloadLanguage() {
        GlobalTranslator.translator().removeSource(registry);
        registry = TranslationRegistry.create(Key.key("ms"));
    }

    /**
     * Reloads language file
     *
     * @see #unloadLanguage()
     * @see #loadLanguage(String, String)
     */
    public static void reloadLanguage() {
        Config config = MSCore.getConfiguration();

        unloadLanguage();
        loadLanguage(config.languageUrl, config.languageCode);
    }

    /**
     * Renders translated component to {@link Component}
     * <br>
     * Uses {@link GlobalTranslator}
     *
     * @param key Translation key
     * @return Translated component
     */
    public static @NotNull Component renderTranslationComponent(@NotNull String key) {
        return GlobalTranslator.render(Component.translatable(key), Locale.US);
    }

    /**
     * Renders translated component to legacy string
     *
     * @param key Translation key
     * @return Translated string
     * @see #renderTranslationComponent(String)
     */
    public static @NotNull String renderTranslation(@NotNull String key) {
        return ChatUtils.serializeLegacyComponent(renderTranslationComponent(key));
    }

    /**
     * Loads language file from
     *
     * @return Language file
     */
    private @NotNull File loadFile() {
        File langFolder = new File("config/minersstudios/msTranslations");

        if (!langFolder.exists() && !langFolder.mkdirs()) {
            throw new RuntimeException("Failed to create language folder");
        }

        File langFile = new File(langFolder, this.languageCode + ".json");

        if (!langFile.exists()) {
            String url = this.sourceUrl + this.languageCode + ".json";

            try {
                URL fileUrl = new URL(url);

                try (
                        InputStream in = fileUrl.openStream();
                        OutputStream out = new FileOutputStream(langFile)
                ) {
                    in.transferTo(out);
                }
            } catch (IOException e) {
                ChatUtils.sendError("Failed to download language file: " + url);
            }
        }

        return langFile;
    }

    /**
     * Loads language file as JsonObject from file path specified in {@link #file}
     *
     * @return JsonObject of language file
     * @throws JsonSyntaxException If language file is corrupted
     */
    private @NotNull JsonObject loadTranslations() throws JsonSyntaxException {
        try {
            Path path = Path.of(this.file.getAbsolutePath());
            String content = Files.readString(path);
            JsonElement element = JsonParser.parseString(content);

            return element.getAsJsonObject();
        } catch (IOException | JsonSyntaxException e) {
            ChatUtils.sendError("Failed to load corrupted language file: " + this.languageCode + ".json");
            ChatUtils.sendError("Creating backup file and trying to load language file again");

            this.createBackupFile();
            this.file = this.loadFile();

            return this.loadTranslations();
        }
    }

    /**
     * Creates a backup file of the corrupted language file
     * <br>
     * The backup file will be named as the original file with ".OLD" appended to the end
     * <br>
     * If the backup file already exists, it will be replaced
     *
     * @throws RuntimeException If failed to create backup file
     */
    private void createBackupFile() throws RuntimeException {
        String backupFileName = this.file.getName() + ".OLD";
        File backupFile = new File(this.file.getParent(), backupFileName);
        Path filePath = Path.of(this.file.getAbsolutePath());
        Path backupFilePath = Path.of(backupFile.getAbsolutePath());

        try {
            Files.move(filePath, backupFilePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create" + backupFileName + " backup file", e);
        }
    }
}

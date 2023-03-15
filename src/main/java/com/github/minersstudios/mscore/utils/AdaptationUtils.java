package com.github.minersstudios.mscore.utils;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R2.CraftServer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public final class AdaptationUtils {

	@Contract(value = " -> fail")
	private AdaptationUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static @Nullable Player loadPlayer(@NotNull OfflinePlayer offline) {
		if (!offline.hasPlayedBefore()) return null;
		GameProfile profile = new GameProfile(offline.getUniqueId(),
				offline.getName() != null
				? offline.getName()
				: offline.getUniqueId().toString()
		);
		MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
		ServerLevel worldServer = server.getLevel(Level.OVERWORLD);

		if (worldServer == null) return null;

		Player online = new ServerPlayer(server, worldServer, profile).getBukkitEntity();
		online.loadData();
		return online;
	}

	public static class RegionFile {
		public static final int INT_SIZE = 4;
		public static final int REGION_WIDTH_IN_CHUNKS = 32;
		public static final int CHUNKS_PER_REGION = REGION_WIDTH_IN_CHUNKS * REGION_WIDTH_IN_CHUNKS;

		public static final int UNDEFINED_COMPRESSION_TYPE = 0;
		public static final int COMPRESSION_TYPE_GZIP = 1;
		public static final int COMPRESSION_TYPE_ZLIB = 2;

		private final int[] offset = new int[CHUNKS_PER_REGION];
		private final int[] sectors = new int[CHUNKS_PER_REGION];
		private final int[] length = new int[CHUNKS_PER_REGION];
		private final byte[] compressionType = new byte[CHUNKS_PER_REGION];
		private final byte[][] compressedData = new byte[CHUNKS_PER_REGION][];
		private final CompoundTag[] chunkRoot = new CompoundTag[CHUNKS_PER_REGION];
		private final File fileObject;
		private final RandomAccessFile file;

		public RegionFile(Path fileName) throws FileNotFoundException {
			for (int i = 0; i < CHUNKS_PER_REGION; i++) {
				this.offset[i] = 0;
				this.sectors[i] = 0;
				this.length[i] = 0;
				this.compressionType[i] = UNDEFINED_COMPRESSION_TYPE;
				this.compressedData[i] = null;
				this.chunkRoot[i] = null;
			}

			this.fileObject = new File(fileName.toString());
			if (!this.fileObject.exists()) throw new FileNotFoundException("File not found");
			this.file = new RandomAccessFile(this.fileObject, "r");
		}

		public @NotNull Multimap<Map.Entry<Integer, Integer>, UUID> getCustomDecors() throws Exception {
			Multimap<Map.Entry<Integer, Integer>, UUID> chunks = MultimapBuilder.hashKeys().hashSetValues().build();
			if (!this.initRegion()) return chunks;

			for (int i = 0; i < CHUNKS_PER_REGION; i++) {
				if (this.offset[i] != 0) {
					this.initChunk(i);

					int[] coords = this.chunkRoot[i].getIntArray("Position");
					this.chunkRoot[i].getList("Entities", Tag.TAG_COMPOUND)
					.forEach(tag -> {
						String str = tag.getAsString();
						if (
								(str.contains("id:\"minecraft:armor_stand\"")
								|| str.contains("id:\"minecraft:item_frame\"")
								|| str.contains("id:\"minecraft:item\""))
								&& str.contains("CustomModelData")
						) {
							Pattern pattern = Pattern.compile("UUID:\\[I;(-?\\d+),(-?\\d+),(-?\\d+),(-?\\d+)]");
							Matcher matcher = pattern.matcher(str);
							if (matcher.find()) {
								UUID uuid = UUIDUtil.uuidFromIntArray(new int[]{
										Integer.parseInt(matcher.group(1)),
										Integer.parseInt(matcher.group(2)),
										Integer.parseInt(matcher.group(3)),
										Integer.parseInt(matcher.group(4))
								});
								chunks.put(
										new AbstractMap.SimpleEntry<>(coords[0], coords[1]),
										uuid
								);
							}
						}
					});
				}
			}

			this.file.close();
			return chunks;
		}

		public @NotNull Multimap<Map.Entry<Integer, Integer>, Location> getTileEntitiesWithCustoms(@NotNull World world) throws Exception {
			Multimap<Map.Entry<Integer, Integer>, Location> chunks = MultimapBuilder.hashKeys().hashSetValues().build();
			if (!this.initRegion()) return chunks;

			for (int i = 0; i < CHUNKS_PER_REGION; i++) {
				if (this.offset[i] != 0) {
					this.initChunk(i);

					int[] coords = new int[]{this.chunkRoot[i].getInt("xPos"), this.chunkRoot[i].getInt("zPos")};
					this.chunkRoot[i].getList("block_entities", Tag.TAG_COMPOUND)
					.forEach(tag -> {
						try {
							CompoundTag compoundTag = NbtUtils.snbtToStructure(tag.getAsString());
							ListTag items = compoundTag.getList("Items", Tag.TAG_COMPOUND);
							if (!items.isEmpty()) {
								items.forEach(item -> {
									if (item.getAsString().contains("CustomModelData")) {
										chunks.put(
												new AbstractMap.SimpleEntry<>(coords[0], coords[1]),
												new Location(
														world,
														compoundTag.getInt("x"),
														compoundTag.getInt("y"),
														compoundTag.getInt("z")
												)
										);
									}
								});
							}
						} catch (CommandSyntaxException e) {
							throw new RuntimeException(e);
						}
					});
				}
			}

			this.file.close();
			return chunks;
		}

		private boolean initRegion() throws IOException {
			if (this.file.length() < 2 * INT_SIZE * CHUNKS_PER_REGION) {
				this.file.close();
				Bukkit.getLogger().warning("Invalid file structure (file is not big enough to contain a valid region file header) : " + this.fileObject.getAbsolutePath());
				return false;
			}

			for (int i = 0; i < CHUNKS_PER_REGION; i++) {
				int j = this.file.readInt();
				this.offset[i] = (j & 0xFFFFFF00) >> 8;
				this.sectors[i] = (j & 0x000000FF);
			}
			return true;
		}

		private void initChunk(int i) throws Exception {
			this.file.seek((long) this.offset[i] * INT_SIZE * CHUNKS_PER_REGION);

			this.length[i] = this.file.readInt();
			this.compressionType[i] = this.file.readByte();

			if (this.length[i] == 0) {
				this.file.close();
				throw new Exception("Empty chunk");
			}

			if (this.length[i] > this.sectors[i] * INT_SIZE * CHUNKS_PER_REGION) {
				this.file.close();
				throw new Exception("Invalid chunk length");
			}

			if (
					!(this.compressionType[i] == COMPRESSION_TYPE_GZIP
					|| this.compressionType[i] == COMPRESSION_TYPE_ZLIB)
			) {
				this.file.close();
				throw new Exception("Unknown compression type");
			}

			this.compressedData[i] = new byte[length[i] - 1];
			this.file.read(this.compressedData[i]);

			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.compressedData[i]);
			if (this.compressionType[i] == COMPRESSION_TYPE_ZLIB) {
				this.read(i, new InflaterInputStream(byteArrayInputStream));
			} else if (this.compressionType[i] == COMPRESSION_TYPE_GZIP) {
				this.read(i, new GZIPInputStream(byteArrayInputStream));
			}
		}

		private void read(int i, @NotNull InflaterInputStream inputStream) throws IOException {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int nRead;
			byte[] data = new byte[1024];

			while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}

			buffer.flush();

			this.chunkRoot[i] = NbtIo.read(new DataInputStream(new ByteArrayInputStream(buffer.toByteArray())));
		}
	}
}

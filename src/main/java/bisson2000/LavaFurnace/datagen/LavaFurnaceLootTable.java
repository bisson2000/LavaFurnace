package bisson2000.LavaFurnace.datagen;

import bisson2000.LavaFurnace.blocks.LavaFurnaceBlock;
import bisson2000.LavaFurnace.init.BlocksRegistry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.data.LootTableProvider;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.BlockStateProperty;
import net.minecraft.loot.functions.CopyBlockState;
import net.minecraft.loot.functions.CopyName;
import net.minecraft.loot.functions.CopyNbt;
import net.minecraft.loot.functions.SetContents;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class LavaFurnaceLootTable extends LootTableProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    protected final Map<ResourceLocation, LootTable.Builder> lootTables = new HashMap<>();
    private final DataGenerator generator;

    public LavaFurnaceLootTable(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
        this.generator = dataGeneratorIn;
    }

    @Override
    public String getName() {
        return "Lava Furnace LootTable";
    }

    @Override
    public void act(DirectoryCache cache) {
        addTables();
        Map<ResourceLocation, LootTable> tables = new HashMap<>();
        for (Map.Entry<ResourceLocation, LootTable.Builder> entry : lootTables.entrySet()) {
            tables.put(entry.getKey(), entry.getValue().setParameterSet(LootParameterSets.BLOCK).build());
        }
        writeTables(cache, tables);
    }

    private void addTables() {
        lootTables.put(createStandardRL(BlocksRegistry.LAVA_FURNACE.get()), createStandardTable("lava_furnace", BlocksRegistry.LAVA_FURNACE.get()));
        lootTables.put(createNBTRL(BlocksRegistry.LAVA_FURNACE.get()), createNBTTable("lava_furnace_nbt", (LavaFurnaceBlock) BlocksRegistry.LAVA_FURNACE.get()));
    }

    public static ResourceLocation createStandardRL(Block block) {
        String dataFolder = block.getRegistryName().getNamespace();
        String path = "blocks/" + block.getRegistryName().getPath();
        return new ResourceLocation(dataFolder, path);
    }

    public static ResourceLocation createNBTRL(Block block) {
        String dataFolder = block.getRegistryName().getNamespace();
        String path = "blocks/" + block.getRegistryName().getPath() + "_nbt";
        return new ResourceLocation(dataFolder, path);
    }

    private LootTable.Builder createStandardTable(String name, Block block) {
        LootPool.Builder builder = LootPool.builder()
                .name(name)
                .rolls(ConstantRange.of(1))
                .addEntry(ItemLootEntry.builder(block));
        return LootTable.builder().addLootPool(builder);
    }

    private LootTable.Builder createNBTTable(String name, LavaFurnaceBlock block) {
        LootPool.Builder builder = LootPool.builder()
                .name(name)
                .rolls(ConstantRange.of(1))
                .addEntry(ItemLootEntry.builder(block)
                        //Copy Name
                        .acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY))
                        //Copy entity fluid tank
                        .acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY)
                                .acceptCondition(BlockStateProperty.builder(block).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withBoolProp(block.IS_EMPTY, false)))
                                //.addOperation("inv", "BlockEntityTag.inv", CopyNbt.Action.REPLACE)
                                .addOperation("tank", "BlockEntityTag.tank", CopyNbt.Action.REPLACE)
                        )
                        //Copy entity contents
                        .acceptFunction(SetContents.builderIn()
                                .acceptCondition(BlockStateProperty.builder(block).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withBoolProp(block.IS_EMPTY, false)))
                                .addLootEntry(DynamicLootEntry.func_216162_a(new ResourceLocation("minecraft", "contents")))
                        )
                        //Copy blockStates
                        .acceptFunction(CopyBlockState.func_227545_a_(block).func_227552_a_(block.IS_EMPTY).func_227552_a_(block.HAS_HOT_FLUID)
                                .acceptCondition(BlockStateProperty.builder(block).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withBoolProp(block.IS_EMPTY, false)))
                        )
                );
        return LootTable.builder().addLootPool(builder);
    }

    private void writeTables(DirectoryCache cache, Map<ResourceLocation, LootTable> tables) {
        Path outputFolder = this.generator.getOutputFolder();
        tables.forEach((key, lootTable) -> {
            Path path = outputFolder.resolve("data/" + key.getNamespace() + "/loot_tables/" + key.getPath() + ".json");
            try {
                IDataProvider.save(GSON, cache, LootTableManager.toJson(lootTable), path);
            } catch (IOException e) {
                LOGGER.error("Couldn't write loot table {}", path, e);
            }
        });
    }

}

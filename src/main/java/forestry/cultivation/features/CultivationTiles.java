package forestry.cultivation.features;

import forestry.api.modules.ForestryModuleIds;
import forestry.cultivation.blocks.BlockTypePlanter;
import forestry.cultivation.tiles.TileArboretum;
import forestry.cultivation.tiles.TileBog;
import forestry.cultivation.tiles.TileFarmCrops;
import forestry.cultivation.tiles.TileFarmEnder;
import forestry.cultivation.tiles.TileFarmGourd;
import forestry.cultivation.tiles.TileFarmMushroom;
import forestry.cultivation.tiles.TileFarmNether;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.FeatureTileType;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

@FeatureProvider
public class CultivationTiles {
	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ForestryModuleIds.CULTIVATION);

	public static final FeatureTileType<TileArboretum> ARBORETUM = REGISTRY.tile(TileArboretum::new, "arboretum", () -> CultivationBlocks.PLANTER.getRowBlocks(BlockTypePlanter.ARBORETUM));
	public static final FeatureTileType<TileBog> BOG = REGISTRY.tile(TileBog::new, "bog", () -> CultivationBlocks.PLANTER.getRowBlocks(BlockTypePlanter.PEAT_POG));
	public static final FeatureTileType<TileFarmCrops> CROPS = REGISTRY.tile(TileFarmCrops::new, "crops", () -> CultivationBlocks.PLANTER.getRowBlocks(BlockTypePlanter.FARM_CROPS));
	public static final FeatureTileType<TileFarmEnder> ENDER = REGISTRY.tile(TileFarmEnder::new, "ender", () -> CultivationBlocks.PLANTER.getRowBlocks(BlockTypePlanter.FARM_ENDER));
	public static final FeatureTileType<TileFarmGourd> GOURD = REGISTRY.tile(TileFarmGourd::new, "gourd", () -> CultivationBlocks.PLANTER.getRowBlocks(BlockTypePlanter.FARM_GOURD));
	public static final FeatureTileType<TileFarmMushroom> MUSHROOM = REGISTRY.tile(TileFarmMushroom::new, "mushroom", () -> CultivationBlocks.PLANTER.getRowBlocks(BlockTypePlanter.FARM_MUSHROOM));
	public static final FeatureTileType<TileFarmNether> NETHER = REGISTRY.tile(TileFarmNether::new, "nether", () -> CultivationBlocks.PLANTER.getRowBlocks(BlockTypePlanter.FARM_NETHER));
}

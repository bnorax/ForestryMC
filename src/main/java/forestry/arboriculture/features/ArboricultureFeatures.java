package forestry.arboriculture.features;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import forestry.arboriculture.worldgen.TreeDecorator;
import forestry.core.config.Constants;
import net.minecraftforge.registries.RegisterEvent;

public class ArboricultureFeatures {
	public static final ResourceLocation TREE_DECORATOR_ID = new ResourceLocation(Constants.MOD_ID, "tree_decorator");
	public static final Feature<NoneFeatureConfiguration> TREE_DECORATOR = new TreeDecorator();
	public static final Holder<ConfiguredFeature<NoneFeatureConfiguration, ?>> TREE_DECORATOR_CONF = FeatureUtils.register(TREE_DECORATOR_ID.toString(), TREE_DECORATOR);

	public static void registerFeatures(RegisterEvent event) {
		event.register(Registry.FEATURE_REGISTRY, TREE_DECORATOR_ID, () -> TREE_DECORATOR);
		event.register(Registry.CONFIGURED_FEATURE_REGISTRY, TREE_DECORATOR_ID, TREE_DECORATOR_CONF::value);
	}

	public static void onBiomeLoad(BiomeLoadingEvent event) {
		Holder<PlacedFeature> placed = PlacementUtils.register(TREE_DECORATOR_ID.toString(), TREE_DECORATOR_CONF);
		event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, placed);
	}
}

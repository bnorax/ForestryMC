/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.apiculture.hives;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import forestry.api.ForestryTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import forestry.api.apiculture.ForestryBeeSpecies;
import forestry.api.apiculture.genetics.IBeeSpecies;
import forestry.api.apiculture.hives.IHiveDefinition;
import forestry.api.apiculture.hives.IHiveGen;
import forestry.api.core.HumidityType;
import forestry.api.core.TemperatureType;
import forestry.api.core.ToleranceType;
import forestry.api.genetics.ClimateHelper;
import forestry.api.genetics.alleles.BeeChromosomes;
import forestry.apiculture.blocks.BlockHiveType;
import forestry.apiculture.features.ApicultureBlocks;
import forestry.core.utils.SpeciesUtil;
import net.minecraftforge.common.Tags;

// todo this should be data driven
public enum HiveDefinition implements IHiveDefinition {
	FOREST(ApicultureBlocks.BEEHIVE.get(BlockHiveType.FOREST).defaultState(), 6.0f, ForestryBeeSpecies.FOREST, HiveGenTree.INSTANCE) {
		@Override
		public void postGen(WorldGenLevel world, RandomSource rand, BlockPos pos) {
			postGenFlowers(world, rand, pos, flowerStates);
		}

		@Override
		public boolean isGoodBiome(Holder<Biome> biome) {
			//TODO: Forest bees now have slight cold tolerance. This tag restricts them to the warmer side. Should they require deciduous trees? investigate its not excluding the wrong biomes
			return super.isGoodBiome(biome) && !biome.is(Tags.Biomes.IS_SNOWY);
		}
	},
	MEADOWS(ApicultureBlocks.BEEHIVE.get(BlockHiveType.MEADOWS).defaultState(), 1.0f, ForestryBeeSpecies.MEADOWS, new HiveGenGroundTag(BlockTags.DIRT)) {
		@Override
		public void postGen(WorldGenLevel world, RandomSource rand, BlockPos pos) {
			postGenFlowers(world, rand, pos, flowerStates);
		}

		@Override
		public boolean isGoodBiome(Holder<Biome> biome) {
			//TODO: find a good way to exclude meadows bee from forested areas. This tag seems to contain temperate forests. Sometimes they still generate in plain old forests for some reason but are rarer
			return super.isGoodBiome(biome) && !biome.is(BiomeTags.IS_FOREST);
		}
	},
	DESERT(ApicultureBlocks.BEEHIVE.get(BlockHiveType.DESERT).defaultState(), 1.0f, ForestryBeeSpecies.MODEST, new HiveGenGroundTag(ForestryTags.Blocks.MODEST_BEE_GROUND)) {
		@Override
		public void postGen(WorldGenLevel world, RandomSource rand, BlockPos pos) {
			postGenFlowers(world, rand, pos, cactusStates);
		}
	},
	JUNGLE(ApicultureBlocks.BEEHIVE.get(BlockHiveType.JUNGLE).defaultState(), 6.0f, ForestryBeeSpecies.TROPICAL, HiveGenTree.INSTANCE),
	END(ApicultureBlocks.BEEHIVE.get(BlockHiveType.END).defaultState(), 2.0f, ForestryBeeSpecies.ENDED, new HiveGenGround(Blocks.END_STONE, Blocks.END_STONE_BRICKS)) {
		@Override
		public boolean isGoodBiome(Holder<Biome> biome) {
			return biome.is(BiomeTags.IS_END);
		}
	},
	SNOW(ApicultureBlocks.BEEHIVE.get(BlockHiveType.SNOW).defaultState(), 2.0f, ForestryBeeSpecies.WINTRY, new HiveGenGroundTag(ForestryTags.Blocks.WINTRY_BEE_GROUND)) {
		@Override
		public void postGen(WorldGenLevel world, RandomSource rand, BlockPos pos) {
			BlockPos posAbove = pos.above();
			if (world.isEmptyBlock(posAbove)) {
				world.setBlock(posAbove, Blocks.SNOW.defaultBlockState(), Block.UPDATE_CLIENTS);
			}

			postGenFlowers(world, rand, pos, flowerStates);
		}
	},
	SWAMP(ApicultureBlocks.BEEHIVE.get(BlockHiveType.SWAMP).defaultState(), 2.0f, ForestryBeeSpecies.MARSHY, new HiveGenGroundTag(BlockTags.DIRT)) {
		@Override
		public void postGen(WorldGenLevel world, RandomSource rand, BlockPos pos) {
			postGenFlowers(world, rand, pos, mushroomStates);
		}

		@Override
		public boolean isGoodBiome(Holder<Biome> biome) {
			//No swamp bees bellow freezing
			return super.isGoodBiome(biome) && !biome.is(Tags.Biomes.IS_SNOWY);
		}
	},
	SAVANNA(ApicultureBlocks.BEEHIVE.get(BlockHiveType.SAVANNA).defaultState(), 1.0f, ForestryBeeSpecies.SAVANNA, new HiveGenGroundTag(BlockTags.DIRT)){
		@Override
		public void postGen(WorldGenLevel world, RandomSource rand, BlockPos pos) {
			//TODO: generate pumpkins in dry biomes and melons in normal ones
			//postGenFlowers(world,rand,pos,flowerStates);
		}
	};

	private static final IHiveGen FLOWER_GROUND = new HiveGenGround(Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.SNOW, Blocks.SAND, Blocks.SANDSTONE);
	private static final List<BlockState> flowerStates = new ArrayList<>();
	private static final List<BlockState> mushroomStates = new ArrayList<>();
	private static final List<BlockState> cactusStates = Collections.singletonList(Blocks.CACTUS.defaultBlockState());

	static {
		flowerStates.addAll(Blocks.POPPY.getStateDefinition().getPossibleStates());
		flowerStates.addAll(Blocks.DANDELION.getStateDefinition().getPossibleStates());
		mushroomStates.add(Blocks.RED_MUSHROOM.defaultBlockState());
		mushroomStates.add(Blocks.BROWN_MUSHROOM.defaultBlockState());
	}

	private final BlockState blockState;
	private final float genChance;
	private final ResourceLocation speciesId;
	private final IHiveGen hiveGen;

	HiveDefinition(BlockState hiveState, float genChance, ResourceLocation beeTemplate, IHiveGen hiveGen) {
		this.blockState = hiveState;
		this.genChance = genChance;
		this.speciesId = beeTemplate;
		this.hiveGen = hiveGen;
	}

	@Override
	public IHiveGen getHiveGen() {
		return hiveGen;
	}

	@Override
	public BlockState getBlockState() {
		return blockState;
	}

	@Override
	public boolean isGoodBiome(Holder<Biome> biome) {
		return !biome.is(BiomeTags.IS_NETHER);
	}

	@Override
	public boolean isGoodHumidity(HumidityType humidity) {
		IBeeSpecies species = SpeciesUtil.getBeeSpecies(this.speciesId);
		HumidityType idealHumidity = species.getHumidity();
		ToleranceType humidityTolerance = species.getDefaultGenome().getActiveValue(BeeChromosomes.HUMIDITY_TOLERANCE);
		return ClimateHelper.isWithinLimits(humidity, idealHumidity, humidityTolerance);
	}

	@Override
	public boolean isGoodTemperature(TemperatureType temperature) {
		IBeeSpecies species = SpeciesUtil.getBeeSpecies(this.speciesId);
		TemperatureType idealTemperature = species.getTemperature();
		ToleranceType temperatureTolerance = species.getDefaultGenome().getActiveValue(BeeChromosomes.TEMPERATURE_TOLERANCE);
		return ClimateHelper.isWithinLimits(temperature, idealTemperature, temperatureTolerance);
	}

	@Override
	public float getGenChance() {
		return genChance;
	}

	@Override
	public void postGen(WorldGenLevel world, RandomSource rand, BlockPos pos) {
	}

	protected static void postGenFlowers(WorldGenLevel world, RandomSource rand, BlockPos hivePos, List<BlockState> flowerStates) {
		int plantedCount = 0;
		for (int i = 0; i < 10; i++) {
			int xOffset = rand.nextInt(8) - 4;
			int zOffset = rand.nextInt(8) - 4;
			BlockPos blockPos = hivePos.offset(xOffset, 0, zOffset);
			if ((xOffset == 0 && zOffset == 0) || !world.hasChunkAt(blockPos)) {
				continue;
			}

			blockPos = FLOWER_GROUND.getPosForHive(world, blockPos.getX(), blockPos.getZ());
			if (blockPos == null) {
				continue;
			}

			BlockState state = flowerStates.get(rand.nextInt(flowerStates.size()));
			Block block = state.getBlock();
			if (!block.defaultBlockState().canSurvive(world, blockPos)) {
				continue;
			}

			world.setBlock(blockPos, state, Block.UPDATE_CLIENTS);
			plantedCount++;

			if (plantedCount >= 3) {
				break;
			}
		}
	}
}

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
package forestry.lepidopterology.render;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;

import net.minecraftforge.client.model.SeparateTransformsModel;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import forestry.api.ForestryConstants;
import forestry.api.client.IForestryClientApi;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.alleles.ButterflyChromosomes;
import forestry.api.genetics.capability.IIndividualHandlerItem;
import forestry.api.lepidopterology.genetics.IButterflySpecies;
import forestry.core.models.AbstractBakedModel;
import forestry.core.models.TRSRBakedModel;
import forestry.core.utils.ResourceUtil;
import forestry.core.utils.SpeciesUtil;

import it.unimi.dsi.fastutil.floats.FloatObjectPair;

public class ButterflyItemModel extends AbstractBakedModel {
	private final IdentityHashMap<IButterflySpecies, BakedModel> subModels;
	private final Cache<FloatObjectPair<String>, BakedModel> cache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build();

	public ButterflyItemModel(IdentityHashMap<IButterflySpecies, BakedModel> subModels) {
		this.subModels = subModels;
	}

	@Override
	protected ItemOverrides createOverrides() {
		return new OverrideList();
	}

	private class OverrideList extends ItemOverrides {
		@Override
		public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel worldIn, @Nullable LivingEntity entityIn, int p_173469_) {
			IIndividual individual = Objects.requireNonNull(IIndividualHandlerItem.getIndividual(stack));
			IGenome genome = individual.getGenome();
			IButterflySpecies species = genome.getActiveValue(ButterflyChromosomes.SPECIES);
			float size = genome.getActiveValue(ButterflyChromosomes.SIZE);
			// should this be using the path? or the ID?
			try {
				return cache.get(FloatObjectPair.of(size, species.id().getPath()), () -> {
					// todo include scale, otherwise having the float in the pair is useless
					return new SeparateTransformsModel.Baked(false, true, false, ResourceUtil.getMissingTexture(), ItemOverrides.EMPTY, new TRSRBakedModel(subModels.get(species), 0, 0, 0, size), ImmutableMap.of());
				});
			} catch (ExecutionException e) {
				throw new RuntimeException(e);
			}
		}

		private ImmutableMap<ItemTransforms.TransformType, Transformation> getTransformations(float size) {
			float scale = 1F / 16F;
			float sSize = size * 1.15F;
			Vector3f scaledSize = new Vector3f(sSize, sSize, sSize);
			ImmutableMap.Builder<ItemTransforms.TransformType, Transformation> builder = ImmutableMap.builderWithExpectedSize(5);
			builder.put(ItemTransforms.TransformType.FIXED,
					new Transformation(new Vector3f(scale * 0.5F, scale - (size / 0.75F) * scale, scale * 1.25F), null, scaledSize, null));
			builder.put(ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND,
					new Transformation(new Vector3f(0, -scale * 4.75F, 0), null, scaledSize, null));
			builder.put(ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND,
					new Transformation(new Vector3f(0, -scale * 4.75F, 0), null, scaledSize, null));
			builder.put(ItemTransforms.TransformType.GUI,
					new Transformation(new Vector3f(0, -scale, 0), new Quaternion(new Vector3f(1, 0, 0), 90F, true), scaledSize, null));
			builder.put(ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND,
					new Transformation(new Vector3f(0, 0, 0), null, scaledSize, null));
			return builder.build();
		}
	}

	public record Geometry(
			IdentityHashMap<IButterflySpecies, ResourceLocation> subModels) implements IUnbakedGeometry<Geometry> {
		@Override
		public BakedModel bake(IGeometryBakingContext context, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
			UnbakedModel modelButterfly = bakery.getModel(ForestryConstants.forestry("item/butterfly"));

			if (!(modelButterfly instanceof BlockModel modelBlock)) {
				return null;
			}
			ResourceLocation parentLocation = modelBlock.getParentLocation();
			List<BlockElement> elements = modelBlock.getElements();
			IdentityHashMap<IButterflySpecies, BakedModel> subModelBuilder = new IdentityHashMap<>();

			for (Map.Entry<IButterflySpecies, ResourceLocation> subModel : this.subModels.entrySet()) {
				IButterflySpecies identifier = subModel.getKey();
				ResourceLocation texture = subModel.getValue();

				BlockModel model = new BlockModel(parentLocation, elements, ImmutableMap.of("butterfly", Either.left(new Material(InventoryMenu.BLOCK_ATLAS, texture))), modelBlock.hasAmbientOcclusion, modelBlock.getGuiLight(), modelBlock.getTransforms(), modelBlock.getOverrides());
				ResourceLocation location = ForestryConstants.forestry("item/butterfly");
				ModelState transform = ResourceUtil.loadTransform(ForestryConstants.forestry("item/butterfly"));
				subModelBuilder.put(identifier, model.bake(bakery, model, spriteGetter, transform, location, true));
			}
			return new ButterflyItemModel(subModelBuilder);
		}

		@Override
		public Collection<Material> getMaterials(IGeometryBakingContext context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
			return this.subModels.values().stream().map(location -> new Material(InventoryMenu.BLOCK_ATLAS, location)).collect(Collectors.toSet());
		}
	}

	public static class Loader implements IGeometryLoader<ButterflyItemModel.Geometry> {
		@Override
		public ButterflyItemModel.Geometry read(JsonObject modelContents, JsonDeserializationContext context) throws JsonParseException {
			IdentityHashMap<IButterflySpecies, ResourceLocation> subModels = new IdentityHashMap<>();

			for (IButterflySpecies species : SpeciesUtil.getAllButterflySpecies()) {
				Pair<ResourceLocation, ResourceLocation> pair = IForestryClientApi.INSTANCE.getButterflyManager().getTextures(species);
				subModels.put(species, pair.getFirst());
			}
			return new ButterflyItemModel.Geometry(subModels);
		}
	}
}

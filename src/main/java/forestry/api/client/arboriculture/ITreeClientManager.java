package forestry.api.client.arboriculture;

import java.util.Collection;

import net.minecraft.resources.ResourceLocation;

import com.mojang.datafixers.util.Pair;

import forestry.api.arboriculture.ITreeSpecies;

import org.jetbrains.annotations.Nullable;

/**
 * Tracks client-only data for tree species.
 */
public interface ITreeClientManager {
	/**
	 * @return The leaf sprite for the given species.
	 */
	ILeafSprite getLeafSprite(@Nullable ITreeSpecies species);

	/**
	 * @return A collection of every leaf sprite that was registered.
	 */
	Collection<ILeafSprite> getAllLeafSprites();

	/**
	 * @return The leaf tint for the given species, or {@link ILeafTint#DEFAULT} if none was found.
	 */
	ILeafTint getTint(@Nullable ITreeSpecies species);

	/**
	 * @return A pair containing locations of the block and item models for this species, in that order.
	 */
	Pair<ResourceLocation, ResourceLocation> getSaplingModels(ITreeSpecies species);

	Collection<Pair<ResourceLocation, ResourceLocation>> getAllSaplingModels();
}

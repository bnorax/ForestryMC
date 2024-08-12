package forestry.api.genetics;

import javax.annotation.Nullable;
import java.util.Optional;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import forestry.api.ForestryConstants;

/**
 * An individual belongs to a certain species and has a genome and life stage.
 * It can be mated with another individual to produce offspring.
 */
public interface IIndividual {
	ResourceLocation CAPABILITY_ID = new ResourceLocation(ForestryConstants.MOD_ID, "individual") {
		// Used for AttachCapabilitiesEvent. Hashcode is cached for performance.
		private final int hashCode = super.hashCode();

		@Override
		public int hashCode() {
			return this.hashCode;
		}
	};

	/**
	 * @return The genome of this individual.
	 */
	IGenome getGenome();

	/**
	 * @return The species type of this individual.
	 */
	ISpeciesType<?, ?> getType();

	/**
	 * @return The active species of this individual. Prefer this method over getting it from {@link #getGenome()}.
	 */
	ISpecies<?> getSpecies();

	/**
	 * @return The inactive species of the individual. Prefer this method over getting it from {@link #getGenome()}.
	 */
	ISpecies<?> getInactiveSpecies();

	void setMate(@Nullable IGenome mate);

	/**
	 * @return The genome of this individual's mate, or {@code null} if this individual has no mate.
	 */
	@Nullable
	IGenome getMate();

	/**
	 * An optional getter for {@link #getMate}. Used only for Codec purposes, prefer {@link #getMate} when possible.
	 * @return An optional containing this individual's mate genome, or empty if this individual has no mate.
	 */
	Optional<IGenome> getMateOptional();

	/**
	 * @return {@code true} if this individual has been analyzed and a summary of its genome should be displayed in its tooltip.
	 */
	boolean isAnalyzed();

	/**
	 * @return Sets this species as analyzed. Details about the specimen can be viewed in its tooltip and analyzer pages.
	 */
	boolean analyze();

	ItemStack copyWithStage(ILifeStage stage);

	/**
	 * Writes the state of this individual, including genome, to an item.
	 */
	void saveToStack(ItemStack stack);

	default boolean hasGlint() {
		return getSpecies().hasGlint();
	}

	default boolean isSecret() {
		return getSpecies().isSecret();
	}

	/**
	 * Copies this individual and all of its properties EXCEPT FOR ITS MATE.
	 * Override this method in subclasses to make sure all information is copied.
	 *
	 * @return An exact copy of this individual WITHOUT A MATE.
	 */
	IIndividual copy();

	default <I extends IIndividual> I cast() {
		return (I) this;
	}
}
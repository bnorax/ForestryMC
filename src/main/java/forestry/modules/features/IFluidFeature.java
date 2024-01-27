package forestry.modules.features;

import javax.annotation.Nullable;
import java.util.function.Supplier;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.item.BlockItem;

import net.minecraftforge.fluids.FluidStack;

import forestry.core.fluids.BlockForestryFluid;
import net.minecraftforge.registries.RegisterEvent;

public interface IFluidFeature extends IModFeature {

	FeatureBlock<BlockForestryFluid, BlockItem> fluidBlock();

	default Fluid apply(Fluid fluid) {
		return fluid;
	}

	void setFluid(FlowingFluid fluid);

	void setFlowing(FlowingFluid flowing);

	Supplier<FlowingFluid> getFluidConstructor(boolean flowing);

	@Nullable
	FlowingFluid getFluid();

	@Nullable
	FlowingFluid getFlowing();

	FluidProperties properties();

	boolean hasFluid();

	boolean hasFlowing();

	default FlowingFluid fluid() {
		FlowingFluid fluid = getFluid();
		if (fluid == null) {
			throw new IllegalStateException("Called feature getter method before content creation.");
		}
		return fluid;
	}

	default FlowingFluid flowing() {
		FlowingFluid flowing = getFlowing();
		if (flowing == null) {
			throw new IllegalStateException("Called feature getter method before content creation.");
		}
		return flowing;
	}

	default FluidStack fluidStack(int amount) {
		if (hasFluid()) {
			return new FluidStack(fluid(), amount);
		}
		return FluidStack.EMPTY;
	}

	default FluidStack fluidStack() {
		return fluidStack(FluidAttributes.BUCKET_VOLUME);
	}

	@Override
	default void create() {
        setFluid(getFluidConstructor(false).get());
		setFlowing(getFluidConstructor(true).get());
	}

	@Override
	@SuppressWarnings("unchecked")
	default void register(RegisterEvent event) {
		event.register(Registry.FLUID_REGISTRY, helper -> {
			helper.register(new ResourceLocation(getModId(), getIdentifier()), fluid());
			helper.register(new ResourceLocation(getModId(), getIdentifier() + "_flowing"), flowing());
		});
	}
}

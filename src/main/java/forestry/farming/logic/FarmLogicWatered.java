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
package forestry.farming.logic;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import forestry.api.farming.IFarmHousing;
import forestry.core.fluids.Fluids;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.StackUtils;
import forestry.core.utils.Utils;
import forestry.core.utils.Vect;

public abstract class FarmLogicWatered extends FarmLogic {

	protected ItemStack ground;
	private final ItemStack[] resource;

	private static final FluidStack STACK_WATER = Fluids.WATER.get(1000);

	ArrayList<ItemStack> produce = new ArrayList<ItemStack>();

	public FarmLogicWatered(IFarmHousing housing, ItemStack[] resource, ItemStack ground) {
		super(housing);
		this.ground = ground;
		this.resource = resource;
	}

	@Override
	public int getFertilizerConsumption() {
		return 5;
	}

	@Override
	public int getWaterConsumption(float hydrationModifier) {
		return (int) (20 * hydrationModifier);
	}

	public boolean isAcceptedGround(ItemStack ground) {
		return StackUtils.isIdenticalItem(this.ground, ground);
	}

	@Override
	public boolean isAcceptedResource(ItemStack itemstack) {
		return resource[0].isItemEqual(itemstack);
	}

	@Override
	public Collection<ItemStack> collect() {
		Collection<ItemStack> products = produce;
		produce = new ArrayList<ItemStack>();
		return products;
	}

	@Override
	public boolean cultivate(int x, int y, int z, ForgeDirection direction, int extent) {

		if (maintainSoil(x, y, z, direction, extent))
			return true;

		if (!isManual && maintainWater(x, y, z, direction, extent))
			return true;

		if (maintainCrops(x, y + 1, z, direction, extent))
			return true;

		return false;
	}

	private boolean maintainSoil(int x, int y, int z, ForgeDirection direction, int extent) {

		World world = getWorld();

		for (int i = 0; i < extent; i++) {
			Vect position = translateWithOffset(x, y, z, direction, i);
			Block block = getBlock(position);
			if (!isAirBlock(block) && !Utils.isReplaceableBlock(block)) {

				ItemStack blockStack = getAsItemStack(position);
				if (!isAcceptedGround(blockStack) && housing.hasResources(resource)) {
					produce.addAll(BlockUtil.getBlockItemStack(getWorld(), position));
					setBlock(position, Blocks.air, 0);
					return trySetSoil(position);
				}

				continue;
			}

			if (isManual || isWaterSourceBlock(world, position))
				continue;

			if (trySetWater(world, position))
				return true;

			return trySetSoil(position);
		}

		return false;
	}

	private boolean maintainWater(int x, int y, int z, ForgeDirection direction, int extent) {
		// Still not done, check water then
		World world = getWorld();
		for (int i = 0; i < extent; i++) {
			Vect position = translateWithOffset(x, y, z, direction, i);

			if (trySetWater(world, position))
				return true;
		}

		return false;
	}

	protected boolean maintainCrops(int x, int y, int z, ForgeDirection direction, int extent) {
		return false;
	}

	private boolean trySetSoil(Vect position) {
		if (!housing.hasResources(resource))
			return false;
		setBlock(position, StackUtils.getBlock(ground), ground.getItemDamage());
		housing.removeResources(resource);
		return true;
	}

	private boolean trySetWater(World world, Vect position) {
		if (isWaterSourceBlock(world, position) || !canPlaceWater(world, position))
			return false;

		if (!housing.hasLiquid(STACK_WATER))
			return false;

		produce.addAll(BlockUtil.getBlockItemStack(world, position));
		setBlock(position, Blocks.water, 0);
		housing.removeLiquid(STACK_WATER);
		return true;
	}

	private boolean canPlaceWater(World world, Vect position) {
		// don't place water close to other water
		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				Vect offsetPosition = position.add(new Vect(x, 0, z));
				if (isWaterSourceBlock(world, offsetPosition))
					return false;
			}
		}

		// don't place water if it can flow into blocks next to it
		for (int x = -1; x <= 1; x++) {
			Vect offsetPosition = position.add(new Vect(x, 0, 0));
			if (isAirBlock(offsetPosition))
				return false;
		}
		for (int z = -1; z <= 1; z++) {
			Vect offsetPosition = position.add(new Vect(0, 0, z));
			if (isAirBlock(offsetPosition))
				return false;
		}

		return true;
	}

}

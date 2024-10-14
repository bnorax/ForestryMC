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
package forestry.core.network;

import net.minecraft.network.FriendlyByteBuf;

public interface IStreamable {
	/**
	 * Called on the serverside to sync additional information about this block to the client.
	 *
	 * @param data The stream of data about this object to send to the client.
	 */
	void writeData(FriendlyByteBuf data);

	/**
	 * Called on the clientside to receive data from the server.
	 *
	 * @param data The stream of data about this object sent by the server.
	 */
	void readData(FriendlyByteBuf data);
}

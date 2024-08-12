package forestry.core.config;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ForestryConfig {
	private static final ForgeConfigSpec CLIENT_SPEC;
	private static final ForgeConfigSpec COMMON_SPEC;
	private static final ForgeConfigSpec SERVER_SPEC;
	public static final Client CLIENT;
	public static final Common COMMON;
	public static final Server SERVER;

	public static class Common {
		public Common(ForgeConfigSpec.Builder builder) {

		}
	}

	public static class Client {
		public final ForgeConfigSpec.BooleanValue showParticles;
		public final ForgeConfigSpec.BooleanValue enableHints;

		public Client(ForgeConfigSpec.Builder builder) {
			this.showParticles = builder
					.comment("Whether any of Forestry's particles are rendered.")
					.define("particles", true);
			this.enableHints = builder
					.comment("Whether the \"Did you know?\" ledgers are shown in Forestry menus.")
					.define("enable_hints", true);
		}
	}

	public static class Server {
		public final ForgeConfigSpec.BooleanValue pollinateVanillaLeaves;
		public final ForgeConfigSpec.DoubleValue researchMutationBoostMultiplier;
		public final ForgeConfigSpec.DoubleValue maxResearchMutationBoostPercent;
		public final ForgeConfigSpec.BooleanValue enableBackpackResupply;
		public final ForgeConfigSpec.IntValue butterflyClusterLimit;
		public final ForgeConfigSpec.IntValue butterflyClusterWidth;
		public final ForgeConfigSpec.IntValue butterflyClusterHeight;
		public final ForgeConfigSpec.BooleanValue mailAlertsEnabled;
		public final ForgeConfigSpec.BooleanValue mailAlertsOnRight;
		public final ForgeConfigSpec.BooleanValue mailAlertsOnBottom;
		public final ForgeConfigSpec.IntValue multiFarmSize;
		public final ForgeConfigSpec.BooleanValue squareMultiFarms;
		public final ForgeConfigSpec.IntValue legacyFarmsPlanterRings;
		public final ForgeConfigSpec.BooleanValue legacyFarmsUseRings;
		public final ForgeConfigSpec.IntValue legacyFarmsRingSize;

		public Server(ForgeConfigSpec.Builder builder) {
			builder.push("genetics");
			this.pollinateVanillaLeaves = builder
					.comment("Whether bees and butterflies can pollinate Vanilla leaves. Might be undesirable for builds that rely on leaves.")
					.define("pollinate_vanilla_leaves", true);
			this.researchMutationBoostMultiplier = builder
					.comment("When a player researches a mutation using the Escritoire, mutation chances for hives owned by that player are multiplied by this factor, with the increase in chance limited to the value set in \"research_mutation_boost_multiplier\".")
					.defineInRange("research_mutation_boost_multiplier", 1.5, 1.0, 1000.0);
			this.maxResearchMutationBoostPercent = builder
					.comment("When a player researchs a mutation using the Escritoire, mutation chances for hives owned by that player are multiplied by a certain factor, with the increase in chance capped to this value.")
					.defineInRange("max_research_mutation_boost_percent", 5.0, 0.0, 100.0);
			builder.pop();

			builder.push("farming");
			this.multiFarmSize = builder
					.comment("")
					.defineInRange("multiblock_farm_size", 2, 1, 10);
			this.squareMultiFarms = builder
					.comment("Whether Forestry multiblock farms have square shaped farmlands instead of the default diamond shape.")
					.define("square_multiblock_farms", false);
			this.legacyFarmsPlanterRings = builder
					.comment("Sets the size of the farmland that is used by all legacy (single block) farms.")
					.defineInRange("legacy_farms_planter_rings", 4, 1, 10);
			this.legacyFarmsUseRings = builder
					.comment("Whether legacy (single block) farms use a ring layout. The farmland size of the ring layout is always one block smaller.")
					.define("legacy_farms_use_rings", true);
			this.legacyFarmsRingSize = builder
					.comment("Sets the size of the inner ring of the ring layout.")
					.defineInRange("legacy_farms_ring_size", 4, 1, 10);
			builder.pop();

			builder.push("butterflies");
			this.butterflyClusterLimit = builder
					.comment("The maximum number of butterflies that can spawn in the same area or cluster.")
					.defineInRange("butterfly_cluster_limit", 20, 1, 2000);
			this.butterflyClusterWidth = builder
					.comment("The width of the cluster area used when checking if the \"butterfly_cluster_limit\" has been reached.")
					.defineInRange("butterfly_cluster_width", 128, 0, 2000);
			this.butterflyClusterHeight = builder
					.comment("The height of the cluster area used when checking if the \"butterfly_cluster_limit\" has been reached.")
					.defineInRange("butterfly_cluster_height", 64, 0, 2000);
			builder.pop();

			builder.push("mail");
			this.mailAlertsEnabled = builder
					.comment("Whether alerts are enabled for Forestry's mail system.")
					.define("mail_alerts_enable", true);
			this.mailAlertsOnRight = builder
					.comment("Whether mail alerts are shown on the right of the screen instead of the left.")
					.define("mail_alerts_on_right", false);
			this.mailAlertsOnBottom = builder
					.comment("Whether mail alerts are shown on the bottom of the screen instead of the top.")
					.define("mail_alerts_on_bottom", false);
			builder.pop();

			this.enableBackpackResupply = builder
					.comment("Whether backpacks can have their resupply mode enabled, which stocks a player's inventory using blocks from the backpack's inventory.")
					.define("enable_backpack_resupply", true);
		}
	}

	public static void register(ModLoadingContext ctx) {
		ctx.registerConfig(ModConfig.Type.SERVER, SERVER_SPEC);
		ctx.registerConfig(ModConfig.Type.COMMON, COMMON_SPEC);
		ctx.registerConfig(ModConfig.Type.CLIENT, CLIENT_SPEC);
	}

	static {
		{
			Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
			CLIENT = specPair.getLeft();
			CLIENT_SPEC = specPair.getRight();
		}
		{
			Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
			COMMON = specPair.getLeft();
			COMMON_SPEC = specPair.getRight();
		}
		{
			Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Server::new);
			SERVER = specPair.getLeft();
			SERVER_SPEC = specPair.getRight();
		}
	}
}
package gaia.entity.passive;

import gaia.entity.EntityMobMerchant;
import gaia.entity.GaiaTrade;
import gaia.init.GaiaItems;
import gaia.init.Sounds;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;

@SuppressWarnings("squid:MaximumInheritanceDepth")
public class EntityGaiaNPCEnderGirl extends EntityMobMerchant {

	public EntityGaiaNPCEnderGirl(World var1) {
		super(var1);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return Sounds.PASSIVE_SAY;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return Sounds.PASSIVE_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return Sounds.PASSIVE_DEATH;
	}

	@Override
	protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
		if (wasRecentlyHit && (rand.nextInt(1) == 0 || rand.nextInt(1 + lootingModifier) > 0)) {
			entityDropItem(new ItemStack(GaiaItems.SpawnEnderGirl, 1, 0), 0.0F);
		}
	}

	@Override
	public void addRecipies(MerchantRecipeList recipes) {
		recipes.add(new GaiaTrade(new ItemStack(GaiaItems.MiscCurrency, 1, 0), new ItemStack(Items.ENDER_PEARL, 1, 0)));

		// Buy List
		recipes.add(new GaiaTrade(new ItemStack(Items.ENDER_PEARL, 1, 0), new ItemStack(Items.BONE, 8, 0)));
		recipes.add(new GaiaTrade(new ItemStack(Items.ENDER_PEARL, 1, 0), new ItemStack(Items.ROTTEN_FLESH, 16, 0)));
		recipes.add(new GaiaTrade(new ItemStack(Items.ENDER_PEARL, 1, 0), new ItemStack(Items.GUNPOWDER, 4, 0)));
		recipes.add(new GaiaTrade(new ItemStack(Items.ENDER_PEARL, 1, 0), new ItemStack(Items.SPIDER_EYE, 4, 0)));
		recipes.add(new GaiaTrade(new ItemStack(Items.ENDER_PEARL, 1, 0), new ItemStack(Items.STRING, 8, 0)));

		// Sell List
		recipes.add(new GaiaTrade(new ItemStack(Items.BONE, 16, 0), new ItemStack(Items.ENDER_PEARL, 1, 0)));
		recipes.add(new GaiaTrade(new ItemStack(Items.GUNPOWDER, 8, 0), new ItemStack(Items.ENDER_PEARL, 1, 0)));
		recipes.add(new GaiaTrade(new ItemStack(Items.ROTTEN_FLESH, 24, 0), new ItemStack(Items.ENDER_PEARL, 1, 0)));
		recipes.add(new GaiaTrade(new ItemStack(Items.STRING, 16, 0), new ItemStack(Items.ENDER_PEARL, 1, 0)));
		recipes.add(new GaiaTrade(new ItemStack(Items.SLIME_BALL, 8, 0), new ItemStack(Items.ENDER_PEARL, 1, 0)));
		recipes.add(new GaiaTrade(new ItemStack(Items.SPIDER_EYE, 8, 0), new ItemStack(Items.ENDER_PEARL, 1, 0)));
	}
}

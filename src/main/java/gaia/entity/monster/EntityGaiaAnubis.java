package gaia.entity.monster;

import gaia.GaiaConfig;
import gaia.entity.EntityAttributes;
import gaia.entity.EntityMobHostileBase;
import gaia.entity.ai.Ranged;
import gaia.init.GaiaItems;
import gaia.init.Sounds;
import gaia.items.ItemShard;
import gaia.renderer.particle.ParticleWarning;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings({"squid:MaximumInheritanceDepth", "squid:S2160"})
public class EntityGaiaAnubis extends EntityMobHostileBase implements IRangedAttackMob {

	private EntityAIAttackRanged aiArrowAttack = new EntityAIAttackRanged(this, EntityAttributes.ATTACK_SPEED_2, 20, 60, 15.0F);
	private EntityAIAttackMelee aiAttackOnCollide = new EntityAIAttackMelee(this, EntityAttributes.ATTACK_SPEED_2, true);

	private int switchHealth;
	private int spawn;
	private int spawnTimer;
	private int spawnLevel3;
	private int spawnLevel3Chance;

	public EntityGaiaAnubis(World worldIn) {
		super(worldIn);

		experienceValue = EntityAttributes.EXPERIENCE_VALUE_2;
		stepHeight = 1.0F;

		setPathPriority(PathNodeType.DANGER_FIRE, 0.0F);

		switchHealth = 0;
		spawn = 0;
		spawnTimer = 0;

		spawnLevel3 = 0;
		spawnLevel3Chance = 0;
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(0, new EntityAISwimming(this));

		tasks.addTask(2, new EntityAIWander(this, 1.0D));
		tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		tasks.addTask(4, new EntityAILookIdle(this));
		targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(EntityAttributes.MAX_HEALTH_2);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(EntityAttributes.FOLLOW_RANGE);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(EntityAttributes.MOVE_SPEED_2);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(EntityAttributes.ATTACK_DAMAGE_2);
		getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(EntityAttributes.RATE_ARMOR_2);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (damage > EntityAttributes.BASE_DEFENSE_2) {
			if (GaiaConfig.GENERAL.spawnLevel3) {
				spawnLevel3Chance += (int) (GaiaConfig.GENERAL.spawnLevel3Chance * 0.05);
			}
			return super.attackEntityFrom(source, EntityAttributes.BASE_DEFENSE_2);
		}

		return super.attackEntityFrom(source, damage);
	}

	@Override
	public void knockBack(Entity entityIn, float strength, double xRatio, double zRatio) {
		super.knockBack(xRatio, zRatio, EntityAttributes.KNOCKBACK_2);
	}

	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
		Ranged.magic(target, this, distanceFactor);
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		if (super.attackEntityAsMob(entityIn)) {
			if (entityIn instanceof EntityLivingBase) {
				byte byte0 = 0;
				byte byte1 = 0;

				if (world.getDifficulty() == EnumDifficulty.NORMAL) {
					byte0 = 5;
					byte1 = 10;
				} else if (world.getDifficulty() == EnumDifficulty.HARD) {
					byte0 = 10;
					byte1 = 20;
				}

				if (byte0 > 0) {
					((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, byte0 * 20, 0));
					((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, byte1 * 20, 0));
				}
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isAIDisabled() {
		return false;
	}

	@Override
	public void onLivingUpdate() {
		beaconMonster();

		if ((getHealth() < EntityAttributes.MAX_HEALTH_2 * 0.75F) && (switchHealth == 0)) {
			tasks.removeTask(aiArrowAttack);
			tasks.addTask(1, aiAttackOnCollide);
			switchHealth = 1;
		}

		if ((getHealth() > EntityAttributes.MAX_HEALTH_2 * 0.75F) && (switchHealth == 1)) {
			tasks.removeTask(aiAttackOnCollide);
			tasks.addTask(1, aiArrowAttack);
			switchHealth = 0;
		}

		EntitySkeleton spawnMob;
		if (getHealth() < EntityAttributes.MAX_HEALTH_2 * 0.75F && getHealth() > 0.0F && spawn == 0) {

			setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(Items.STICK));

			if (spawnTimer != 30) {
				spawnTimer += 1;
			}

			if (spawnTimer == 30) {
				world.setEntityState(this, (byte) 12);
				setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(Items.EGG));

				if (!world.isRemote) {
					spawnMob = new EntitySkeleton(world);
					spawnMob.setLocationAndAngles(posX, posY, posZ, rotationYaw, 0.0F);
					spawnMob.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(spawnMob)), null);
					spawnMob.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Blocks.PUMPKIN));
					world.spawnEntity(spawnMob);
				}

				spawnTimer = 0;
				spawn = 1;
			}
		}

		if (getHealth() < EntityAttributes.MAX_HEALTH_2 * 0.25F && getHealth() > 0.0F && spawn == 1) {

			setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(Items.STICK));

			if (spawnTimer != 30) {
				spawnTimer += 1;
			}

			if (spawnTimer == 30) {
				world.setEntityState(this, (byte) 12);
				setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(Items.EGG));

				if (!world.isRemote) {
					spawnMob = new EntitySkeleton(world);
					spawnMob.setLocationAndAngles(posX, posY, posZ, rotationYaw, 0.0F);
					spawnMob.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(spawnMob)), null);
					spawnMob.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Blocks.PUMPKIN));
					world.spawnEntity(spawnMob);
				}

				if (GaiaConfig.GENERAL.spawnLevel3) {
					if (spawnLevel3Chance > (int) (GaiaConfig.GENERAL.spawnLevel3Chance * 0.5)) {
						spawnLevel3Chance = (int) (GaiaConfig.GENERAL.spawnLevel3Chance * 0.5);
					}

					if ((rand.nextInt(GaiaConfig.GENERAL.spawnLevel3Chance - spawnLevel3Chance) == 0 || rand.nextInt(1) > 0)) {
						spawnLevel3 = 1;
					}
				}

				spawnTimer = 0;
				spawn = 2;
			}
		}

		if (spawnLevel3 == 1) {
			world.setEntityState(this, (byte) 13);

			attackEntityFrom(DamageSource.GENERIC, EntityAttributes.MAX_HEALTH_2 * 0.01F);
		}

		super.onLivingUpdate();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(byte id) {
		if (id == 12) {
			spawnParticles(EnumParticleTypes.EXPLOSION_NORMAL);
		} else if (id == 13) {
			for (int i = 0; i < 1; ++i) {
				ParticleWarning particleCustom = new ParticleWarning(world,
						posX + rand.nextDouble() * width * 2.0D - width,
						posY + 1.0D + rand.nextDouble() * height,
						posZ + rand.nextDouble() * width * 2.0D - width, 0.0D, 0.0D, 0.0D);
				Minecraft.getMinecraft().effectRenderer.addEffect(particleCustom);
			}
		} else {
			super.handleStatusUpdate(id);
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(EnumParticleTypes particleType) {
		for (int i = 0; i < 5; ++i) {
			double d0 = rand.nextGaussian() * 0.02D;
			double d1 = rand.nextGaussian() * 0.02D;
			double d2 = rand.nextGaussian() * 0.02D;
			world.spawnParticle(particleType,
					posX + rand.nextDouble() * width * 2.0D - width,
					posY + 1.0D + rand.nextDouble() * height,
					posZ + rand.nextDouble() * width * 2.0D - width, d0, d1, d2);
		}
	}

	private void beaconMonster() {
		if (!world.isRemote) {
			AxisAlignedBB axisalignedbb = (new AxisAlignedBB(posX, posY, posZ, posX + 1, posY + 1, posZ + 1)).grow(6D);

			List<EntityLivingBase> moblist = world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);

			for (EntityLivingBase mob : moblist) {
				if (mob instanceof EntityZombie || mob instanceof EntitySkeleton) {
					mob.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 300, 1, true, true));
				}
			}
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return Sounds.AGGRESSIVE_SAY;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return Sounds.AGGRESSIVE_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return Sounds.AGGRESSIVE_DEATH;
	}

	@Override
	protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {
		super.dropLoot(wasRecentlyHit, lootingModifier, source);

		dropFewItems(wasRecentlyHit, lootingModifier);
	}

	@Nullable
	@Override
	protected ResourceLocation getLootTable() {
		return LootTableList.ENTITIES_WITCH;
	}

	@Override
	protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
		if (wasRecentlyHit) {
			// Nuggets/Fragments
			int var11 = rand.nextInt(3) + 1;

			for (int var12 = 0; var12 < var11; ++var12) {
				ItemShard.Drop_Nugget(this, 1);
			}

			if (GaiaConfig.OPTIONS.additionalOre) {
				int var13 = rand.nextInt(3) + 1;

				for (int var14 = 0; var14 < var13; ++var14) {
					ItemShard.Drop_Nugget(this, 5);
				}
			}

			// Rare
			if ((rand.nextInt(EntityAttributes.RATE_RARE_DROP) == 0 || rand.nextInt(1 + lootingModifier) > 0)) {
				int i = rand.nextInt(3);
				if (i == 0) {
					dropItem(GaiaItems.BoxGold, 1);

				} else if (i == 1) {
					dropItem(GaiaItems.BagBook, 1);

				} else if (i == 2) {
					dropItem(GaiaItems.MiscBook, 1);
				}
			}
		}

		// Boss
		if (spawnLevel3 == 1) {
			spawnLevel3();
		}
	}

	private void spawnLevel3() {
		EntityGaiaSphinx sphinx;
		sphinx = new EntityGaiaSphinx(world);
		sphinx.setLocationAndAngles(posX, posY, posZ, rotationYaw, 0.0F);
		sphinx.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(sphinx)), null);
		world.spawnEntity(sphinx);
	}

	@Override
	protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier) {
		//noop
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		IEntityLivingData ret = super.onInitialSpawn(difficulty, livingdata);

		tasks.removeTask(aiAttackOnCollide);
		tasks.addTask(1, aiArrowAttack);

		setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(GaiaItems.PropWeapon, 1, 0));

		return ret;
	}

	@Override
	public boolean getCanSpawnHere() {
		return posY > 60.0D && super.getCanSpawnHere();
	}
}

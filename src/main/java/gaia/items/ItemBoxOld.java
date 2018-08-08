package gaia.items;

import gaia.init.GaiaItems;
import gaia.init.Sounds;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class ItemBoxOld extends ItemGaiaLootable {

	public ItemBoxOld(String name) {
		super(name);
		setMaxStackSize(1);
	}

	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}

	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(I18n.format("text.grimoireofgaia.RightClickUse"));
	}

	@Override
	public @Nonnull
	ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand handIn) {
		final ItemStack stack = player.getHeldItem(handIn);

		player.playSound(Sounds.BOX_OPEN_2, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
		Random random = new Random();
		int i = random.nextInt(4);
		switch (i) {
			case 0:
				return loot(GaiaItems.MiscGigaGear);
			case 1:
				return loot(GaiaItems.BookWither);
			case 2:
				return loot(GaiaItems.Spawn);
			case 3:
				return loot(GaiaItems.BagBook);
			default:
				return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		}
	}
}

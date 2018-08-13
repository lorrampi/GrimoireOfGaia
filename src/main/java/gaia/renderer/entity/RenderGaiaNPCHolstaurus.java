package gaia.renderer.entity;

import gaia.GaiaReference;
import gaia.model.ModelGaiaNPCHolstaurus;
import gaia.renderer.entity.layers.LayerGaiaHeldItem;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderGaiaNPCHolstaurus extends RenderLiving<EntityLiving> {
	private static final ResourceLocation texture = new ResourceLocation(GaiaReference.MOD_ID, "textures/models/holstaurus.png");

	public RenderGaiaNPCHolstaurus(RenderManager renderManager, float shadowSize) {
		super(renderManager, new ModelGaiaNPCHolstaurus(), shadowSize);
		addLayer(LayerGaiaHeldItem.right(this, ModelGaiaNPCHolstaurus.rightarm));
		addLayer(LayerGaiaHeldItem.left(this, ModelGaiaNPCHolstaurus.leftarm));
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityLiving entity) {
		return texture;
	}
}

package gaia.renderer.entity;

import gaia.GaiaReference;
import gaia.entity.monster.EntityGaiaDryad;
import gaia.model.ModelGaiaDryad;
import gaia.renderer.entity.layers.LayerGaiaHeldItem;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderGaiaDryad extends RenderLiving<EntityLiving> {
	private static final ResourceLocation texture01 = new ResourceLocation(GaiaReference.MOD_ID, "textures/models/dryad01.png");
	private static final ResourceLocation texture02 = new ResourceLocation(GaiaReference.MOD_ID, "textures/models/alternate/dryad02.png");

	public RenderGaiaDryad(RenderManager renderManager, float shadowSize) {
		super(renderManager, new ModelGaiaDryad(), shadowSize);
		addLayer(LayerGaiaHeldItem.right(this, ModelGaiaDryad.rightarm));
	}

	@Override
	public void transformHeldFull3DItemLayer() {
		GlStateManager.translate(0.0F, 0.1875F, 0.0F);
	}

	private ResourceLocation getTexture(EntityGaiaDryad entityGaiaDryad) {
		switch (entityGaiaDryad.getTextureType()) {
			case 0:
				return texture01;
			case 1:
				return texture02;
			default:
				return texture01;
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityLiving entity) {
		return getTexture((EntityGaiaDryad) entity);
	}
}

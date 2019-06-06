package dohyun22.jst3.client.entity;

import dohyun22.jst3.entity.EntityCar;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCar extends Render<EntityCar> {
	private static final ModelCar MODEL = new ModelCar();

	public RenderCar(RenderManager rm) {
		super(rm);
		shadowSize = 0.5F;
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityCar e) {
		return e.getTex();
	}

	public void doRender(EntityCar e, double x, double y, double z, float yw, float pt) {
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)x, (float)y + 0.625F, (float)z);
		GlStateManager.rotate(270.0F - yw, 0.0F, 1.0F, 0.0F);
		GlStateManager.scale(-1.0F, -1.0F, 1.0F);
		bindEntityTexture(e);
		if (renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.enableOutlineMode(getTeamColor(e));
		}
		MODEL.render(e, pt, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		if (renderOutlines) {
			GlStateManager.disableOutlineMode();
			GlStateManager.disableColorMaterial();
		}
		GlStateManager.popMatrix();
		super.doRender(e, x, y, z, yw, pt);
	}
}

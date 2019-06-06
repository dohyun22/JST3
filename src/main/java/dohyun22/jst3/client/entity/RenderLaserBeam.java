package dohyun22.jst3.client.entity;

import org.lwjgl.opengl.GL11;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.entity.EntityLaserBeam;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;

@SideOnly(Side.CLIENT)
public class RenderLaserBeam <T extends EntityLaserBeam> extends Render<T> {
	private static final ResourceLocation tex = new ResourceLocation(JustServerTweak.MODID, "textures/entity/beam.png");

	public RenderLaserBeam(RenderManager rm) {
		super(rm);
	}

	@Override
	public void doRender(EntityLaserBeam e, double x, double y, double z, float yaw, float pt) {
		if (e.prevRotationYaw == 0.0F && e.prevRotationPitch == 0.0F) return;
		bindTexture(getEntityTexture(e));

	    GlStateManager.pushMatrix();
	    GlStateManager.translate((float)x, (float)y, (float)z);
	    GlStateManager.rotate(e.prevRotationYaw + (e.rotationYaw - e.prevRotationYaw) * pt - 90.0F, 0.0F, 1.0F, 0.0F);
	    GlStateManager.rotate(e.prevRotationPitch + (e.rotationPitch - e.prevRotationPitch) * pt, 0.0F, 0.0F, 1.0F);
	    Tessellator ts = Tessellator.getInstance();
	    BufferBuilder vb = ts.getBuffer();
	    float uSideS = 0.0F;
	    float uSideE = 0.5F;
	    float vSideS = 0.0F;
	    float vSideE = 0.15625F;
	    float uBackS = 0.0F;
	    float uBackE = 0.15625F;
	    float vBackS = 0.15625F;
	    float vBackE = 0.3125F;
	    float scale = 0.05625F;
	    GlStateManager.enableRescaleNormal();
	    GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
	    GlStateManager.scale(scale, scale, scale);
	    GlStateManager.translate(-4.0F, 0.0F, 0.0F);
	    GL11.glNormal3f(scale, 0.0F, 0.0F);
	    vb.begin(7, DefaultVertexFormats.POSITION_TEX);
	    vb.pos(-7.0D, -2.0D, -2.0D).tex(uBackS, vBackS).endVertex();
	    vb.pos(-7.0D, -2.0D, 2.0D).tex(uBackE, vBackS).endVertex();
	    vb.pos(-7.0D, 2.0D, 2.0D).tex(uBackE, vBackE).endVertex();
	    vb.pos(-7.0D, 2.0D, -2.0D).tex(uBackS, vBackE).endVertex();
	    ts.draw();
	    GL11.glNormal3f(-scale, 0.0F, 0.0F);
	    vb.begin(7, DefaultVertexFormats.POSITION_TEX);
	    vb.pos(-7.0D, 2.0D, -2.0D).tex(uBackS, vBackS).endVertex();
	    vb.pos(-7.0D, 2.0D, 2.0D).tex(uBackE, vBackS).endVertex();
	    vb.pos(-7.0D, -2.0D, 2.0D).tex(uBackE, vBackE).endVertex();
	    vb.pos(-7.0D, -2.0D, -2.0D).tex(uBackS, vBackE).endVertex();
	    ts.draw();
	    for (int j = 0; j < 4; j++) {
	    	GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
	    	GL11.glNormal3f(0.0F, 0.0F, scale);
	    	vb.begin(7, DefaultVertexFormats.POSITION_TEX);
	    	vb.pos(-8.0D, -2.0D, 0.0D).tex(uSideS, vSideS).endVertex();
	    	vb.pos(8.0D, -2.0D, 0.0D).tex(uSideE, vSideS).endVertex();
	    	vb.pos(8.0D, 2.0D, 0.0D).tex(uSideE, vSideE).endVertex();
	    	vb.pos(-8.0D, 2.0D, 0.0D).tex(uSideS, vSideE).endVertex();
	    	ts.draw();
	    }
	    GlStateManager.disableRescaleNormal();
	    GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityLaserBeam e) {
		return tex;
	}
}

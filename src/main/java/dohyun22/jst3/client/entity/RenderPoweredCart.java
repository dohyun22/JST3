package dohyun22.jst3.client.entity;

import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import dohyun22.jst3.entity.EntityPoweredCart;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderMinecart;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;

@SideOnly(Side.CLIENT)
public class RenderPoweredCart extends RenderMinecart<EntityPoweredCart> {
	private static final ResourceLocation tex = new ResourceLocation("jst3:textures/entity/ecart.png");

	public RenderPoweredCart(RenderManager rm) {
		super(rm);
		shadowSize = 0.5F;
		modelMinecart = new ModelPoweredCart();
	}
  
	@Override
	protected ResourceLocation getEntityTexture(EntityPoweredCart e) {
		return tex;
	}

	@Override
	public void doRender(EntityPoweredCart e, double x, double y, double z, float yaw, float pt) {
		GL11.glPushMatrix();
		long var10 = e.getEntityId() * 493286711L;
		var10 = var10 * var10 * 4392167121L + var10 * 98761L;
		float tx = (((float) (var10 >> 16 & 0x7) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		float ty = (((float) (var10 >> 20 & 0x7) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		float tz = (((float) (var10 >> 24 & 0x7) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		GL11.glTranslatef(tx, ty, tz);
		double mx = e.lastTickPosX + (e.posX - e.lastTickPosX) * pt;
		double my = e.lastTickPosY + (e.posY - e.lastTickPosY) * pt;
		double mz = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * pt;
		double d6 = 0.3D;
		Vec3d v3 = e.getPos(mx, my, mz);
		float p = e.prevRotationPitch + (e.rotationPitch - e.prevRotationPitch) * pt;
		if (v3 != null) {
			Vec3d v31 = e.getPosOffset(mx, my, mz, d6);
			Vec3d v32 = e.getPosOffset(mx, my, mz, -d6);
			if (v31 == null)
				v31 = v3;
			if (v32 == null)
				v32 = v3;
			x += v3.x - mx;
			y += (v31.y + v32.y) / 2.0D - my;
			z += v3.z - mz;
			Vec3d v33 = v32.addVector(-v31.x, -v31.y, -v31.z);
			if (v33.lengthVector() != 0.0D) {
				v33 = v33.normalize();
				yaw = (float) (Math.atan2(v33.z, v33.x) / 3.141592653589793D) * 180.0F;
				p = (float) (Math.atan(v33.y) * 73.0D);
			}
		}
		yaw %= 360.0F;
		if (yaw < 0.0F)
			yaw += 360.0F;
		yaw += 360.0F;

		double sY = e.rotationYaw;
		sY += 180.0D;
		sY %= 360.0D;
		if (sY < 0.0D)
			sY += 360.0D;
		sY += 360.0D;
		if (Math.abs(yaw - sY) > 90.0D) {
			yaw += 180.0F;
			p = -p;
		}
		e.setRenderYaw(yaw);
		GL11.glTranslatef((float) x, (float) y + 0.375F, (float) z);
		GL11.glRotatef(180.0F - yaw, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-p, 0.0F, 0.0F, 1.0F);
		float f3 = e.getRollingAmplitude() - pt;
		float f4 = e.getDamage() - pt;
		if (f4 < 0.0F)
			f4 = 0.0F;
		if (f3 > 0.0F) {
			float angle = MathHelper.sin(f3) * f3 * f4 / 10.0F;
			angle = Math.min(angle, 0.8F);
			angle = Math.copySign(angle, e.getRollingDirection());
			GL11.glRotatef(angle, 1.0F, 0.0F, 0.0F);
		}
		bindEntityTexture(e);
		GL11.glScalef(-1.0F, -1.0F, 1.0F);
		modelMinecart.render(e, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GL11.glPopMatrix();
	}
}
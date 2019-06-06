package dohyun22.jst3.client.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import dohyun22.jst3.entity.EntityPrimedOre;

@SideOnly(Side.CLIENT)
public class RenderPrimedOre <T extends EntityPrimedOre> extends Render<T> {
	public RenderPrimedOre(RenderManager rm) {
		super(rm);
		shadowSize = 0.0F;
	}

	@Override
	public void doRender(EntityPrimedOre e, double x, double y, double z, float yaw, float pt) {}

	@Override
	protected ResourceLocation getEntityTexture(EntityPrimedOre e) {
		return null;
	}
}
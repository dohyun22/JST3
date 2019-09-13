package dohyun22.jst3.client;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

@SideOnly(Side.CLIENT)
public class ParticleFlamethrower extends Particle {
	private float flameScale;
	private boolean changed;

	public ParticleFlamethrower(World w, double x, double y, double z, double xs, double ys, double zs) {
		super(w, x, y, z, xs, ys, zs);
		motionX = (motionX * 0.01D + xs);
		motionY = (motionY * 0.01D + ys);
		motionZ = (motionZ * 0.01D + zs);
		posX += (rand.nextFloat() - rand.nextFloat()) * 0.05F;
		posY += (rand.nextFloat() - rand.nextFloat()) * 0.05F;
		posZ += (rand.nextFloat() - rand.nextFloat()) * 0.05F;
		flameScale = particleScale;
		particleRed = 1.0F;
		particleGreen = 1.0F;
		particleBlue = 1.0F;
		particleMaxAge = ((int) (8.0D / (Math.random() * 0.8D + 0.2D)) + 4);
		setParticleTextureIndex(48);
	}

	@Override
	public void renderParticle(BufferBuilder b, Entity e, float pt, float rx, float rz, float ryz, float rxy, float rxz) {
		float f = (particleAge + pt) / particleMaxAge;
		particleScale = (flameScale * (1.0F - f * f * 0.5F));
		super.renderParticle(b, e, pt, rx, rz, ryz, rxy, rxz);
	}

	@Override
	public int getBrightnessForRender(float in) {
		return changed ? 0 : 15728880;
	}

	@Override
	public void onUpdate() {
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		particleAge++;
		if (particleAge >= particleMaxAge)
			setExpired();
		else if (!changed && particleAge >= particleMaxAge - 5) {
			changed = true;
			particleRed = 0.08F;
			particleGreen = 0.08F;
			particleBlue = 0.08F;
			setParticleTextureIndex(5 + world.rand.nextInt(3));
		}
		move(motionX, motionY, motionZ);
		motionX *= 0.96D;
		motionY *= 0.96D;
		motionZ *= 0.96D;
		if (onGround) {
			if (particleAge < particleMaxAge - 5) {
				particleAge = particleMaxAge - 5;
				motionX = 0;
				motionY = 0.05F;
				motionZ = 0;
			}
		}
	}
}

package dohyun22.jst3.client.entity;

import dohyun22.jst3.entity.EntityPoweredCart;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelPoweredCart extends ModelBase {
	ModelRenderer Shape1;
	ModelRenderer Shape2;
	ModelRenderer Shape3;
	ModelRenderer Shape4;
	ModelRenderer Shape5;
	ModelRenderer powerBox;
	ModelRenderer insl1;
	ModelRenderer insl2;
	ModelRenderer insl3;
	ModelRenderer insl4;
	ModelRenderer pan1;
	ModelRenderer pan2;
	ModelRenderer pan3;
	ModelRenderer pan4;
	ModelRenderer pan5;
	ModelRenderer cond1;
	ModelRenderer cond2;
	ModelRenderer cond3;
	  
	public ModelPoweredCart() {
		textureWidth = 64;
		textureHeight = 64;
	    
		Shape1 = new ModelRenderer(this, 0, 10);
		Shape1.addBox(-10F, -8F, -1F, 20, 16, 2);
		Shape1.setRotationPoint(0F, 4F, 0F);
		Shape1.setTextureSize(64, 64);
		Shape1.mirror = true;
		setRotation(Shape1, 1.570796F, 0F, 0F);
		Shape2 = new ModelRenderer(this, 0, 0);
		Shape2.addBox(-8F, -9F, -1F, 16, 8, 2);
		Shape2.setRotationPoint(-9F, 4F, 0F);
		Shape2.setTextureSize(64, 64);
		Shape2.mirror = true;
		setRotation(Shape2, 0F, -1.570796F, 0F);
		Shape3 = new ModelRenderer(this, 0, 0);
		Shape3.addBox(-8F, -9F, -1F, 16, 8, 2);
		Shape3.setRotationPoint(9F, 4F, 0F);
		Shape3.setTextureSize(64, 64);
		Shape3.mirror = true;
		setRotation(Shape3, 0F, 1.570796F, 0F);
		Shape4 = new ModelRenderer(this, 0, 0);
		Shape4.addBox(-8F, -9F, -1F, 16, 8, 2);
		Shape4.setRotationPoint(0F, 4F, -7F);
		Shape4.setTextureSize(64, 64);
		Shape4.mirror = true;
		setRotation(Shape4, 0F, 3.141593F, 0F);
		Shape5 = new ModelRenderer(this, 0, 0);
		Shape5.addBox(-8F, -9F, -1F, 16, 8, 2);
		Shape5.setRotationPoint(0F, 4F, 7F);
		Shape5.setTextureSize(64, 64);
		Shape5.mirror = true;
		setRotation(Shape5, 0F, 0F, 0F);
		powerBox = new ModelRenderer(this, 0, 28);
		powerBox.addBox(-6F, -6F, -6F, 12, 12, 12);
		powerBox.setRotationPoint(0F, -3F, 0F);
		powerBox.setTextureSize(64, 64);
		setRotation(powerBox, 0F, 0F, 0F);
		insl1 = new ModelRenderer(this, 36, 0);
		insl1.addBox(-1F, -3F, -1F, 2, 6, 2);
		insl1.setRotationPoint(-4F, -12F, 4F);
		insl1.setTextureSize(64, 64);
		setRotation(insl1, 0F, 0F, 0F);
		insl2 = new ModelRenderer(this, 36, 0);
		insl2.addBox(-1F, -3F, -1F, 2, 6, 2);
		insl2.setRotationPoint(-4F, -12F, -4F);
		insl2.setTextureSize(64, 64);
		setRotation(insl2, 0F, 0F, 0F);
		insl3 = new ModelRenderer(this, 36, 0);
		insl3.addBox(-1F, -3F, -1F, 2, 6, 2);
		insl3.setRotationPoint(4F, -12F, 4F);
		insl3.setTextureSize(64, 64);
		setRotation(insl3, 0F, 0F, 0F);
		insl4 = new ModelRenderer(this, 36, 0);
		insl4.addBox(-1F, -3F, -1F, 2, 6, 2);
		insl4.setRotationPoint(4F, -12F, -4F);
		insl4.setTextureSize(64, 64);
		setRotation(insl4, 0F, 0F, 0F);
		pan1 = new ModelRenderer(this, 44, 0);
		pan1.addBox(-0.5F, -20F, -1F, 1, 20, 2);
		pan1.setRotationPoint(-4.5F, -15.5F, 0F);
		pan1.setTextureSize(64, 64);
		setRotation(pan1, 0F, 0F, 1.396263F);
		pan2 = new ModelRenderer(this, 44, 0);
		pan2.addBox(-0.5F, -20F, -1F, 1, 20, 2);
		pan2.setRotationPoint(15.19F, -18.97F, 0F);
		pan2.setTextureSize(64, 64);
		setRotation(pan2, 0F, 0F, -1.396263F);
		pan3 = new ModelRenderer(this, 44, 0);
		pan3.addBox(-0.5F, -7F, -1F, 1, 14, 2);
		pan3.setRotationPoint(-4.5F, -22.6F, 0F);
		pan3.setTextureSize(64, 64);
		setRotation(pan3, 1.570796F, 0F, 1.570796F);
		pan4 = new ModelRenderer(this, 44, 0);
		pan4.addBox(-0.5F, 0F, -1F, 1, 2, 2);
		pan4.setRotationPoint(-4.5F, -22.8F, -6.5F);
		pan4.setTextureSize(64, 64);
		setRotation(pan4, 0F, 1.570796F, 0F);
		pan5 = new ModelRenderer(this, 44, 0);
		pan5.addBox(-0.5F, 0F, -1F, 1, 2, 2);
		pan5.setRotationPoint(-4.5F, -22.8F, 6.5F);
		pan5.setTextureSize(64, 64);
		setRotation(pan5, 0F, 1.570796F, 0F);
		cond1 = new ModelRenderer(this, 44, 0);
		cond1.addBox(-0.5F, -3F, -1F, 1, 6, 2);
		cond1.setRotationPoint(0F, -15.5F, 0F);
		cond1.setTextureSize(64, 64);
		setRotation(cond1, 0F, 0F, 1.570796F);
		cond2 = new ModelRenderer(this, 44, 0);
		cond2.addBox(-0.5F, -5F, -1F, 1, 10, 2);
		cond2.setRotationPoint(4F, -15.5F, 0F);
		cond2.setTextureSize(64, 64);
		setRotation(cond2, 1.570796F, 0F, 1.570796F);
		cond3 = new ModelRenderer(this, 44, 0);
		cond3.addBox(-0.5F, -5F, -1F, 1, 10, 2);
		cond3.setRotationPoint(-4F, -15.5F, 0F);
		cond3.setTextureSize(64, 64);
		setRotation(cond3, 1.570796F, 0F, 1.570796F);
	}

	@Override
	public void render(Entity e, float f, float f1, float f2, float f3, float f4, float f5) {
	    setRotationAngles(f, f1, f2, f3, f4, f5, e);
	    Shape1.render(f5);
	    Shape2.render(f5);
	    Shape3.render(f5);
	    Shape4.render(f5);
	    Shape5.render(f5);
	    powerBox.render(f5);
	    insl1.render(f5);
	    insl2.render(f5);
	    insl3.render(f5);
	    insl4.render(f5);
	    pan1.render(f5);
	    pan2.render(f5);
	    pan3.render(f5);
	    pan4.render(f5);
	    pan5.render(f5);
	    cond1.render(f5);
	    cond2.render(f5);
	    cond3.render(f5);
	}

	private void setRotation(ModelRenderer m, float x, float y, float z) {
		m.rotateAngleX = x;
		m.rotateAngleY = y;
		m.rotateAngleZ = z;
	}
	  
	@Override
	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
		if (e instanceof EntityPoweredCart) {
			EntityPoweredCart ep = (EntityPoweredCart)e;
			float panAng = 1.52F;
			switch (ep.getPanState()) {
			case 1:
				panAng = 1.36F;
				break;
			case 2:
				panAng = 0.91F;
				break;
			case 3:
				panAng = 0.12F;
				break;
			}
			
			float panh = MathHelper.cos(panAng) * -20;
			float base = -15.5F;
			pan1.rotateAngleZ = panAng;
			pan2.rotateAngleZ = panAng * -1;
			pan2.rotationPointX = -4.5F + (MathHelper.sin(panAng) * 20.0F);
			pan2.rotationPointY = base + panh;
			pan3.rotationPointY = base + panh * 2;
			pan4.rotationPointY = base + panh * 2;
			pan5.rotationPointY = base + panh * 2;
		}
	}

}

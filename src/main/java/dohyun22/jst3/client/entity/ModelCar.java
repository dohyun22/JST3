package dohyun22.jst3.client.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelCar extends ModelBase {
    private ModelRenderer bottom,side1,side2,side3,side4,seat1,seat2,handle,wheel1,wheel2,wheel3,wheel4,shaft1,shaft2;

    public ModelCar() {
        textureWidth = 128;
        textureHeight = 64;
        bottom = new ModelRenderer(this, 0, 0);
        bottom.setRotationPoint(0.0F, 3.0F, 0.0F);
        bottom.addBox(-14.0F, -8.0F, -3.0F, 28, 16, 2, 0.0F);
        setRotateAngle(bottom, 1.5707F, 0.0F, 0.0F);
        side3 = new ModelRenderer(this, 0, 40);
        side3.setRotationPoint(0.0F, 6.0F, -9.0F);
        side3.addBox(-14.0F, -9.0F, -1.0F, 28, 9, 2, 0.0F);
        setRotateAngle(side3, 0.0F, 3.1415F, 0.0F);
        handle = new ModelRenderer(this, 60, 7);
        handle.setRotationPoint(-13.9F, -3.0F, 0.0F);
        handle.addBox(0.0F, -3.0F, -3.0F, 0, 6, 6, 0.0F);
        setRotateAngle(handle, 0.0F, 0.0F, -0.5585F);
        wheel4 = new ModelRenderer(this, 48, 18);
        wheel4.setRotationPoint(12.0F, 8.0F, 6.5F);
        wheel4.addBox(-2.0F, -2.0F, -1.5F, 4, 4, 3, 0.0F);
        side4 = new ModelRenderer(this, 0, 51);
        side4.setRotationPoint(0.0F, 6.0F, 9.0F);
        side4.addBox(-14.0F, -9.0F, -1.0F, 28, 9, 2, 0.0F);
        shaft1 = new ModelRenderer(this, 44, 18);
        shaft1.setRotationPoint(-12.0F, 8.0F, 0.0F);
        shaft1.addBox(-0.5F, -5.0F, -0.5F, 1, 10, 1, 0.0F);
        setRotateAngle(shaft1, 1.5707F, 0.0F, 0.0F);
        side2 = new ModelRenderer(this, 0, 29);
        side2.setRotationPoint(15.0F, 6.0F, 0.0F);
        side2.addBox(-10.0F, -9.0F, -1.0F, 20, 9, 2, 0.0F);
        setRotateAngle(side2, 0.0F, 1.5707F, 0.0F);
        wheel1 = new ModelRenderer(this, 48, 18);
        wheel1.setRotationPoint(-12.0F, 8.0F, -6.5F);
        wheel1.addBox(-2.0F, -2.0F, -1.5F, 4, 4, 3, 0.0F);
        shaft2 = new ModelRenderer(this, 44, 18);
        shaft2.setRotationPoint(12.0F, 8.0F, 0.0F);
        shaft2.addBox(-0.5F, -5.0F, -0.5F, 1, 10, 1, 0.0F);
        setRotateAngle(shaft2, 1.5707F, 0.0F, 0.0F);
        side1 = new ModelRenderer(this, 0, 18);
        side1.setRotationPoint(-15.0F, 6.0F, 0.0F);
        side1.addBox(-10.0F, -9.0F, -1.0F, 20, 9, 2, 0.0F);
        setRotateAngle(side1, 0.0F, 4.7123F, 0.0F);
        wheel2 = new ModelRenderer(this, 48, 18);
        wheel2.setRotationPoint(-12.0F, 8.0F, 6.5F);
        wheel2.addBox(-2.0F, -2.0F, -1.5F, 4, 4, 3, 0.0F);
        wheel3 = new ModelRenderer(this, 48, 18);
        wheel3.setRotationPoint(12.0F, 8.0F, -6.5F);
        wheel3.addBox(-2.0F, -2.0F, -1.5F, 4, 4, 3, 0.0F);
        seat2 = new ModelRenderer(this, 60, 0);
        seat2.setRotationPoint(7.0F, 4.0F, 0.0F);
        seat2.addBox(-6.0F, -12.0F, 0.0F, 12, 12, 1, 0.0F);
        setRotateAngle(seat2, 0.0F, -1.5707F, 0.1745F);
        seat1 = new ModelRenderer(this, 60, 0);
        seat1.setRotationPoint(0.0F, 3.0F, 0.0F);
        seat1.addBox(-6.0F, -6.0F, 0.0F, 12, 12, 1, 0.0F);
        setRotateAngle(seat1, -1.5707F, 0.0F, 0.0F);
    }

    @Override
    public void render(Entity e, float f, float f1, float f2, float f3, float f4, float f5) { 
        bottom.render(f5);
        side3.render(f5);
        handle.render(f5);
        wheel4.render(f5);
        side4.render(f5);
        shaft1.render(f5);
        side2.render(f5);
        wheel1.render(f5);
        shaft2.render(f5);
        side1.render(f5);
        wheel2.render(f5);
        wheel3.render(f5);
        seat2.render(f5);
        seat1.render(f5);
    }

    private void setRotateAngle(ModelRenderer m, float x, float y, float z) {
        m.rotateAngleX = x;m.rotateAngleY = y;m.rotateAngleZ = z;
    }
}

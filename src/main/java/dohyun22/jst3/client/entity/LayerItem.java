package dohyun22.jst3.client.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.mojang.authlib.GameProfile;

import dohyun22.jst3.JustServerTweak;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerItem implements LayerRenderer<AbstractClientPlayer> {
	private static final ResourceLocation[] texs = new ResourceLocation[] {
			new ResourceLocation(JustServerTweak.MODID, "textures/entity/athame.png"),
			new ResourceLocation("minecraft:textures/items/iron_sword.png"),
			new ResourceLocation("minecraft:textures/items/gold_sword.png"),
			new ResourceLocation("minecraft:textures/items/diamond_sword.png")
	};
	private static final HashMap<UUID, Integer> list = new HashMap();
	
	static {
		addToList("63854a6a-4045-46a0-a21d-6ef068a86bbf", 0);
		addToList("0e18e043-4432-429a-a799-caf6ae9bb071", 0);
	}
  
	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
  
	@Override
	public void doRenderLayer(AbstractClientPlayer acp, float f1, float f2, float f3, float f4, float f5, float f6, float f7) {
		doRender(acp, f1, f2, f3, f4, f5, f6, f7);
	}
	
	public static void addToList(String uidStr, int num) {
		list.put(UUID.fromString(uidStr), num);
	}
  
	private static int getTextureIndex (AbstractClientPlayer pl) {
		if (pl == null) return -1;
		UUID uid = pl.getGameProfile().getId();
		if (uid == null) return -1;
		Integer i = list.get(uid);
		return i == null ? -1 : i.intValue();
	}
	
	private void doRender(AbstractClientPlayer acp, float f, float f1, float renderTick, float f2, float f3, float f4, float f5) {
		int num = getTextureIndex(acp);
		if (num >= 0 && num < texs.length && (!acp.isWearing(EnumPlayerModelParts.CAPE) || ((AbstractClientPlayer)acp).getLocationCape() == null) && !acp.isInvisible()) {
			ItemStack st = acp.inventory.armorItemInSlot(2);
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, 0.35F, 0.16F);
			if (!st.isEmpty())
				GlStateManager.translate(0.0F, acp.isSneaking() ? -0.1F : 0.0F, acp.isSneaking() ? 0.025F : 0.06F);

			if (acp.isSneaking()) {
				GlStateManager.translate(0.0F, 0.08F, 0.13F);
				GlStateManager.rotate(28.8F, 1.0F, 0.0F, 0.0F);
			}
			GlStateManager.rotate(-180.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(770, 771);
			renderItemIn3d(num);
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
	}
  
	private static void renderItemIn3d(int num)  {
		TextureManager tm = Minecraft.getMinecraft().getTextureManager();
		if (tm == null) return;
		GlStateManager.pushMatrix();
		Tessellator tessellator = Tessellator.getInstance();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.translate(-0.5F, -0.5F, 1 / 32.0F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		tm.bindTexture(texs[num]);
		float minU = 0.0f;
		float maxU = 1.0f;
		float minV = 0.0f;
		float maxV = 1.0f;
		renderItemIn2D(tessellator, maxU, minV, minU, maxV, 16, 16, 0.0625F);
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GlStateManager.popMatrix();
	}
	
	private static void renderItemIn2D(Tessellator tess, float par1, float par2, float par3, float par4, int par5, int par6, float par7) {
		BufferBuilder vb = tess.getBuffer();
    	vb.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        vb.pos(0.0D, 0.0D, 0.0D).tex((double)par1, (double)par4).normal(0.0F, 0.0F, 1.0F).endVertex();
        vb.pos(1.0D, 0.0D, 0.0D).tex((double)par3, (double)par4).normal(0.0F, 0.0F, 1.0F).endVertex();
        vb.pos(1.0D, 1.0D, 0.0D).tex((double)par3, (double)par2).normal(0.0F, 0.0F, 1.0F).endVertex();
        vb.pos(0.0D, 1.0D, 0.0D).tex((double)par1, (double)par2).normal(0.0F, 0.0F, 1.0F).endVertex();
        tess.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        vb.pos(0.0D, 1.0D, (double)(0.0F - par7)).tex((double)par1, (double)par2).normal(0.0F, 0.0F, -1.0F).endVertex();
        vb.pos(1.0D, 1.0D, (double)(0.0F - par7)).tex((double)par3, (double)par2).normal(0.0F, 0.0F, -1.0F).endVertex();
        vb.pos(1.0D, 0.0D, (double)(0.0F - par7)).tex((double)par3, (double)par4).normal(0.0F, 0.0F, -1.0F).endVertex();
        vb.pos(0.0D, 0.0D, (double)(0.0F - par7)).tex((double)par1, (double)par4).normal(0.0F, 0.0F, -1.0F).endVertex();
        tess.draw();
        float f5 = 0.5F * (par1 - par3) / (float)par5;
        float f6 = 0.5F * (par4 - par2) / (float)par6;
        vb.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        int k;
        float f7;
        float f8;

        for (k = 0; k < par5; ++k) {
            f7 = (float)k / (float)par5;
            f8 = par1 + (par3 - par1) * f7 - f5;
            vb.pos((double)f7, 0.0D, (double)(0.0F - par7)).tex( (double)f8, (double)par4).normal(-1.0F, 0.0F, 0.0F).endVertex();
            vb.pos((double)f7, 0.0D, 0.0D).tex((double)f8, (double)par4).normal(-1.0F, 0.0F, 0.0F).endVertex();
            vb.pos((double)f7, 1.0D, 0.0D).tex((double)f8, (double)par2).normal(-1.0F, 0.0F, 0.0F).endVertex();
            vb.pos((double)f7, 1.0D, (double)(0.0F - par7)).tex((double)f8, (double)par2).normal(-1.0F, 0.0F, 0.0F).endVertex();
        }

        tess.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        float f9;

        for (k = 0; k < par5; ++k) {
            f7 = (float)k / (float)par5;
            f8 = par1 + (par3 - par1) * f7 - f5;
            f9 = f7 + 1.0F / (float)par5;
            vb.pos((double)f9, 1.0D, (double)(0.0F - par7)).tex((double)f8, (double)par2).normal(1.0F, 0.0F, 0.0F).endVertex();
            vb.pos((double)f9, 1.0D, 0.0D).tex((double)f8, (double)par2).normal(1.0F, 0.0F, 0.0F).endVertex();
            vb.pos((double)f9, 0.0D, 0.0D).tex((double)f8, (double)par4).normal(1.0F, 0.0F, 0.0F).endVertex();
            vb.pos((double)f9, 0.0D, (double)(0.0F - par7)).tex((double)f8, (double)par4).normal(1.0F, 0.0F, 0.0F).endVertex();
        }

        tess.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);

        for (k = 0; k < par6; ++k) {
            f7 = (float)k / (float)par6;
            f8 = par4 + (par2 - par4) * f7 - f6;
            f9 = f7 + 1.0F / (float)par6;
            vb.pos(0.0D, (double)f9, 0.0D).tex((double)par1, (double)f8).normal(0.0F, 1.0F, 0.0F).endVertex();
            vb.pos(1.0D, (double)f9, 0.0D).tex((double)par3, (double)f8).normal(0.0F, 1.0F, 0.0F).endVertex();
            vb.pos(1.0D, (double)f9, (double)(0.0F - par7)).tex((double)par3, (double)f8).normal(0.0F, 1.0F, 0.0F).endVertex();
            vb.pos(0.0D, (double)f9, (double)(0.0F - par7)).tex((double)par1, (double)f8).normal(0.0F, 1.0F, 0.0F).endVertex();
        }

        tess.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);

        for (k = 0; k < par6; ++k) {
            f7 = (float)k / (float)par6;
            f8 = par4 + (par2 - par4) * f7 - f6;
            vb.pos(1.0D, (double)f7, 0.0D).tex((double)par3, (double)f8).normal(0.0F, 1.0F, 0.0F).endVertex();
            vb.pos(0.0D, (double)f7, 0.0D).tex((double)par1, (double)f8).normal(0.0F, 1.0F, 0.0F).endVertex();
            vb.pos(0.0D, (double)f7, (double)(0.0F - par7)).tex((double)par1, (double)f8).normal(0.0F, 1.0F, 0.0F).endVertex();
            vb.pos(1.0D, (double)f7, (double)(0.0F - par7)).tex((double)par3, (double)f8).normal(0.0F, 1.0F, 0.0F).endVertex();
        }

        tess.draw();
	}
}

package dohyun22.jst3.client.gui;

import org.lwjgl.opengl.GL11;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.container.ContainerGTI;
import dohyun22.jst3.loader.JSTCfg;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GUIGTI extends GUIBase {
	private static final ResourceLocation gui = new ResourceLocation(JustServerTweak.MODID, "textures/gui/gti.png");

	public GUIGTI(ContainerGTI r) {
		super(r);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float t, int mx, int my) {
	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(gui);
	    int x = (this.width - this.xSize) / 2;
	    int y = (this.height - this.ySize) / 2;
	    drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
	}
	
	private static String getUnlocalizedErrorMsg(byte err) {
		switch (err) {
		case 1:
			return "jst.msg.gti.duplicate";
		case 2:
			return "jst.msg.gti.toolarge";
		case 3:
			return "jst.msg.gti.capacity";
		default:
			return "jst.msg.gti.unknown";
		}
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2) {
		ContainerGTI con = (ContainerGTI) this.inventorySlots;
		if (con.err < 0) {
			this.fontRenderer.drawString(I18n.format("jst.msg.com.out"), 13, 14, 24576);
			double d = con.output / 100.0D;
			this.fontRenderer.drawString(d + " EU / " + (d * JSTCfg.RFPerEU) + " RF", 13, 24, 24576);
			this.fontRenderer.drawString(I18n.format("jst.msg.gti.size", con.size), 13, 34, 24576);
		} else {
			this.fontRenderer.drawString(I18n.format(getUnlocalizedErrorMsg(con.err)), 13, 14, 0xFF0000);
		}
	}
}

package dohyun22.jst3.client.gui;

import org.lwjgl.opengl.GL11;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.container.ContainerFusion;
import dohyun22.jst3.loader.JSTCfg;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GUIFusion extends GUIBase {
	private static final ResourceLocation gui = new ResourceLocation(JustServerTweak.MODID, "textures/gui/fusion.png");

	public GUIFusion(ContainerFusion con) {
		super(con);
		this.xSize = 176;
		this.ySize = 198;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2) {
		ContainerFusion con = (ContainerFusion) this.inventorySlots;
		if (con.complete) {
			fontRenderer.drawString((con.rfDisp ? (con.energy * JSTCfg.RFPerEU) + " RF" : con.energy + " EU") + " /", 12, 60, 0x00FFFF);
			fontRenderer.drawString(con.rfDisp ? (con.mxenergy * JSTCfg.RFPerEU) + " RF" : con.mxenergy + " EU", 12, 70, 0x00FFFF);
		} else {
			fontRenderer.drawString(I18n.format("jst.msg.multi.invalid"), 14, 58, 0xFF0000);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float pt, int mX, int mY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(gui);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		ContainerFusion con = (ContainerFusion) inventorySlots;
		if (con.energy > 0) {
			int i = (int) ((Math.min(Math.max(con.energy, 0), con.mxenergy) * 136L) / Math.max(con.mxenergy, 1L));
			drawTexturedModalRect(x + 9, y + 82, 0, 198, i, 5);
		}
		if (con.rfDisp) drawTexturedModalRect(x + 151, y + 25, 176, 18, 18, 18);
		if (con.state > 0) drawTexturedModalRect(x + 154, y + 98, 176, con.state == 2 ? 43 : 36, 12, 7);
		if (Loader.isModLoaded("jei")) drawTexturedModalRect(x + 151, y + 7, 176, 0, 18, 18);
	}

	@Override
	public void drawScreen(int mX, int mY, float pt) {
		super.drawScreen(mX, mY, pt);
		ContainerFusion con = (ContainerFusion) inventorySlots;
		if (!Loader.isModLoaded("jei") && isPointInRegion(152, 8, 16, 16, mX, mY))
			drawHoveringText(I18n.format("jst.compat.jei.missing"), mX, mY);
		if (isPointInRegion(152, 26, 16, 16, mX, mY))
			drawHoveringText(I18n.format("jst.msg.com.eu_rf"), mX, mY);
		if (con.state > 0 && isPointInRegion(157, 95, 12, 7, mX, mY))
			drawHoveringText(I18n.format("jst.compat.jei.fusion." + (con.state == 2 ? "neut" : "prot")), mX, mY);
		if (!con.getSlot(0).getHasStack() && isPointInRegion(58, 93, 18, 18, mX, mY))
			drawHoveringText(I18n.format("jst.msg.fusion.breeder"), mX, mY);
	}
}

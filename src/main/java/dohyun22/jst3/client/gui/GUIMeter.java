package dohyun22.jst3.client.gui;

import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.container.ContainerMeter;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GUIMeter extends GUIBase {
	private static final ResourceLocation gui = new ResourceLocation(JustServerTweak.MODID, "textures/gui/emeter.png");

	public GUIMeter(Container con) {
		super(con);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(gui);
	    int x = (width - xSize) / 2;
	    int y = (height - ySize) / 2;
	    drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	    if (((ContainerMeter) inventorySlots).displayRF)
	    	drawTexturedModalRect(x + 151, y + 31, 176, 0, 18, 18);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2){
		ContainerMeter con = (ContainerMeter) inventorySlots;
		fontRenderer.drawString(I18n.format("jst.msg.meter.total", con.displayRF ? JSTUtils.formatNum(con.sum * JSTCfg.RFPerEU) + " RF" : JSTUtils.formatNum(con.sum) + " EU"), 13, 18, 24576);
		fontRenderer.drawString(I18n.format("jst.msg.meter.avg", con.displayRF ? JSTUtils.formatNum(con.avg * JSTCfg.RFPerEU) + " RF" : JSTUtils.formatNum(con.avg) + " EU"), 13, 32, 24576);
		fontRenderer.drawString(I18n.format("jst.msg.meter.amp", JSTUtils.formatNum(con.amp)), 13, 46, 24576);
	}
	
	@Override
    public void drawScreen(int mX, int mY, float pt) {
		super.drawScreen(mX, mY, pt);
		if (isPointInRegion(152, 10, 16, 16, mX, mY))
			this.drawHoveringText(I18n.format("jst.msg.com.re"), mX, mY);
		if (isPointInRegion(152, 32, 16, 16, mX, mY))
			this.drawHoveringText(I18n.format("jst.msg.com.eu_rf"), mX, mY);
	}
}

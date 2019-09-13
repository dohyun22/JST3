package dohyun22.jst3.client.gui;

import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.container.ContainerRPlanner;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GUIRPlanner extends GUIBase {
	private static final ResourceLocation gui = new ResourceLocation(JustServerTweak.MODID, "textures/gui/rplanner.png");

	public GUIRPlanner(Container con) {
		super(con);
		xSize = 211;
		ySize = 245;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mX, int mY) {
	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(gui);
	    int x = (width - xSize) / 2;
	    int y = (height - ySize) / 2;
	    drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	    ContainerRPlanner con = (ContainerRPlanner) inventorySlots;
	    if (con.boom > 0) {
	    	drawTexturedModalRect(x + 189, y + 51, 212, 18, 18, 18);
	    	for (int n = 0; n < 4; n++)
	    		drawTexturedModalRect(x + 189, y + 87 + n * 18, 212, 18, 18, 18);
	    } else if (con.active) {
	    	drawTexturedModalRect(x + 189, y + 51, 212, 0, 18, 18);
	    }
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2){
		ContainerRPlanner con = (ContainerRPlanner) inventorySlots;
		int c = 24576;
		if (con.boom <= 0) {
			fontRenderer.drawString(I18n.format("jst.msg.rplanner.time", con.timer), 11, 11, c);
			fontRenderer.drawString(I18n.format("jst.msg.rplanner.simspeed", con.speed * 20), 11, 22, c);
			fontRenderer.drawString(I18n.format("jst.msg.rplanner.heat", con.heat, con.mxheat), 11, 33, c);
			
			fontRenderer.drawString(I18n.format("jst.msg.meter.total", con.eu), 100, 11, c);
			fontRenderer.drawString(I18n.format("jst.msg.com.out") + " " + (con.output / 10.0F) + " EU/t", 100, 22, c);
		} else {
			c = 0xFF0000;
			fontRenderer.drawString(I18n.format("jst.msg.rplanner.explode", con.timer), 11, 11, c);
			fontRenderer.drawString(I18n.format("jst.msg.rplanner.explode2", con.boom / 10.0F), 11, 33, c);
		}
	}
	
	@Override
    public void drawScreen(int mX, int mY, float pt) {
		super.drawScreen(mX, mY, pt);
		List<String> txt = null;
		ContainerRPlanner crp = (ContainerRPlanner)inventorySlots;
		for (int n = 0; n < 5; n++) {
			if (n > 0 && crp.boom > 0) break;
			if (isPointInRegion(190, 70 + n * 18, 16, 16, mX, mY)) {
				int sf = n % 2 == 0 ? 2 : 1;
				if (n == 0)
					txt = Arrays.<String>asList(new String[] {I18n.format("jst.msg.com.re"), I18n.format("jst.msg.rplanner.clear")});
				else if (n == 1 || n == 2)
					txt = JSTUtils.getListFromTranslation("jst.msg.rplanner.sc" + sf);
				else
					txt = JSTUtils.getListFromTranslation("jst.msg.rplanner.t" + sf);
			}
		}
		if (isPointInRegion(6, 74, 16, 16, mX, mY) && !crp.getSlot(61).getHasStack()) txt = JSTUtils.getListFromTranslation("jst.msg.com.data");
		if (txt != null) drawHoveringText(txt, mX, mY);
    }
}

package dohyun22.jst3.client.gui;

import org.lwjgl.opengl.GL11;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.container.ContainerCircuitResearch;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class GUICircuitResearch extends GUIBase {
	private static final ResourceLocation gui = new ResourceLocation(JustServerTweak.MODID,
			"textures/gui/circuit_research.png");

	public GUICircuitResearch(Container c) {
		super(c);
		xSize = 215;
		ySize = 222;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2) {
		ContainerCircuitResearch con = (ContainerCircuitResearch) this.inventorySlots;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float t, int mx, int my) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(gui);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		
		ContainerCircuitResearch con = (ContainerCircuitResearch) this.inventorySlots;
		for(int i = 0; i < con.listOfGame.length; i++) {
			byte value = con.listOfGame[i];
			int xS = 10 + i * 14;
			int yS = 10 + (i % con.row) * 14;
			switch(value) {
			case (byte)1:
				this.drawTexturedModalRect(xS, yS, 1, 224, 12, 12);
				break;
			default:
				break;
			}
		}
	}
	// 184 25

	@Override
	public void drawScreen(int mX, int mY, float pt) {
		super.drawScreen(mX, mY, pt);
		/*
		 * if (isPointInRegion(71, 34, 16, 16, mX, mY))
		 * this.drawHoveringText(I18n.format("jst.msg.laser.mine"), mX, mY); if
		 * (isPointInRegion(89, 34, 16, 16, mX, mY))
		 * this.drawHoveringText(I18n.format("jst.msg.laser.autosmelt"), mX, mY);
		 */
	}
}

package dohyun22.jst3.client.gui;

import java.awt.Polygon;
import java.io.IOException;

import org.lwjgl.opengl.GL11;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.container.ContainerCircuitResearch;
import dohyun22.jst3.tiles.machine.MT_CircuitResearchMachine;
import dohyun22.jst3.tiles.machine.MT_CircuitResearchMachine.MiniGameTile;
import dohyun22.jst3.tiles.machine.MT_CircuitResearchMachine.Wire;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class GUICircuitResearch extends GUIBase {
	private static final ResourceLocation gui = new ResourceLocation(JustServerTweak.MODID,
			"textures/gui/circuit_research.png");

	public GUICircuitResearch(Container c) {
		super(c);
		xSize = 217;
		ySize = 225;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mx, int my) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(gui);
		ContainerCircuitResearch con = (ContainerCircuitResearch) this.inventorySlots;
		for (int i = 0; i < con.listOfGame.length; i++) {
			byte value = con.listOfGame[i];
			MiniGameTile mgt = MiniGameTile.getTile(value);
			if (mgt != null)
				mgt.draw(this, 10 + 14 * (i % con.row), 10 + 14 * (i / con.row));
		}

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float t, int mx, int my) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(gui);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

		ContainerCircuitResearch con = (ContainerCircuitResearch) this.inventorySlots;

	}
	// 184 25

	@Override
	protected void mouseClicked(int mX, int mY, int b) throws IOException {
		if (isPointInRegion(10, 10, 167, 125, mX, mY)) {
			int u = mX - 10 - guiLeft, v = mY - 10 - guiTop;
			int px = u / 14;
			int py = v / 14;
			int dir = 1000;
			u %= 14;
			v %= 14;
			Polygon p = new Polygon();
			p.addPoint(0, 14);
			p.addPoint(7, 7);
			p.addPoint(14, 14);
			if (p.contains(u, v))
				dir = 2000;
			p = new Polygon();
			p.addPoint(0, 0);
			p.addPoint(7, 7);
			p.addPoint(0, 14);
			if (p.contains(u, v))
				dir = 3000;
			p = new Polygon();
			p.addPoint(14, 0);
			p.addPoint(7, 7);
			p.addPoint(14, 14);
			if (p.contains(u, v))
				dir = 4000;
			handleMouseClick(null, px + py * MT_CircuitResearchMachine.ROW + dir, b, ClickType.QUICK_CRAFT);
		} else
			super.mouseClicked(mX, mY, b);
	}

	public int slotx = -1;
	public int sloty = -1;

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		/*
		ContainerCircuitResearch con = (ContainerCircuitResearch) this.inventorySlots;
		if (clickedMouseButton == 0) {
			int u = mouseX - 10 - guiLeft, v = mouseY - 10 - guiTop;
			int px = u / 14;
			int py = v / 14;
			if (slotx == -1 || sloty == -1) {
				slotx = px;
				sloty = py;
				return;//start
			} else if(slotx == px && sloty == py){
				return;
			} else{
				int alpha = slotx - px;
				int beta = sloty - py;
				int pos = con.row * sloty + slotx;
				/*if (slotx < 0) {
					con.listOfGame[pos] = Wire.getTile(3).id;
				} else if(slotx > 0){
					con.listOfGame[pos] = Wire.getTile(4).id;
				}else {
					if (sloty < 0) {
						con.listOfGame[pos] = Wire.getTile(1).id;
					} else if(sloty > 0){
						con.listOfGame[pos] = Wire.getTile(2).id;
					}
				}
				int dir = 1000;
				Polygon p = new Polygon();
				p.addPoint(0, 14);
				p.addPoint(7, 7);
				p.addPoint(14, 14);
				if (p.contains(u, v))
					dir = 2000;
				p = new Polygon();
				p.addPoint(0, 0);
				p.addPoint(7, 7);
				p.addPoint(0, 14);
				if (p.contains(u, v))
					dir = 3000;
				p = new Polygon();
				p.addPoint(14, 0);
				p.addPoint(7, 7);
				p.addPoint(14, 14);
				if (p.contains(u, v))
					dir = 4000;
				handleMouseClick(null, px + py * MT_CircuitResearchMachine.ROW + dir, clickedMouseButton, ClickType.QUICK_CRAFT);
			}
			slotx = px;
			sloty = py;
		}*/
	}
}
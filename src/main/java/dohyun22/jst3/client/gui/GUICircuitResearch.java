package dohyun22.jst3.client.gui;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.container.ContainerCircuitResearch;
import dohyun22.jst3.tiles.test.MT_CircuitResearchMachine;
import dohyun22.jst3.tiles.test.MT_CircuitResearchMachine.MiniGameTile;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class GUICircuitResearch extends GUIBase {
	private static final ResourceLocation gui = new ResourceLocation(JustServerTweak.MODID, "textures/gui/circuit_research.png");

	public GUICircuitResearch(Container c) {
		super(c);
		xSize = 217;
		ySize = 225;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(gui);
		ContainerCircuitResearch con = (ContainerCircuitResearch) this.inventorySlots;
		for (int i = 0; i < con.listOfGame.length; i++) {
			byte value = con.listOfGame[i];
			MiniGameTile mgt = MiniGameTile.getTile(value);
			if (mgt != null) mgt.draw(this, 10 + 14 * (i % con.row), 10 + 14 * (i / con.row));
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

	public void drawTexturedModalRect(int x1, int y1, int x2, int y2, int textureX1, int textureY1, int textureX2,
			int textureY2) {
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos((double) (x1), (double) (y2), (double) this.zLevel)
				.tex((double) ((float) (textureX1) * 0.00390625F), (double) ((float) (textureY2) * 0.00390625F))
				.endVertex();
		bufferbuilder.pos((double) (x2), (double) (y2), (double) this.zLevel)
				.tex((double) ((float) (textureX2) * 0.00390625F), (double) ((float) (textureY2) * 0.00390625F))
				.endVertex();
		bufferbuilder.pos((double) (x2), (double) (y1), (double) this.zLevel)
				.tex((double) ((float) (textureX2) * 0.00390625F), (double) ((float) (textureY1) * 0.00390625F))
				.endVertex();
		bufferbuilder.pos((double) (x1), (double) (y1), (double) this.zLevel)
				.tex((double) ((float) (textureX1) * 0.00390625F), (double) ((float) (textureY1) * 0.00390625F))
				.endVertex();
		tessellator.draw();
	}

	@Override
	protected void mouseClicked(int mX, int mY, int b) throws IOException {
		if (isPointInRegion(10, 10, 167, 125, mX, mY)) {
			int px = (mX - 10 - guiLeft) / 14;
			int py = (mY - 10 - guiTop) / 14;
			handleMouseClick(null, px + py * MT_CircuitResearchMachine.row + 1000, b, ClickType.QUICK_CRAFT);
		} else super.mouseClicked(mX, mY, b);
	}
}

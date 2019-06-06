package dohyun22.jst3.client.gui;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.api.FineDustCapability;
import dohyun22.jst3.container.ContainerDust;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GUIDust extends GUIBase {
	private static final ResourceLocation gui = new ResourceLocation(JustServerTweak.MODID, "textures/gui/dust.png");

	public GUIDust(Container con) {
		super(con);
		xSize = 176;
		ySize = 176;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2){
		ContainerDust con = (ContainerDust) inventorySlots;
		fontRenderer.drawString("[" + (con.cx * 16) + "," + (con.cz * 16) + "]: " + FineDustCapability.toMicrogram(con.dust) + " \u338D/\u33A5", 40, 150, 0x00FFFF);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float t, int mx, int my) {
	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(gui);
	    int x = (width - xSize) / 2;
	    int y = (height - ySize) / 2;
	    drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	    
	    ContainerDust con = (ContainerDust) inventorySlots;
	    int px, py;
	    for (px = 0; px < ContainerDust.mapSz; px++) {
		    for (py = 0; py < ContainerDust.mapSz; py++) {
		    	try {
		    		byte c = con.dustmap[px + py * ContainerDust.mapSz];
		    		drawTexturedModalRect(x + 39 + px * 9, y + 23 + py * 9, 176, 0 + 8 * c, 8, 8);
		    	} catch (Exception e) {}
		    }
	    }
	    px = con.sp % ContainerDust.mapSz;
	    py = con.sp / ContainerDust.mapSz;
	    drawTexturedModalRect(x + 37 + px * 9, y + 21 + py * 9, 176, 128, 12, 12);
	}

	@Override
	protected void mouseClicked(int mX, int mY, int b) throws IOException {
		super.mouseClicked(mX, mY, b);
		if (isPointInRegion(38, 22, 99, 99, mX, mY)) {
			int px = (mX - 38 - guiLeft) / 9;
			int py = (mY - 22 - guiTop) / 9;
			handleMouseClick(null, px + py * ContainerDust.mapSz, 0, ClickType.QUICK_CRAFT);
		}
	}
}

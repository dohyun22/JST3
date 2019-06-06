package dohyun22.jst3.client.gui;

import org.lwjgl.opengl.GL11;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.container.ContainerMulti;
import dohyun22.jst3.tiles.multiblock.MT_Multiblock;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GUIMulti extends GUIBase {
	private static final ResourceLocation gui = new ResourceLocation(JustServerTweak.MODID, "textures/gui/multi.png");
	private final byte[][][] structure;

	public GUIMulti(Container c, byte[][][] str) {
		super(c);
		structure = str;
		this.xSize = 176;
		this.ySize = 186;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2) {
		ContainerMulti con = ((ContainerMulti)inventorySlots);
		if (!con.te.hasValidMTE() || !(con.te.mte instanceof MT_Multiblock)) return;
		((MT_Multiblock)con.te.mte).doDisplay(con.state, con.data, fontRenderer);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(gui);
	    int x = (this.width - this.xSize) / 2;
	    int y = (this.height - this.ySize) / 2;
	    drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
	    int sx = 14;
	    int sy = 14;
		if (structure != null) {
			try {
				int between = 5 + structure[0][0].length * 5;
				for (int py = 0; py < structure.length; py++) {
					for (int pz = 0; pz < structure[0].length; pz++) {
						for (int px = 0; px < structure[0][0].length; px++) {
							byte val = structure[py][pz][px];
							if (val <= 0)
								continue;
							int off = 176 + (val - 1) * 4;
							drawTexturedModalRect(x + sx + (between * py) + (px * 5), y + sy + pz * 5, off, 0, 4, 4);
						}
					}
				}
			} catch (Exception e) {
				this.fontRenderer.drawString(e.toString(), 14, 20, 0xFF0000);
			}
		}
	}

}

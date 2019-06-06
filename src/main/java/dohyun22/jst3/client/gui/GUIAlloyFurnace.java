package dohyun22.jst3.client.gui;

import org.lwjgl.opengl.GL11;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.container.ContainerAlloyFurnace;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GUIAlloyFurnace extends GUIBase {
	private static final ResourceLocation gui = new ResourceLocation(JustServerTweak.MODID, "textures/gui/alloy.png");

	public GUIAlloyFurnace(Container con) {
		super(con);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float pt, int mx, int my) {
	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(gui);
	    int x = (width - xSize) / 2;
	    int y = (height - ySize) / 2;
	    drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

	    ContainerAlloyFurnace con = (ContainerAlloyFurnace) inventorySlots;
        if (con.burnTime > 0) {
        	int i = con.currentBurnTime;
        	if (i == 0) i = 200;
            int s = con.burnTime * 13 / i;
            drawTexturedModalRect(x + 52, y + 36 + 13 - s, 176, 13 - s, 14, s + 1);
        }

        int s = con.totalCookTime != 0 && con.cookTime != 0 ? con.cookTime * 24 / con.totalCookTime : 0;
        this.drawTexturedModalRect(x + 79, y + 34, 176, 14, s + 1, 16);
	}
}

package dohyun22.jst3.client.gui;

import org.lwjgl.opengl.GL11;

import dohyun22.jst3.JustServerTweak;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

@SideOnly(Side.CLIENT)
public class GUIECart extends GUIBase {
	private static final ResourceLocation gui = new ResourceLocation(JustServerTweak.MODID, "textures/gui/ecart.png");

	public GUIECart(Container c) {
		super(c);
		xSize = 148;
		ySize = 34;
	}
	
	@Override
    public void initGui() {
        super.initGui();
		guiTop = height - ySize - 32;
    }

	@Override
	protected void drawGuiContainerBackgroundLayer(float t, int mx, int my) {
	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(gui);
	    int x = (width - xSize) / 2;
	    int y = height - ySize - 32;
	    drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}

	@Override
	public void drawDefaultBackground() {}
}

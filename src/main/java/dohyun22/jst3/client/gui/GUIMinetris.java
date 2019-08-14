package dohyun22.jst3.client.gui;

import java.io.IOException;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.utils.JSTSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GUIMinetris extends GUIBase {
	private static final ResourceLocation gui = new ResourceLocation(JustServerTweak.MODID, "textures/gui/mini.png");
	private static final int X = 10, Y = 20; 
	private boolean[][] map = new boolean[X][Y];
	private final byte[][][][] pieces = {
			{//I
				{{0,1},{1,1},{2,1},{3,1}},
				{{1,0},{1,1},{1,2},{1,3}},
				{{0,1},{1,1},{2,1},{3,1}},
				{{1,0},{1,1},{1,2},{1,3}}
			},
			{//J
				{{0,1},{1,1},{2,1},{2,0}},
				{{1,0},{1,1},{1,2},{2,2}},
				{{0,1},{1,1},{2,1},{0,2}},
				{{1,0},{1,1},{1,2},{0,0}}
			},
			{//JM
				{{0,1},{1,1},{2,1},{2,2}},
				{{1,0},{1,1},{1,2},{0,2}},
				{{0,1},{1,1},{2,1},{0,0}},
				{{1,0},{1,1},{1,2},{2,0}}
			},
			{//O
				{{0,0},{0,1},{1,0},{1,1}},
				{{0,0},{0,1},{1,0},{1,1}},
				{{0,0},{0,1},{1,0},{1,1}},
				{{0,0},{0,1},{1,0},{1,1}}
			},
			{//S
				{{1,0},{2,0},{0,1},{1,1}},
				{{0,0},{0,1},{1,1},{1,2}},
				{{1,0},{2,0},{0,1},{1,1}},
				{{0,0},{0,1},{1,1},{1,2}}
			},
			{//T
				{{1,0},{0,1},{1,1},{2,1}},
				{{1,0},{0,1},{1,1},{1,2}},
				{{0,1},{1,1},{2,1},{1,2}},
				{{1,0},{1,1},{2,1},{1,2}}
			},
			{//SM
				{{0,0},{1,0},{1,1},{2,1}},
				{{1,0},{0,1},{1,1},{0,2}},
				{{0,0},{1,0},{1,1},{2,1}},
				{{1,0},{0,1},{1,1},{0,2}}
			},
			{//SP
				{{0,0}},
				{{0,0}},
				{{0,0}},
				{{0,0}}
			},
			{//D
				{{0,1},{1,1}},
				{{1,0},{1,1}},
				{{2,1},{1,1}},
				{{1,2},{1,1}}
			},
			{//+
				{{1,0},{0,1},{1,1},{2,1},{1,2}},
				{{1,0},{0,1},{1,1},{2,1},{1,2}},
				{{1,0},{0,1},{1,1},{2,1},{1,2}},
				{{1,0},{0,1},{1,1},{2,1},{1,2}}
			},
			{//F
				{{1,0},{2,0},{0,1},{1,1},{1,2}},
				{{1,0},{0,1},{1,1},{2,1},{2,2}},
				{{1,0},{1,1},{2,1},{0,2},{1,2}},
				{{0,0},{0,1},{1,1},{2,1},{1,2}}
			},
			{//FM
				{{1,0},{1,1},{1,2},{0,0},{2,1}},
				{{0,1},{1,1},{2,1},{1,2},{2,0}},
				{{1,0},{1,1},{1,2},{0,1},{2,2}},
				{{0,1},{1,1},{2,1},{0,2},{1,0}}
			},
			{//U
				{{0,0},{2,0},{0,1},{1,1},{2,1}},
				{{1,0},{2,0},{1,1},{1,2},{2,2}},
				{{0,1},{1,1},{2,1},{0,2},{2,2}},
				{{0,0},{1,0},{1,1},{0,2},{1,2}}
			},
			{//W
				{{0,0},{0,1},{1,1},{1,2},{2,2}},
				{{1,0},{2,0},{0,1},{1,1},{0,2}},
				{{0,0},{1,0},{1,1},{2,1},{2,2}},
				{{2,0},{1,1},{2,1},{0,2},{1,2}}
			},
			{//V
				{{0,0},{0,1},{0,2},{1,2},{2,2}},
				{{0,0},{0,1},{0,2},{1,0},{2,0}},
				{{0,0},{1,0},{2,0},{2,1},{2,2}},
				{{2,0},{2,1},{2,2},{1,2},{0,2}}
			},
			{//P
				{{1,0},{2,0},{1,1},{2,1},{1,2}},
				{{0,1},{1,1},{2,1},{1,2},{2,2}},
				{{1,0},{0,1},{1,1},{0,2},{1,2}},
				{{0,0},{1,0},{0,1},{1,1},{2,1}}
			},
			{//PR
				{{0,0},{1,0},{0,1},{1,1},{1,2}},
				{{1,0},{2,0},{0,1},{1,1},{2,1}},
				{{1,0},{1,1},{2,1},{1,2},{2,2}},
				{{0,1},{1,1},{2,1},{0,2},{1,2}}
			}
	};
    private byte[] theme = new byte[] {22,-3,17,18,20,-3,18,17,15,-3,15,18,22,-3,20,18,17,-5,18,20,-3,22,-3,18,-3,15,-3,15,-9,20,-3,23,15,-3,13,23,22,-5,18,22,-3,20,18,17,-3,17,18,20,-3,22,-3,18,-3,15,-3,15,-7,22,-3,17,18,20,-3,18,17,15,-3,15,18,22,-3,20,18,17,-5,18,20,-3,22,-3,18,-3,15,-3,15,-9,20,-3,23,15,-3,13,23,22,-5,18,22,-3,20,18,17,-3,17,18,20,-3,22,-3,18,-3,15,-3,15,-7,10,-7,6,-7,8,-7,5,-7,6,-7,3,-7,2,-7,5,-7,10,-7,6,-7,8,-7,5,-7,6,-3,10,-3,15,-7,14,-15};
	private float tick;
    private int note, nw, gameTick, score, rot, current, pieceX, pieceY, nextRot, next, pentomino;
    private boolean gameover;

	public GUIMinetris(Container con) {
		super(con);
		xSize = 133;
		ySize = 184;
	}

	@Override
	public void initGui() {
		super.initGui();
		int x = (width - xSize) / 2, y = (height - ySize) / 2;
		buttonList.add(new GuiButton(0, x + 28, y + 159, 16, 20, "<<"));
		buttonList.add(new GuiButton(1, x + 49, y + 159, 16, 20, ">>"));
		buttonList.add(new GuiButton(2, x + 70, y + 159, 16, 20, "\u21B7"));
		buttonList.add(new GuiButton(3, x + 91, y + 159, 16, 20, "\u2193"));
		load();
	}

	@Override
	protected void actionPerformed(GuiButton gb) throws IOException {
		if (gameover) {
			load();
			return;
		}
		switch (gb.id) {
		case 0: move(-1); break;
		case 1: move(1); break;
		case 2: rotate(); break;
		case 3: drop(true);
		}
	}

	@Override
    public void drawScreen(int mx, int my, float pt) {
		if (tick != pt) {
			update();
			gameTick++;
			tick = pt;
		}
		super.drawScreen(mx, my, pt);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float pt, int mx, int my) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(gui);
		int x = (width - xSize) / 2, y = (height - ySize) / 2;
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		if (gameover) drawTexturedModalRect(x + 92, y + 70, 133, 6, 23, 11);
		int p = Math.abs(score);
		for (int n = 0; n < 6; n++) {
			int d = p % 10;
			drawTexturedModalRect(x + 116 - n * 6, y + 20, 133 + d * 5, 17, 5, 7);
			p /= 10;
			if (p <= 0) break;
		}
		for (int px = 0; px < X; px++) for (int py = 0; py < Y; py++) if (map[px][py])
			drawTexturedModalRect(x + 13 + px * 7, y + 13 + py * 7, 133, 0, 6, 6);
		if (current >= 0) for (byte[] c : pieces[current][rot])
			drawTexturedModalRect(x + 13 + (c[0] + pieceX) * 7, y + 13 + (c[1] + pieceY) * 7, 133, 0, 6, 6);
		if (next >= 0) for (byte[] c : pieces[next][nextRot])
			drawTexturedModalRect(x + 90 + c[0] * 7, y + 41 + c[1] * 7, 133, 0, 6, 6);
	}

	@Override
	protected void keyTyped(char c, int k) throws IOException {
        if (k == 1 || mc.gameSettings.keyBindInventory.isActiveAndMatches(k)) {
            mc.player.closeScreen();
            return;
        }
		if (gameover) {
			load();
			return;
		}
		if (k == 57 || k == 17 || k == 200)
			rotate();
		else if (k == 31 || k == 208)
			drop(true);
		else if (k == 30 || k == 203)
			move(-1);
		else if (k == 32 || k == 205)
			move(1);
	}

	private void load() {
		map = new boolean[X][Y];
		gameover = false;
		note = 0;
		nw = 0;
		gameTick = 0;
		score = 0;
		rot = 0;
		current = -1;
		nextRot = 0;
		next = -1;
		newPiece();
	}

	private void update() {
		if (gameover) return;
		if (gameTick % 2 == 0) {
			if (nw > 0) {
				nw--;
			} else {
				byte n = theme[note];
				note = (note + 1) % theme.length;
				if (n >= 0) {
					float p = (float)Math.pow(2.0D, (n - 12) / 12.0D);
					playSound(SoundEvents.BLOCK_NOTE_HARP, p);
					nw = 1;
				} else {
					nw = -n;
				}
			}
		}
		if (gameTick % 20 == 0) drop(false);
	}

	private boolean collide(int x, int y, int rotation) {
		for (byte[] p : pieces[current][rotation]) {
			int px = p[0] + x, py = p[1] + y;
			if (px < 0 || py < 0 || px >= X || py >= Y || map[px][py])
				return true;
		}
		return false;
	}

	private void rotate() {
		int nr = (rot + 1) % 4;
		if (!collide(pieceX, pieceY, nr)) rot = nr;
	}

	private void move(int i) {
		if (!collide(pieceX + i, pieceY, rot)) pieceX += i;	
	}

	private void drop(boolean flag) {
		int n = flag ? Y : 1;
		for (int m = 0; m < n; m++)
			if (!collide(pieceX, pieceY + 1, rot))
				pieceY += 1;
			else {
				fixPiece();
				break;
			}
	}

	private void fixPiece() {
		for (byte[] p : pieces[current][rot])
			map[pieceX + p[0]][pieceY + p[1]] = true;
		clearRows();
		newPiece();
	}

	private void newPiece() {
		pieceX = X / 2 - 1; pieceY = 0;
		Random r = new Random();
		int a = score < 10000 || pentomino > 0 ? 9 : pieces.length;
		if (pentomino > 0) pentomino--;
		if (current < 0) {
			current = r.nextInt(a);
			rot = r.nextInt(4);
		} else {
			current = next;
			rot = nextRot;
		}
		next = r.nextInt(a);
		if (next >= 9)
			pentomino = score < 20000 ? 4 : 2;
		nextRot = r.nextInt(4);
		if (collide(pieceX, pieceY, rot)) {
			playSound(JSTSounds.LEVEL, 1.0F);
			gameover = true; 
		}
	}

	private void clearRows() {
		boolean gap;
		int clear = 0;
		for (int j = 19; j > 0; j--) {
			gap = false;
			for (int i = 0; i < 10; i++) {
				if (!map[i][j]) {
					gap = true;
					break;
				}
			}
			if (!gap) {
				for (int k = j - 1; k > 0; k--)
					for (int l = 0; l < 10; l++)
						map[l][k+1] = map[l][k];
				j += 1;
				clear += 1;
			}
		}
		SoundEvent se = SoundEvents.BLOCK_STONE_PLACE;
		if (clear > 0) {
			se = clear < 2 ? SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP : JSTSounds.BONUS;
			score += (clear + clear - 1) * 100;
		}
		playSound(se, 1.0F);
	}
}

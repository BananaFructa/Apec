package Apec.Components.Gui.Menu.CustomizationMenu;

import Apec.ApecMain;
import Apec.Utils.ApecUtils;
import Apec.Components.Gui.GuiIngame.GUIComponent;
import Apec.Settings.SettingID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.Sys;
import org.lwjgl.util.vector.Vector2f;

import java.util.List;

public class CustomizationGuiButton extends GuiButton {

    class SnapData {
        public boolean xSnap = false,ySnap = false;
        public Vector2f vec = new Vector2f(0,0);
    }

    Vector2f initialPos;

    List<Integer> xSnapPoint, ySnapPoints;
    GUIComponent guiComponent;
    public boolean isUserDragging = false;
    private Vector2f fineTuned = new Vector2f(0,0);
    private boolean LockY = false;

    public CustomizationGuiButton (GUIComponent component,List<Integer> xSnapPoint, List<Integer> ySnapPoints) {
        super(0,0,0,1,1,"");
        this.guiComponent = component;
        this.xSnapPoint = xSnapPoint;
        this.ySnapPoints = ySnapPoints;
    }
    public CustomizationGuiButton (GUIComponent component,List<Integer> xSnapPoint, List<Integer> ySnapPoints,boolean LockY) {
        this(component,xSnapPoint,ySnapPoints);
        this.LockY = LockY;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            ScaledResolution sr = new ScaledResolution(mc);
            Vector2f v, rv;

            v = this.guiComponent.getCurrentAnchorPoint();
            rv = this.guiComponent.getCurrentBoundingPoint();

            if (v.x < rv.x && v.y < rv.y) {
                this.xPosition = (int) v.x;
                this.yPosition = (int) v.y;
                this.width = (int) (rv.x - v.x);
                this.height = (int) (rv.y - v.y);
            } else if (v.x > rv.x && v.y > rv.y) {
                this.xPosition = (int) rv.x;
                this.yPosition = (int) rv.y;
                this.width = (int) (v.x - rv.x);
                this.height = (int) (v.y - rv.y);
            } else if (v.x > rv.x && v.y < rv.y) {
                this.xPosition = (int) rv.x;
                this.yPosition = (int) v.y;
                this.width = (int) (v.x - rv.x);
                this.height = (int) (rv.y - v.y);
            } else if (v.x < rv.x && v.y > rv.y) {
                this.xPosition = (int) v.x;
                this.yPosition = (int) rv.y;
                this.width = (int) (rv.x - v.x);
                this.height = (int) (v.y - rv.y);
            }
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            
            if (this.hovered)
                drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, 0x3adddddd);
            else
                drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, 0x6adddddd);

            this.mouseDragged(mc, mouseX, mouseY);

            if (isUserDragging) {
                Vector2f anchor;

                anchor = ApecUtils.addVec(this.guiComponent.getTotalParentOffset(), this.guiComponent.getAnchorPointPosition());

                boolean isSnappedToPositionX = false;
                boolean isSnappedToPositionY = false;

                SnapData SnapResult = IsSnapped(mouseX, mouseY, sr, anchor);
                isSnappedToPositionX = SnapResult.xSnap;
                isSnappedToPositionY = SnapResult.ySnap;


                Vector2f Result = new Vector2f(
                        isSnappedToPositionX ? SnapResult.vec.x + fineTuned.x : mouseX - anchor.x + fineTuned.x + initialPos.x,
                        (isSnappedToPositionY ? SnapResult.vec.y + fineTuned.y : mouseY - anchor.y + fineTuned.y + initialPos.y) * (LockY ? 0 : 1)
                );
                this.guiComponent.setDeltaPosition(Result);

            } else {
                this.fineTuned = new Vector2f(0, 0);
            }
        }
    }

    private SnapData IsSnapped (int mouseX, int mouseY, ScaledResolution sr, Vector2f anchor) {
        Vector2f vec = new Vector2f(0,0);
        boolean isSnappedToPositionX = false,isSnappedToPositionY = false;
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SNAP_IN_EDITING)) {
            for (Integer i : xSnapPoint) {
                if (Math.abs(i - mouseX - initialPos.x) < 5) {
                    isSnappedToPositionX = true;
                    vec = new Vector2f(i - anchor.x, mouseY - anchor.y);
                    drawRect(i, 0, i + 1, sr.getScaledHeight(), 0xffff0000);
                    break;
                }
            }

            if (!LockY) {
                for (Integer i : ySnapPoints) {
                    if (Math.abs(i - mouseY - initialPos.y) < 5) {
                        isSnappedToPositionY = true;
                        vec = new Vector2f(isSnappedToPositionX ? vec.x : mouseX - anchor.x, i - anchor.y);
                        drawRect(0, i, sr.getScaledWidth(), i + 1, 0xffff0000);
                        break;
                    }
                }
            }
        }
        SnapData sd = new SnapData();
        sd.xSnap = isSnappedToPositionX;
        sd.ySnap = isSnappedToPositionY;
        sd.vec = vec;
        return sd;
    }

    public void userStartedDragging(int mouseX,int mouseY) {
        initialPos = this.guiComponent.getCurrentAnchorPoint();
        initialPos.x -= mouseX;
        initialPos.y -= mouseY;
        this.isUserDragging = true;
    }

    public void setDeltaToZero() {
        this.guiComponent.setDeltaPosition(new Vector2f(0, 0));
    }

    public void fineRepositioning(Vector2f v) {
        if (this.isUserDragging) this.fineTuned = ApecUtils.addVec(fineTuned,v);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        super.mouseReleased(mouseX, mouseY);
        this.isUserDragging = false;
    }
}

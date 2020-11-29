package Apec.Components.Gui.Menu.CustomizationMenu;

import Apec.ApecMain;
import Apec.ApecUtils;
import Apec.Components.Gui.GuiIngame.GuiElements.GUIComponent;
import Apec.Settings.SettingID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.apache.commons.lang3.tuple.Triple;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

public class CustomizationGuiButton extends GuiButton {

    class SnapData {
        public boolean xSnap = false,ySnap = false;
        public Vector2f vec = new Vector2f(0,0);
    }

    Vector2f initialPos;

    List<Integer> xSnapPoint, ySnapPoints;
    GUIComponent guiComponent;
    int SubComponent = -1;
    public boolean isUserDragging = false;
    private Vector2f fineTuned = new Vector2f(0,0);
    private boolean LockY = false;

    public CustomizationGuiButton (GUIComponent component,List<Integer> xSnapPoint, List<Integer> ySnapPoints) {
        super(0,0,0,1,1,"");
        this.guiComponent = component;
        this.xSnapPoint = xSnapPoint;
        this.ySnapPoints = ySnapPoints;
    }
    public CustomizationGuiButton (GUIComponent component,int SubComponent,List<Integer> xSnapPoint, List<Integer> ySnapPoints) {
        this(component,xSnapPoint,ySnapPoints);
        this.SubComponent = SubComponent;
    }
    public CustomizationGuiButton (GUIComponent component,int SubComponent,List<Integer> xSnapPoint, List<Integer> ySnapPoints,boolean LockY) {
        this(component,SubComponent,xSnapPoint,ySnapPoints);
        this.LockY = LockY;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            ScaledResolution sr = new ScaledResolution(mc);
            Vector2f v, rv;
            if (SubComponent == -1) {
                v = this.guiComponent.getRealAnchorPoint();
                rv = this.guiComponent.getBoundingPoint();
            } else {
                v = ApecUtils.addVec(this.guiComponent.getRealAnchorPoint(),this.guiComponent.getSubElementsRealAnchorPoints().get(SubComponent));
                rv = ApecUtils.addVec(this.guiComponent.getRealAnchorPoint(),this.guiComponent.getSubElementsBoundingPoints().get(SubComponent));
            }
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
                if (SubComponent == -1) {
                    anchor = this.guiComponent.getAnchorPointPosition();
                } else {
                    anchor = ApecUtils.addVec(this.guiComponent.getSubElementsAnchorPoints().get(SubComponent), this.guiComponent.getAnchorPointPosition());
                }

                boolean isSnappedToPositionX = false;
                boolean isSnappedToPositionY = false;

                SnapData SnapResult = IsSnapped(mouseX, mouseY, sr, anchor);
                isSnappedToPositionX = SnapResult.xSnap;
                isSnappedToPositionY = SnapResult.ySnap;


                if (SubComponent == -1) {
                    Vector2f Result = new Vector2f(
                            isSnappedToPositionX ? SnapResult.vec.x + fineTuned.x : mouseX - anchor.x + fineTuned.x + initialPos.x,
                            (isSnappedToPositionY ? SnapResult.vec.y + fineTuned.y : mouseY - anchor.y + fineTuned.y + initialPos.y) * (LockY ? 0 : 1)
                    );
                    this.guiComponent.setDelta_position(Result);
                } else {
                    Vector2f Result = new Vector2f(
                            isSnappedToPositionX ? SnapResult.vec.x + fineTuned.x : mouseX - anchor.x + fineTuned.x + initialPos.x,
                            (isSnappedToPositionY ? SnapResult.vec.y + fineTuned.y : mouseY - anchor.y + fineTuned.y + initialPos.y) * (LockY ? 0 : 1)
                    );
                    this.guiComponent.setSubElementDelta_position(Result, SubComponent);
                }
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
        initialPos = this.guiComponent.getRealAnchorPoint();
        if (SubComponent != -1) {
            initialPos = ApecUtils.addVec(initialPos,this.guiComponent.getSubElementsRealAnchorPoints().get(SubComponent));
        }
        initialPos.x -= mouseX;
        initialPos.y -= mouseY;
        this.isUserDragging = true;
    }

    public void setDeltaToZero() {
        if (SubComponent == -1) {
            this.guiComponent.setDelta_position(new Vector2f(0, 0));
        } else {
            this.guiComponent.setSubElementDelta_position(new Vector2f(0,0),SubComponent);
        }
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

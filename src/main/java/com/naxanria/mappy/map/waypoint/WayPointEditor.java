package com.naxanria.mappy.map.waypoint;

import com.naxanria.mappy.client.ScreenBase;
import com.naxanria.mappy.client.widget.TitledWidget;
import com.naxanria.mappy.util.Predicates;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class WayPointEditor extends ScreenBase
{
  private static final StringTextComponent title = new StringTextComponent("Edit Waypoint");

  private final WayPoint wayPoint;
  private int colorIndex;
  
  private TitledWidget<TextFieldWidget> nameField;
  private Button prevColorButton, nextColorButton;
  private TextFieldWidget xField, yField, zField;
//  private CheckboxWidget hiddenCheckbox, alwaysShownCheckBox;
  private Button saveButton, cancelButton;
  private Consumer<WayPoint> onSaveCallback;
  
  public WayPointEditor(WayPoint wayPoint, Screen parent, Consumer<WayPoint> onSaveCallback)
  {
    super(title, parent);
    
    this.wayPoint = wayPoint;
    colorIndex = getColorIndex(wayPoint.color);
    this.onSaveCallback = onSaveCallback;
  }
  
  @Override
  public void init()
  {
    int halfWidth = width / 2;
  
    int halfW = 150 / 2;
    int h = 20;
    
    int x = halfWidth - halfW;
    
    nameField = new TitledWidget<>(font, new TextFieldWidget(font, 0, 0, 100, 12, "Name"), x, 50, 180, h, "", lang("name"));
    nameField.changeFocus(true);
    nameField.widget.setMaxStringLength(12);
    nameField.widget.setText(wayPoint.name);
    
    children.add(nameField);
    
    Predicate<String> validNumber = (s) -> Predicates.or(s, Predicates.isInteger, Predicates.isEmpty);
    
    int pw = 60;
    
    xField = new TextFieldWidget(font, x, 70, pw, h, "");
    xField.setValidator(validNumber);
    xField.setMaxStringLength(7);
    xField.setText(wayPoint.pos.getX() + "");
    
    yField = new TextFieldWidget(font, x + pw, 70, pw, h, "");
    yField.setValidator(validNumber);
    yField.setMaxStringLength(7);
    yField.setText(wayPoint.pos.getY() + "");
    
    zField = new TextFieldWidget(font, x + pw + pw, 70, pw, h, "");
    zField.setValidator(validNumber);
    zField.setMaxStringLength(7);
    zField.setText(wayPoint.pos.getZ() + "");
    
    children.add(xField);
    children.add(yField);
    children.add(zField);
    
    int bw = 20;
    prevColorButton = new Button(x, 92, bw, h, "<", (b) -> cycleColor(-1));
    children.add(prevColorButton);
    
    nextColorButton = new Button(x + halfWidth - bw - 30, 92, bw, h, ">", (b) -> cycleColor(1));
    children.add(nextColorButton);
    
    int by = height - 90;
    saveButton = new Button(x, by, 60, h, lang("save"), (b) -> { save(); onClose(); });
    children.add(saveButton);
    
    cancelButton = new Button(x + 62, by, 60, h, lang("cancel"), (b) -> onClose());
    children.add(cancelButton);
    
    setFocused(nameField);
//    setInitialFocus(nameField);
  }
  
  private void cycleColor(int i)
  {
    colorIndex += i;
    if (colorIndex < 0)
    {
      colorIndex = WayPoint.WAYPOINT_COLORS.length - 1;
    }
    else if (colorIndex >= WayPoint.WAYPOINT_COLORS.length)
    {
      colorIndex = 0;
    }
  }
  
  private void save()
  {
    wayPoint.name = nameField.widget.getText();
    wayPoint.color = WayPoint.WAYPOINT_COLORS[colorIndex];
    
    int xPos = xField.getText().isEmpty() ? 0 : Integer.parseInt(xField.getText());
    int yPos = yField.getText().isEmpty() ? 0 : Integer.parseInt(yField.getText());
    int zPos = zField.getText().isEmpty() ? 0 : Integer.parseInt(zField.getText());
    
    wayPoint.pos = new BlockPos(xPos, yPos, zPos);
    
    if (onSaveCallback != null)
    {
      onSaveCallback.accept(wayPoint);
    }
    
    WayPointManager.INSTANCE.save();
  }
  
  @Override
  public void onClose()
  {
    minecraft.displayGuiScreen(parent);
  }
  
  @Override
  public void renderForeground()
  {
    int x = prevColorButton.x + prevColorButton.getWidth() + 2;
    int w = nextColorButton.x - x - 2;
    int y = prevColorButton.y + 3;
    int h = 12;
    int col = WayPoint.WAYPOINT_COLORS[colorIndex];
    
    borderedRect(x, y, w, h, col, 2, 0xFFCCCCCC);
  }
  
  @Override
  public void tick()
  {
  
  }
  
  private void rect(int x, int y, int w, int h, int color)
  {
    fill(x, y, x + w, y + h, color);
  }
  
  private void borderedRect(int x, int y, int w, int h, int color, int borderColor)
  {
    borderedRect(x, y, w, h, color, 2, borderColor);
  }
  
  private void borderedRect(int x, int y, int w, int h, int color, int border, int borderColor)
  {
    int hb = border >> 1;
    rect(x, y, w, h, borderColor);
    rect(x + hb, y + hb, w - border, h - border, color);
  }
  
  private int getColorIndex(int color)
  {
    for (int i = 0; i < WayPoint.WAYPOINT_COLORS.length; i++)
    {
      if (WayPoint.WAYPOINT_COLORS[i] == color)
      {
        return i;
      }
    }
    
    return 0;
  }
  
  @Override
  public boolean keyPressed(int int_1, int int_2, int int_3)
  {
    if (int_1 == GLFW.GLFW_KEY_ENTER)
    {
      save();
      onClose();
      return true;
    }
    
    return super.keyPressed(int_1, int_2, int_3);
  }
}

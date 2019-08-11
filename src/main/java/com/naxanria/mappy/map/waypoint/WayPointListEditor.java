package com.naxanria.mappy.map.waypoint;

import com.naxanria.mappy.Mappy;
import com.naxanria.mappy.gui.DrawableHelperBase;
import com.naxanria.mappy.gui.ScreenBase;
import com.naxanria.mappy.util.BiValue;
import com.naxanria.mappy.util.MathUtil;
import com.naxanria.mappy.util.RandomUtil;
import com.naxanria.mappy.util.Util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.world.dimension.DimensionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WayPointListEditor extends ScreenBase
{
  public static final ResourceLocation DEFAULT_IDENTIFIER = new ResourceLocation("textures/block/dirt.png");
  public static final HashMap<String, BiValue<String, ResourceLocation>> DIMENSION_INFO = new HashMap<String, BiValue<String, ResourceLocation>>()
  {{
    put("minecraft:overworld", new BiValue<>("mappy.dim.overworld", new ResourceLocation("textures/block/stone.png")));
    put("minecraft:the_nether", new BiValue<>("mappy.dim.nether", new ResourceLocation("textures/block/nether_bricks.png")));
    put("minecraft:the_end", new BiValue<>("mappy.dim.the_end", new ResourceLocation("textures/block/end_stone_bricks.png")));
  }};
  
  private static class Entry extends DrawableHelperBase implements IGuiEventListener
  {
    private static Minecraft minecraft = Minecraft.getInstance();
  
    private int x;
    private int y;
    private int width, height;
    private WayPoint wayPoint;
    private Button editButton;
    private Button deleteButton;
//    private ButtonWidget chatButton;
  
    public Entry(WayPointListEditor wayPointListEditor, int x, int y, int width, int height, WayPoint wayPoint)
    {
      this.width = width;
      this.height = height;
      this.wayPoint = wayPoint;
      
      editButton = new Button(0, 0, 40, height, wayPointListEditor.lang("edit"), (b) -> wayPointListEditor.edit(wayPoint));
      deleteButton = new Button(0, 0, 40, height, wayPointListEditor.lang("delete"), (b) -> wayPointListEditor.delete(wayPoint));
//      chatButton = new ButtonWidget(0, 0, 40, height, wayPointListEditor.lang("chat"), (b) -> wayPointListEditor.toChat(wayPoint));
      
      setPosition(x, y);
    }
  
    public void setPosition(int x, int y)
    {
      this.x = x;
      this.y = y;
      
      rightAlign(deleteButton, x + width);
      rightAlign(editButton, deleteButton);
//      rightAlign(chatButton, editButton);
      
      editButton.y = y;
//      editButton.x = x + width - editButton.getWidth() - deleteButton.getWidth();
      deleteButton.y = y;
//      deleteButton.x = x + width - deleteButton.getWidth();
//      chatButton.y = y;
    }
    
    
    public void render(int mouseX, int mouseY, float delta)
    {
      FontRenderer font = minecraft.fontRenderer;
      
      // background
      boolean hover = isMouseOver(mouseX, mouseY);
      int bgColor = hover ? 0x88aaaaaa : 0x88333333;
      fill(x, y, x + width, y + height, bgColor);
      
      // todo: other icons if needed
      
      int size = height - 2;
      
      wayPoint.iconType.draw(x + size / 2, y + 1 + size / 2, wayPoint.color);
//
//      int diamondSize = height - 2;
//      diamond(x, y + 1, diamondSize, diamondSize, wayPoint.color);
      
      int stringY = y + 6;
      
      int nameX = x + size + 2;

      drawString(font, wayPoint.name, nameX, stringY, WHITE);
      
      int posX = editButton.x - 2;
      drawRightAlignedString(font, Util.prettyFy(wayPoint.pos), posX, stringY, WHITE);
      
      editButton.render(mouseX, mouseY, delta);
      deleteButton.render(mouseX, mouseY, delta);
//      chatButton.render(mouseX, mouseY, delta);
    }
  
    @Override
    public boolean mouseClicked(double double_1, double double_2, int int_1)
    {
      return editButton.mouseClicked(double_1, double_2, int_1) || deleteButton.mouseClicked(double_1, double_2, int_1);// || chatButton.mouseReleased(double_1, double_2, int_1);
    }
  
    @Override
    public boolean mouseReleased(double double_1, double double_2, int int_1)
    {
      return editButton.mouseReleased(double_1, double_2, int_1) || deleteButton.mouseReleased(double_1, double_2, int_1);// || chatButton.mouseReleased(double_1, double_2, int_1);
    }
  
    @Override
    public boolean isMouseOver(double mouseX, double mouseY)
    {
      return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
  
    private void rightAlign(Button toAlign, Button from)
    {
      toAlign.x = from.x - toAlign.getWidth();
    }
  
    private void rightAlign(Button toAlign, int right)
    {
      toAlign.x = right - toAlign.getWidth();
    }
  }
  
  private static final TextComponent title = new StringTextComponent("Waypoints");
  
  private WayPointManager manager = WayPointManager.INSTANCE;
  private int currentDim = 0;
  private int currentDimIndex = 0;
  private List<Integer> dimensions;
  private List<WayPoint> wayPoints;
  private List<Entry> entries = new ArrayList<>();
  private BiValue<String, ResourceLocation> info;

  private int scrollAmount = 0;
  private int maxScroll = 0;
  
  private Button prevDimensionButton, nextDimensionButton;
  private Button addButton, closeButton;
  
  private int x, y;
  private int width, height;
  
  public WayPointListEditor(Screen parent)
  {
    super(title, parent);
    if (minecraft == null)
    {
      minecraft = Minecraft.getInstance();
    }
    
    dimensions = manager.getWaypointDimensions();
    
    int overWorldId = DimensionType.OVERWORLD.getId();
    int netherId = DimensionType.THE_NETHER.getId();
    int theEndId = DimensionType.THE_END.getId();
    if (!dimensions.contains(overWorldId))
    {
      dimensions.add(overWorldId);
    }
    
    if (!dimensions.contains(netherId))
    {
      dimensions.add(netherId);
    }
    
    if (!dimensions.contains(theEndId))
    {
      dimensions.add(theEndId);
    }
    
    
    currentDim = minecraft.player.dimension.getId();
    currentDimIndex = getDimIndex(currentDim);
    
    
  }
  
  @Override
  protected void init()
  {
    width = Math.max(300, minecraft.mainWindow.getScaledWidth() / 2);
    height = minecraft.mainWindow.getScaledHeight();
  
    x = minecraft.mainWindow.getScaledWidth() / 2 - width / 2;
    
    prevDimensionButton = new Button(x + 10, 10, 20, 20, "<", (b) -> cycleDimension(-1));
    nextDimensionButton = new Button(x + width - 20 - 10, 10, 20, 20, ">", (b) -> cycleDimension(1));
    
    addButton = new Button(x + 10, height - 25, 60, 20, lang("create"), (b) -> add());
    closeButton = new Button(x + 15 + addButton.getWidth(), height - 25, 60, 20, lang("close"), (b) -> onClose());
  
    reset();
  }
  
  private void createEntries()
  {
    entries.clear();
  
    int y = 40;
    for (WayPoint wp :
      wayPoints)
    {
      Entry entry = new Entry(this, x + 10, scrollAmount + y, width - 10 - 10, 20, wp);
      entries.add(entry);
      
      y += entry.height;
    }
  }
  
  private void updateEntries()
  {
    int y = 40;
    for (Entry entry :
      entries)
    {
      entry.setPosition(x + 10, scrollAmount + y);
      y += entry.height;
    }
  }
  
  
  private void cycleDimension(int i)
  {
    currentDimIndex += i;
    if (currentDimIndex >= dimensions.size())
    {
      currentDimIndex = 0;
    }
    else if (currentDimIndex < 0)
    {
      currentDimIndex = dimensions.size() - 1;
    }
    
    currentDim = dimensions.get(currentDimIndex);
    reset();
  }
  
  private int getDimIndex(int dim)
  {
    for (int i = 0; i < dimensions.size(); i++)
    {
      int dimId = dimensions.get(i);
      if (dimId == dim)
      {
        return i;
      }
    }
    
    return 0;
  }
  
  public void reset()
  {
    info = getDimensionInfo(currentDim);
    
    wayPoints = manager.getWaypoints(currentDim);
    createEntries();
    maxScroll = wayPoints.size() * 20;
    
    children.clear();
    children.addAll(entries);
    children.add(addButton);
    children.add(closeButton);
    children.add(prevDimensionButton);
    children.add(nextDimensionButton);
    
  }
  
  @Override
  public void render(int mouseX, int mouseY, float partialTicks)
  {
    // background
    fill(x, 0, x + width, height, 0x33444444);
    
    // entries
    entries.forEach(e -> e.render(mouseX, mouseY, partialTicks));
    
    drawBorders(mouseX, mouseY, partialTicks);
//    System.out.println("RENDER");
    
    prevDimensionButton.render(mouseX, mouseY, partialTicks);
  
    
    String dimensionName = info == null ? lang("unknown") : I18n.format(info.A);
    drawCenteredString(font, dimensionName, 130 / 2 + prevDimensionButton.x + prevDimensionButton.getWidth(), 15, 0xffffffff);
    
    nextDimensionButton.render(mouseX, mouseY, partialTicks);
    
    addButton.render(mouseX, mouseY, partialTicks);
    closeButton.render(mouseX, mouseY, partialTicks);
    
    drawScrollBar();
  }
  
  private BiValue<String, ResourceLocation> getDimensionInfo(int dim)
  {
    DimensionType type = DimensionType.getById(dim);
    String key = "unknown";
    if (type != null)
    {
      ResourceLocation rl = DimensionType.getKey(type);
      if (rl != null)
      {
        key = rl.toString();
      }
    }
    
    if (key.equals("unknown"))
    {
      Mappy.LOGGER.warn("Unkown dim: " + dim);
    }
    
    return DIMENSION_INFO.getOrDefault(key, new BiValue<>(key, DEFAULT_IDENTIFIER));
  }
  
  private void drawScrollBar()
  {
  }
  
  private void drawBorders(int mouseX, int mouseY, float delta)
  {
    ResourceLocation id;
    if (info != null)
    {
      id = info.B;
  
      if (id == null)
      {
        id = DEFAULT_IDENTIFIER;
      }
    }
    else
    {
      id = DEFAULT_IDENTIFIER;
    }
    

    renderTextureRepeating(x, 0, width, 40, 16, 16, id);
    renderTextureRepeating(x, height - 40, width, 40, 16, 16, id);
    
  }
  
  private void edit(WayPoint wayPoint)
  {
    minecraft.displayGuiScreen(new WayPointEditor(wayPoint, this, null));
  }
  
  private void tp(WayPoint wp)
  {
    // todo: Teleporting player.
  
    
  }
  
  private void toChat(WayPoint wp)
  {
//    StringTextComponent text = new StringTextComponent(Util.prettyFy(wp.pos));
//    Style style = text.getStyle();
//    style.setColor(TextFormat.AQUA);
//    style.setBold(true);
//    style.setClickEvent(
//      new ClickEvent(
//        ClickEvent.Action.SUGGEST_COMMAND, "/wp " + wp.pos.getX() + " " + wp.pos.getY() + " " + wp.pos.getZ() + " " + wp.dimension
//    ));
//
//    onClose();
//
//    minecraft.player.sendMessage(text);
  }
  
  private void add()
  {
    WayPoint wayPoint = new WayPoint();
    wayPoint.dimension = currentDim;
    wayPoint.color = RandomUtil.getElement(WayPoint.WAYPOINT_COLORS);
    wayPoint.pos = new BlockPos(0, 0, 0);
    wayPoint.name = "Waypoint";
    
    minecraft.displayGuiScreen(new WayPointEditor(wayPoint, this, (manager::add)));
  }
  
  private void delete(WayPoint wayPoint)
  {
    manager.remove(wayPoint);
    manager.save();
    reset();
  }
  
  @Override
  public boolean mouseScrolled(double double_1, double double_2, double double_3)
  {
    scrollAmount = MathUtil.clamp(scrollAmount + (int) (double_3 * 12), -maxScroll + 80, 0);
    updateEntries();
    
    return true;
  }
}

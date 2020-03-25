package com.naxanria.mappy.map.waypoint;

import com.naxanria.mappy.Mappy;
import com.naxanria.mappy.config.MappyConfig;
import com.naxanria.mappy.gui.DrawableHelperBase;
import com.naxanria.mappy.gui.ScreenBase;
import com.naxanria.mappy.util.*;

import net.minecraft.client.MainWindow;
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
    private Button teleportButton;
    private Button chatButton;
    private WayPointListEditor wayPointListEditor;
    
    private List<Button> buttons = new ArrayList<>();
  
    public Entry(WayPointListEditor wayPointListEditor, int x, int y, int width, int height, WayPoint wayPoint)
    {
      this.width = width;
      this.height = height;
      this.wayPoint = wayPoint;
      this.wayPointListEditor = wayPointListEditor;
      
      editButton = new Button(0, 0, 40, height, wayPointListEditor.lang("edit"), (b) -> wayPointListEditor.edit(wayPoint));
      buttons.add(editButton);
      
      deleteButton = new Button(0, 0, 40, height, wayPointListEditor.lang("delete"), (b) -> wayPointListEditor.delete(wayPoint));
      buttons.add(deleteButton);
  
      MappyConfig.Client config = MappyConfig.getConfig();
      if (config.chatButton.get())
      {
        chatButton = new Button(0, 0, 40, height, wayPointListEditor.lang("chat"), (b) -> wayPointListEditor.toChat(wayPoint));
        buttons.add(chatButton);
      }
      
      if (config.teleportButton.get())
      {
        teleportButton = new Button(0, 0, 17, height, wayPointListEditor.lang("teleport"), (b) -> wayPointListEditor.teleport(wayPoint));
        buttons.add(teleportButton);
      }
      
      setPosition(x, y);
    }
  
    public void setPosition(int x, int y)
    {
      boolean tp = MappyConfig.getConfig().teleportButton.get();
      boolean chat = MappyConfig.getConfig().showInChat.get();
      
      this.x = x;
      this.y = y;
      
      if (chat)
      {
        rightAlign(chatButton, x + width);
      }
      
      if (tp)
      {
        if (chat)
        {
          rightAlign(teleportButton, chatButton);
        }
        else
        {
          rightAlign(teleportButton, x + width);
        }
        
        rightAlign(deleteButton, teleportButton);
      }
      else
      {
        if (chat)
        {
          rightAlign(deleteButton, chatButton);
        }
        else
        {
          rightAlign(deleteButton, x + width);
        }
      }
      
      rightAlign(editButton, deleteButton);
      
      for(Button b : buttons)
      {
        b.y = y;
      }
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
      
      for (Button b : buttons)
      {
        b.render(mouseX, mouseY, delta);
      }
//
//      teleportButton.render(mouseX, mouseY, delta);
//      editButton.render(mouseX, mouseY, delta);
//      deleteButton.render(mouseX, mouseY, delta);
      
//      chatButton.render(mouseX, mouseY, delta);
    }
  
    @Override
    public boolean mouseClicked(double double_1, double double_2, int int_1)
    {
      for(Button b : buttons)
      {
        if (b.mouseClicked(double_1, double_2, int_1))
        {
          return true;
        }
      }
      return false;
    }
  
    @Override
    public boolean mouseReleased(double double_1, double double_2, int int_1)
    {
      for(Button b : buttons)
      {
        if (b.mouseReleased(double_1, double_2, int_1))
        {
          return true;
        }
      }
      return false;
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
  private boolean canTeleport = false;
  
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
    MainWindow mainWindow = minecraft.getMainWindow();
    width = Math.max(300, mainWindow.getScaledWidth() / 2);
    height = mainWindow.getScaledHeight();
  
    x = mainWindow.getScaledWidth() / 2 - width / 2;
    
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
      Mappy.LOGGER.warn("Unknown dim: " + dim);
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
    

    DrawableHelperBase.renderTextureRepeating(x, 0, width, 40, 16, 16, id);
    DrawableHelperBase.renderTextureRepeating(x, height - 40, width, 40, 16, 16, id);
    
  }
  
  @Override
  public boolean isPauseScreen()
  {
    return false;
  }
  
  private void edit(WayPoint wayPoint)
  {
    minecraft.displayGuiScreen(new WayPointEditor(wayPoint, this, null));
  }
  
  private boolean canTeleport()
  {
    // todo: figure out if server allows you to teleport or not
    return true;
  }
  
  private void teleport(WayPoint wayPoint)
  {
    Mappy.LOGGER.info("Teleporting");
    // check if we can teleport
    if (canTeleport())
    {
      // execute teleport
      BlockPos pos = wayPoint.pos;
      
      minecraft.player.sendChatMessage("/tp " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
      minecraft.displayGuiScreen(null);
      Mappy.LOGGER.info("Teleported to " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
    }
  }
  
  
  private void toChat(WayPoint wp)
  {
    BlockPos pos = wp.pos;
    minecraft.player.sendChatMessage("[" + wp.name + " " + pos.getX() + " " + pos.getY() + " " + pos.getZ() + "]");
    minecraft.displayGuiScreen(null);
  }
  
  private void add()
  {
    WayPoint wayPoint = new WayPoint();
    wayPoint.dimension = currentDim;
    wayPoint.color = RandomUtil.getElement(WayPoint.WAYPOINT_COLORS);
    wayPoint.pos = minecraft.player.getPosition();
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

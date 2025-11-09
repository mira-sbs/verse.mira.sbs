package sbs.mira.verse.command.lobby;

import app.ashcon.intake.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import sbs.mira.core.model.MiraCommandModel;
import sbs.mira.core.model.configuration.MiraMapRotationModel;
import sbs.mira.verse.MiraVersePulse;

public
class CommandRotation
  extends MiraCommandModel<MiraVersePulse>
{
  /**
   * tbd.
   *
   * @param pulse tbd.
   */
  public
  CommandRotation( @NotNull MiraVersePulse pulse )
  {
    super( pulse );
  }
  
  @Command (
    aliases = { "rotation", "rot" },
    usage = "[]",
    desc = "view the current map rotation.",
    help = "help tbd."
  )
  public
  void rotation( @NotNull CommandSender sender )
  {
    MiraMapRotationModel<MiraVersePulse> map_rotation =
      this.pulse( ).model( ).lobby( ).map_rotation( );
    
    String[] rotation_map_labels = map_rotation.values( );
    int current_rotation_index = map_rotation.index( );
    int next_rotation_index =
      current_rotation_index + 1 >= rotation_map_labels.length ? 0 : current_rotation_index;
    
    boolean is_next_map_set = map_rotation.set_next_map( );
    
    sender.sendMessage( "current rotation:" );
    for ( int rotation_index = 0; rotation_index < rotation_map_labels.length; rotation_index++ )
    {
      boolean is_current_map = rotation_index == current_rotation_index;
      boolean is_next_map = rotation_index == next_rotation_index;
      
      String extra_info = "";
      
      if ( is_current_map )
      {
        extra_info = " %s[current]".formatted( ChatColor.RED );
      }
      
      if ( is_next_map && !is_next_map_set )
      {
        extra_info = " %s[next]".formatted( ChatColor.DARK_RED );
      }
      
      String rotation_item = "%s[%s]%s» %s%s%s".formatted(
        ChatColor.AQUA,
        String.valueOf( rotation_index ),
        ChatColor.DARK_AQUA,
        ChatColor.GREEN,
        rotation_map_labels[ rotation_index ],
        extra_info );
      
      sender.sendMessage( rotation_item );
    }
    
    if ( is_next_map_set )
    {
      sender.sendMessage( ChatColor.YELLOW + "the next map has been set out of rotation." );
    }
  }
}

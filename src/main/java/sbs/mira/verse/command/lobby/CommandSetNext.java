package sbs.mira.verse.command.lobby;

import app.ashcon.intake.Command;
import app.ashcon.intake.parametric.annotation.Switch;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sbs.mira.core.model.MiraCommandModel;
import sbs.mira.core.model.map.MiraMapModel;
import sbs.mira.verse.MiraVersePulse;

public
class CommandSetNext
  extends MiraCommandModel<MiraVersePulse>
{
  /**
   * tbd.
   *
   * @param pulse tbd.
   */
  public
  CommandSetNext( @NotNull MiraVersePulse pulse )
  {
    super( pulse );
  }
  
  @Command (
    aliases = { "setnext", "sn" },
    desc = "manually set the next map that will be played.",
    usage = "<map>",
    help = "help tbd.",
    perms = "mira.moderator",
    flags = "u"
  )
  public
  void set_next(
    @NotNull CommandSender sender,
    @Nullable String map_label,
    @Switch ('u') boolean unset )
  {
    
    if ( unset )
    {
      this.pulse( ).model( ).lobby( ).map_rotation( ).set_next_map( null );
      
      this.server( ).broadcastMessage( "%s has unset the next map - the rotation will continue.".formatted(
        sender.getName( ) ) );
      
      return;
    }
    
    // fixme: need map repository + game mode repository.
    MiraMapModel<MiraVersePulse> map = null;
    
    if ( map == null )
    {
      sender.sendMessage( ChatColor.RED + "that map does not exist." );
      
      return;
    }
    
    this.pulse( ).model( ).lobby( ).map_rotation( ).set_next_map( map_label );
    
    this.server( ).broadcastMessage( "%s has set the next map to be %s.".formatted(
      sender.getName( ),
      map.display_name( ) ) );
  }
}

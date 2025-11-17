package sbs.mira.verse.command.lobby;

import app.ashcon.intake.Command;
import app.ashcon.intake.parametric.annotation.Switch;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import sbs.mira.core.model.MiraCommandModel;
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
    String map_label,
    @Switch ('u') boolean unset )
  {
    
    if ( unset )
    {
      this.pulse( ).model( ).lobby( ).map_rotation( ).set_next_map( null );
      
      this.server( ).broadcastMessage( this.pulse( ).model( ).message(
        "match.map.set_next.unset",
        sender.getName( ) ) );
      
      return;
    }
    
    // fixme: need map repository + game mode repository.
    try
    {
      Class<?> map = this.pulse( ).model( ).map_repository( ).map_class( map_label );
    }
    catch ( IllegalArgumentException illegal_argument_exception )
    {
      sender.sendMessage( this.pulse( ).model( ).message( "match.map.no_match" ) );
      
      return;
    }
    
    this.pulse( ).model( ).lobby( ).map_rotation( ).set_next_map( map_label );
    
    //fixme: use map name and not label. need db entries first!
    this.server( ).broadcastMessage( this.pulse( ).model( ).message(
      "match.map.set_next.ok",
      sender.getName( ),
      map_label ) );
  }
}

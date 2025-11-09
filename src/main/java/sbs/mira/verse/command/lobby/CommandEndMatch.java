package sbs.mira.verse.command.lobby;

import app.ashcon.intake.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import sbs.mira.core.model.MiraCommandModel;
import sbs.mira.core.model.match.MiraMatchState;
import sbs.mira.verse.MiraVersePulse;

public
class CommandEndMatch
  extends MiraCommandModel<MiraVersePulse>
{
  /**
   * tbd.
   *
   * @param pulse tbd.
   */
  protected
  CommandEndMatch( @NotNull MiraVersePulse pulse )
  {
    super( pulse );
  }
  
  @Command (
    aliases = { "endmatch", "endm" },
    usage = "[]",
    desc = "view the current map rotation.",
    help = "help tbd.",
    perms = "mira.moderator"
  )
  // Give this a special permission so only administrators can use it
  public
  void end_match( @NotNull CommandSender sender )
  {
    if ( this.pulse( ).model( ).lobby( ).match( ).state( ) != MiraMatchState.GAME )
    {
      sender.sendMessage( ChatColor.RED + "there is no match to end." );
      
      return;
    }
    
    this.server( ).broadcastMessage( "%s%s called an end to this match early.".formatted(
      ChatColor.LIGHT_PURPLE,
      sender.getName( ) ) );
    
    this.pulse( ).model( ).lobby( ).conclude_match( );
  }
}

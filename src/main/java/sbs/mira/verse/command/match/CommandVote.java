package sbs.mira.verse.command.match;

import app.ashcon.intake.Command;
import app.ashcon.intake.parametric.annotation.Switch;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sbs.mira.core.model.MiraCommandModel;
import sbs.mira.core.model.match.MiraGameModeType;
import sbs.mira.verse.MiraVersePulse;

public
class CommandVote
  extends MiraCommandModel<MiraVersePulse>
{
  /**
   * instantiates this handler of the `/vote` command.
   *
   * @param pulse reference to mira.
   */
  public
  CommandVote( @NotNull MiraVersePulse pulse )
  {
    super( pulse );
  }
  
  @Command (
    aliases = { "vote", "v" },
    desc = "cast a one-time vote for an available game mode during voting.",
    usage = "<game mode> [-r/rig]",
    help = "help tbd."
  )
  public
  void vote(
    @NotNull CommandSender sender,
    @NotNull String input_game_mode,
    @Switch ('r') boolean rig )
  {
    if ( !( sender instanceof Player ) )
    {
      sender.sendMessage( ChatColor.RED + "console cannot participate in-game. :(" );
      
      return;
    }
    
    @Nullable MiraGameModeType input_game_mode_type = null;
    
    for ( MiraGameModeType game_mode_type : MiraGameModeType.values( ) )
    {
      if ( input_game_mode.equalsIgnoreCase( game_mode_type.name( ) ) )
      {
        input_game_mode_type = game_mode_type;
        break;
      }
    }
    
    if ( input_game_mode_type == null )
    {
      sender.sendMessage( ChatColor.RED + "that game mode does not exist." );
      
      return;
    }
    
    this.pulse( ).model( ).lobby( ).match( ).votes( ).try_vote(
      this.pulse( ).model( ).player( ( ( Player ) sender ).getUniqueId( ) ),
      input_game_mode_type,
      rig );
  }
}

package sbs.mira.verse;

import app.ashcon.intake.bukkit.BukkitIntake;
import app.ashcon.intake.bukkit.graph.BasicBukkitCommandGraph;
import app.ashcon.intake.fluent.DispatcherNode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import sbs.mira.core.MiraPlugin;
import sbs.mira.core.utility.MiraWorldUtility;
import sbs.mira.verse.command.lobby.CommandEndMatch;
import sbs.mira.verse.command.lobby.CommandRotation;
import sbs.mira.verse.command.lobby.CommandSetNext;
import sbs.mira.verse.command.match.CommandJoin;
import sbs.mira.verse.command.match.CommandLeave;
import sbs.mira.verse.command.match.CommandVote;

import java.io.IOException;

/**
 * main class + main methods for initialisation & destruction of the mira-verse
 * plugin.
 * created on 2017-03-20.
 *
 * @author jj stephen
 * @version 1.0.1
 * @see sbs.mira.core.MiraPlugin
 * @since 1.0.0
 */
public
class MiraVersePlugin
  extends MiraPlugin<MiraVersePulse>
{
  @NotNull
  private final MiraVerseDataModel data_model;
  
  public
  MiraVersePlugin( )
  {
    super( new MiraVersePulse( ) );
    
    this.data_model = new MiraVerseDataModel( this.pulse( ) );
  }
  
  @Override
  public
  void onLoad( )
  {
    BasicBukkitCommandGraph command_graph = new BasicBukkitCommandGraph( );
    DispatcherNode dispatcher = command_graph.getRootDispatcherNode( );
    dispatcher.registerCommands( new CommandJoin( this.pulse( ) ) );
    dispatcher.registerCommands( new CommandLeave( this.pulse( ) ) );
    dispatcher.registerCommands( new CommandVote( this.pulse( ) ) );
    dispatcher.registerCommands( new CommandEndMatch( this.pulse( ) ) );
    dispatcher.registerCommands( new CommandRotation( this.pulse( ) ) );
    dispatcher.registerCommands( new CommandSetNext( this.pulse( ) ) );
    
    BukkitIntake intake = new BukkitIntake( this, command_graph );
    intake.register( );
  }
  
  @Override
  public
  void onEnable( )
  {
    super.onEnable( );
    
    this.pulse( ).revive( this, this.data_model );
    
    this.pulse( ).log( "[verse] %s enables..".formatted( this.description( ) ) );
    
    this.getServer( ).getMessenger( ).registerOutgoingPluginChannel( this, "BungeeCord" );
    
    this.pulse( ).log( "[verse] first match starts." );
    
    try
    {
      this.pulse( ).model( ).lobby( ).begin_match( this.pulse( ).model( ).map_repository( ), this.pulse().model().game_mode_repository() );
    }
    catch ( IOException exception )
    {
      this.pulse( ).log( "could not start first match: %s".formatted( exception.getMessage( ) ) );
      
      this.getServer( ).shutdown( );
    }
  }
  
  @Override
  public
  void onDisable( )
  {
    super.onDisable( );
    
    for ( Player online : this.getServer( ).getOnlinePlayers( ) )
    {
      online.kickPlayer( this.getServer( ).getShutdownMessage( ) );
    }
    
    String world_name = this.pulse( ).model( ).lobby( ).match( ).world( ).getName( );
    
    try
    {
      MiraWorldUtility.discards( world_name );
      
      this.pulse( ).log( "(^-^)7 lingering match world '%s' deleted. bai!".formatted( world_name ) );
    }
    catch ( IOException ignored )
    {
      this.pulse( ).log(
        "lingering match world '%s' could not be discarded and must be deleted manually.".formatted(
          world_name ) );
    }
  }
}

package sbs.mira.verse;

import app.ashcon.intake.bukkit.BukkitIntake;
import app.ashcon.intake.bukkit.graph.BasicBukkitCommandGraph;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import sbs.mira.core.MiraPlugin;
import sbs.mira.core.utility.MiraWorldUtility;

import java.io.IOException;

/**
 * [witty comment here.]
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
  public
  MiraVersePlugin( @NotNull MiraVersePulse pulse )
  {
    super( pulse );
  }
  
  @Override
  public
  void onLoad( )
  {
    this.log( "[verse] %s loads..".formatted( this.description( ) ) );
    
    super.onLoad( );
    
    this.pulse( ).breathe( this, new MiraVerseDataModel( this.pulse( ) ) );
    
    BasicBukkitCommandGraph cmdGraph = new BasicBukkitCommandGraph( );
    // todo: commands here
    
    BukkitIntake intake = new BukkitIntake( this, cmdGraph );
    intake.register( );
  }
  
  @Override
  public
  void onEnable( )
  {
    super.onEnable( );
    
    this.log( "[verse] %s enables..".formatted( this.description( ) ) );
    
    this.getServer( ).getMessenger( ).registerOutgoingPluginChannel( this, "BungeeCord" );
    
    this.log( "[verse] first match starts." );
    
    try
    {
      pulse( ).model( ).lobby( ).begin_match( );
    }
    catch ( IOException exception )
    {
      this.log( "could not start first match: %s".formatted( exception.getMessage( ) ) );
      
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
    }
    catch ( IOException ignored )
    {
      this.log( "lingering match world '%s' could not be discarded and must be deleted manually.".formatted(
        world_name ) );
    }
  }
}

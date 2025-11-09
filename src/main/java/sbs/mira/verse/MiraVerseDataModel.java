package sbs.mira.verse;

import org.bukkit.craftbukkit.v1_21_R6.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sbs.mira.core.model.MiraConfigurationModel;
import sbs.mira.core.model.MiraPluginDataModel;
import sbs.mira.core.model.match.MiraLobbyModel;

/**
 * [wit.]
 * created on 2017-03-20.
 *
 * @author jj.mira.sbs
 * @author jd.mira.sbs
 * @version 1.0.1
 * @since 1.0.0
 */
public
class MiraVerseDataModel
  extends MiraPluginDataModel<MiraVersePulse, MiraVersePlayer>
{
  private @Nullable MiraConfigurationModel<MiraVersePulse> pvp_messages;
  
  private final @NotNull MiraLobbyModel<MiraVersePulse> lobby;
  
  
  public
  MiraVerseDataModel( @NotNull MiraVersePulse pulse )
  {
    super( pulse );
    
    this.lobby = new MiraLobbyModel<>( this.pulse( ) );
  }
  
  @Override
  public
  void initialise( )
  {
    super.initialise( );
    
    this.pvp_messages = new MiraConfigurationModel<>( this.pulse( ), "pvp_messages.yml" );
  }
  
  @Override
  public @NotNull
  String find_message( @NotNull String key )
  {
    assert this.pvp_messages != null;
    
    String result = this.pvp_messages.get( key );
    
    if ( result == null )
    {
      result = super.find_message( key );
    }
    
    return result;
  }
  
  @Override
  public @NotNull
  MiraVersePlayer declares( @NotNull CraftPlayer subject )
  {
    
    return new MiraVersePlayer( subject, pulse( ) );
  }
  
  public @NotNull
  MiraVersePlayer declares( @NotNull Player subject )
  {
    return this.declares( ( CraftPlayer ) subject );
  }
  
  /*—[mvc]————————————————————————————————————————————————————————————————————*/
  
  public @NotNull
  MiraLobbyModel<MiraVersePulse> lobby( )
  {
    return lobby;
  }
}

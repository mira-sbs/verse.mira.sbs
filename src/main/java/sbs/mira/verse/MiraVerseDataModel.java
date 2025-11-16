package sbs.mira.verse;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sbs.mira.core.model.MiraConfigurationModel;
import sbs.mira.core.model.MiraPluginDataModel;
import sbs.mira.core.model.match.MiraLobbyModel;
import sbs.mira.verse.event.handler.MiraVerseLobbyJoinGuard;
import sbs.mira.verse.event.handler.MiraVerseLobbyQuitGuard;
import sbs.mira.verse.model.map.MiraVerseMapRepository;
import sbs.mira.verse.model.match.MiraVerseGameModeRepository;
import sbs.mira.verse.model.match.MiraVerseLobbyGuard;

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
  @Nullable
  private MiraConfigurationModel<MiraVersePulse> verse_messages;
  
  @NotNull
  private final MiraVerseMapRepository map_repository;
  @NotNull
  private final MiraVerseGameModeRepository game_mode_repository;
  @Nullable
  private MiraLobbyModel<MiraVersePulse> lobby;
  
  
  public
  MiraVerseDataModel( @NotNull MiraVersePulse pulse )
  {
    super( pulse );
    
    this.map_repository = new MiraVerseMapRepository( );
    this.game_mode_repository = new MiraVerseGameModeRepository( );
  }
  
  @Override
  public
  void initialise( )
  {
    super.initialise( );
    
    this.lobby = new MiraLobbyModel<>( this.pulse( ) );
    this.verse_messages = new MiraConfigurationModel<>( this.pulse( ), "verse_messages.yml" );
    this.event_handler( new MiraVerseLobbyJoinGuard( this.pulse( ) ) );
    this.event_handler( new MiraVerseLobbyQuitGuard( this.pulse( ) ) );
    
    // todo: keep track of this?
    new MiraVerseLobbyGuard( this.pulse( ) );
  }
  
  @Override
  @NotNull
  public
  String find_message( @NotNull String key )
  {
    assert this.verse_messages != null;
    
    String result = this.verse_messages.get( key );
    
    if ( result == null )
    {
      result = super.find_message( key );
    }
    
    return result;
  }
  
  /*—[mvc]————————————————————————————————————————————————————————————————————*/
  
  @NotNull
  public
  MiraLobbyModel<MiraVersePulse> lobby( )
  {
    if ( this.lobby == null )
    {
      throw new NullPointerException( "lobby was not instantiated?" );
    }
    
    return this.lobby;
  }
  
  @NotNull
  public
  MiraVerseMapRepository map_repository( )
  {
    return this.map_repository;
  }
  
  @NotNull
  public
  MiraVerseGameModeRepository game_mode_repository( )
  {
    return this.game_mode_repository;
  }
}

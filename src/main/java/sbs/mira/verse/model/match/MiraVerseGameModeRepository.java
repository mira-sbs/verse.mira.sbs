package sbs.mira.verse.model.match;

import org.jetbrains.annotations.NotNull;
import sbs.mira.core.model.match.MiraGameModeModel;
import sbs.mira.core.model.match.MiraGameModeRepository;
import sbs.mira.core.model.match.MiraMatch;
import sbs.mira.core.model.match.MiraMatchModel;
import sbs.mira.verse.MiraVersePulse;
import sbs.mira.verse.model.match.game.mode.MiraCaptureTheFlag;
import sbs.mira.verse.model.match.game.mode.MiraDestroyTheMonument;
import sbs.mira.verse.model.match.game.mode.MiraLifePool;
import sbs.mira.verse.model.match.game.mode.MiraTeamDeathMatch;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public
class MiraVerseGameModeRepository
  implements MiraGameModeRepository<MiraVersePulse>
{
  @NotNull
  private final Map<String, Class<? extends MiraGameModeModel<MiraVersePulse>>> repository;
  
  public
  MiraVerseGameModeRepository( )
  {
    this.repository = new HashMap<>( );
    this.repository.put( "tdm", MiraTeamDeathMatch.class );
    this.repository.put( "ctf", MiraCaptureTheFlag.class );
    this.repository.put( "dtm", MiraDestroyTheMonument.class );
    this.repository.put( "lp", MiraLifePool.class );
  }
  
  @Override
  @NotNull
  public
  Class<? extends MiraGameModeModel<MiraVersePulse>> game_mode_class( @NotNull String game_mode_label )
  {
    if ( !this.repository.containsKey( game_mode_label ) )
    {
      throw new IllegalArgumentException( "unknown game mode label '%s'?".formatted( game_mode_label ) );
    }
    
    return this.repository.get( game_mode_label );
  }
  
  @Override
  @NotNull
  public
  MiraGameModeModel<MiraVersePulse> game_mode(
    @NotNull MiraVersePulse pulse,
    @NotNull MiraMatchModel<MiraVersePulse> match,
    @NotNull String game_mode_label )
  {
    Class<? extends MiraGameModeModel<MiraVersePulse>> game_mode_class =
      this.game_mode_class( game_mode_label );
    
    try
    {
      return game_mode_class.getDeclaredConstructor(
        MiraVersePulse.class,
        MiraMatch.class ).newInstance( pulse, match );
    }
    catch ( InstantiationException | IllegalAccessException | InvocationTargetException |
            NoSuchMethodException exception )
    {
      throw new IllegalStateException(
        "could not instantiate game mode '%s'".formatted( game_mode_label ),
        exception );
    }
  }
}

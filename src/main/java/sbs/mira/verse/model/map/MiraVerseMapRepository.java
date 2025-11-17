package sbs.mira.verse.model.map;

import org.jetbrains.annotations.NotNull;
import sbs.mira.core.model.map.MiraMapModel;
import sbs.mira.core.model.map.MiraMapRepository;
import sbs.mira.core.model.match.MiraMatchModel;
import sbs.mira.verse.MiraVersePulse;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public
class MiraVerseMapRepository
  implements MiraMapRepository<MiraVersePulse>
{
  @NotNull
  private final Map<String, Class<? extends MiraMapModel<MiraVersePulse>>> repository;
  
  public
  MiraVerseMapRepository( )
  {
    this.repository = new HashMap<>( );
    this.repository.put( "battlement", Battlement.class );
    this.repository.put( "clash_of_clay", ClashOfClay.class );
  }
  
  @Override
  @NotNull
  public
  Class<? extends MiraMapModel<MiraVersePulse>> map_class( @NotNull String map_label )
  throws IllegalArgumentException
  {
    if ( !this.repository.containsKey( map_label ) )
    {
      throw new IllegalArgumentException( "unknown map label '%s'?".formatted( map_label ) );
    }
    
    return this.repository.get( map_label );
  }
  
  public @NotNull
  MiraMapModel<MiraVersePulse> map(
    @NotNull MiraVersePulse pulse,
    @NotNull MiraMatchModel<MiraVersePulse> match,
    @NotNull String map_label )
  {
    Class<? extends MiraMapModel<MiraVersePulse>> map_class = this.map_class( map_label );
    
    try
    {
      return map_class.getDeclaredConstructor(
        MiraVersePulse.class,
        MiraMatchModel.class ).newInstance(
        pulse,
        match );
    }
    catch ( InstantiationException | IllegalAccessException | InvocationTargetException |
            NoSuchMethodException exception )
    {
      throw new IllegalStateException(
        "could not retrieve map '%s'".formatted( map_label ),
        exception );
    }
  }
}

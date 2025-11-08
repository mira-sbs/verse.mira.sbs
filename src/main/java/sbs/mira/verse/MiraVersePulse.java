package sbs.mira.verse;

import org.jetbrains.annotations.NotNull;
import sbs.mira.core.MiraPulse;

public final
class MiraVersePulse
  extends MiraPulse<MiraVersePlugin, MiraVerseDataModel>
{
  public
  MiraVersePulse( @NotNull MiraVersePlugin plugin, @NotNull MiraVerseDataModel master )
  {
    super( plugin, master );
  }
  
  
  @Override
  public @NotNull
  MiraVersePlugin plugin( )
  {
    if ( this.plugin != null )
    {
      return plugin;
    }
    else
    {
      throw new FlatlineException( "not yet breathing." );
    }
  }
  
  @Override
  public @NotNull
  MiraVerseDataModel model( )
  {
    if ( this.model != null )
    {
      return this.model;
    }
    else
    {
      throw new FlatlineException( "not yet breathing." );
    }
  }
}

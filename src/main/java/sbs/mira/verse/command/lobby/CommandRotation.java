package sbs.mira.verse.command.lobby;

import app.ashcon.intake.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import sbs.mira.core.model.MiraCommandModel;
import sbs.mira.core.model.configuration.MiraMapRotationModel;
import sbs.mira.core.model.map.MiraMapModel;
import sbs.mira.verse.MiraVersePulse;

public
class CommandRotation
  extends MiraCommandModel<MiraVersePulse>
{
  /**
   * tbd.
   *
   * @param pulse tbd.
   */
  public
  CommandRotation( @NotNull MiraVersePulse pulse )
  {
    super( pulse );
  }
  
  @Command (
    aliases = { "rotation", "rot" },
    usage = "[]",
    desc = "view the current map rotation.",
    help = "help tbd."
  )
  public
  void rotation( @NotNull CommandSender sender )
  {
    MiraMapRotationModel<MiraVersePulse> map_rotation =
      this.pulse( ).model( ).lobby( ).map_rotation( );
    
    String[] rotation_map_labels = map_rotation.values( );
    int current_rotation_index = map_rotation.index( );
    int next_rotation_index = current_rotation_index + 1;
    
    if ( next_rotation_index >= rotation_map_labels.length )
    {
      next_rotation_index = 0;
    }
    
    boolean is_current_map_set = this.pulse( ).model( ).lobby( ).match( ).was_manually_set( );
    boolean is_next_map_set = this.pulse( ).model( ).lobby( ).map_rotation( ).set_next_map( );
    
    sender.sendMessage( this.pulse( ).model( ).message( "match.rotation.header" ) );
    
    for ( int rotation_index = 0; rotation_index < rotation_map_labels.length; rotation_index++ )
    {
      boolean is_current_map = rotation_index == current_rotation_index;
      boolean is_next_map = rotation_index == next_rotation_index;
      
      String map_label = rotation_map_labels[ rotation_index ];
      // todo: retrieve map name without loading map.
      String map_name = rotation_map_labels[ rotation_index ];
      
      if ( is_current_map )
      {
        if ( is_current_map_set )
        {
          sender.sendMessage(
            this.pulse( ).model( ).message( "match.rotation.item", map_label, map_name, "" ) );
          
          MiraMapModel<MiraVersePulse> current_map =
            this.pulse( ).model( ).lobby( ).match( ).map( );
          
          sender.sendMessage(
            this.pulse( ).model( ).message(
              "match.rotation.one_off.item",
              current_map.label( ),
              current_map.display_name( ),
              "[current]" ) );
        }
        else
        {
          sender.sendMessage(
            this.pulse( ).model( ).message(
              "match.rotation.item",
              map_label,
              map_name,
              "[current]" ) );
        }
      }
      else if ( is_next_map )
      {
        if ( is_next_map_set )
        {
          sender.sendMessage(
            this.pulse( ).model( ).message( "match.rotation.item", map_label, map_name, "" ) );
          
          String set_next_map_label =
            this.pulse( ).model( ).lobby( ).map_rotation( ).next_map_label( );
          
          sender.sendMessage(
            this.pulse( ).model( ).message(
              "match.rotation.one_off.item",
              set_next_map_label,
              set_next_map_label,
              "[next]" ) );
        }
        else
        {
          sender.sendMessage(
            this.pulse( ).model( ).message(
              "match.rotation.item",
              map_label,
              map_name,
              "[next]" ) );
        }
      }
      else
      {
        sender.sendMessage(
          this.pulse( ).model( ).message( "match.rotation.item", map_label, map_name, "" ) );
      }
    }
    
    if ( is_current_map_set )
    {
      sender.sendMessage( this.pulse( ).model( ).message( "match.rotation.one_off.current" ) );
    }
    
    if ( is_next_map_set )
    {
      sender.sendMessage( this.pulse( ).model( ).message( "match.rotation.one_off.next" ) );
    }
  }
}

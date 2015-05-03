Plugin is based on Tiled Java (http://www.mapeditor.org/).
It's ported to use SWT and adapted to eclipse editor/view part system.

Project is aimed at creating Tiled Eclipse integration

Please visit [Download](http://code.google.com/p/eclipse-tiled-mapper/downloads/list) page to get latest update site arcive to install plugin.

Released ver. 0.1.6

Please read [how to install plug-in from zip archive](http://code.google.com/p/eclipse-tiled-mapper/wiki/InstallingPlugIn).

### History ###
Once upon a time I (as many geeks/IT students) was thinking about creating my own game for Android platform - like RPG with swords, knights & castles - or just a framework for fastly creating such a games.
After some time I've realized, that I have insufficent time for such project, I'm not an artist (& using free art is insufficient to create such a game), I have only an idea, but no detailed description of world, heroes etc...

But before I was thinking about creation of maps for such a game & found [AndEngine](http://www.andengine.org/). This Android game Engine has tmx map format support, and, as I later discovered, this format is supported by several free/open source engines & there's already a [map editor for it](http://www.mapeditor.org/)

Mapeditor (`TilEd`) is written in C++ with Qt but there's also Java version of `TilEd`, which is no longer maintained. I've decided to use it as basics for Eclipse plug-in which would provide TMX map editor for my game framework (I was thinking about eclipse-based tooling for it)
So, I've ported `TilEd` java version from Swing to SWT (which is native for Eclipse) & created first version of plug-in.

Bit later an idea of RPG game framework was abandoned, but I  think that such editor would be convinient for guys, who develop their Android/Java games under Eclipse - they'll have an ability to edit TMX files in Eclipse IDE, without launching some external programs, refreshing workspace etc.

### Current state & known issues ###

I've planned to test editor & determine necessary features while creating maps for my own game, but now I haven't a time to test editor & quickly determine it's problems. New features are added accroding to ones provided by [original TilEd](http://www.mapeditor.org/).

  * Any bug reports, feature requests & other suggestions are welcome. It was planned, that plug-in will be tested while making my own game, but currently the idea of this game is abandoned & unfortunately I don't have time to test my editor deeply.
  * I'm not a graphical artist, so I use free icons from the Eclipse or found via Internet. If someone could help with icons - it would be great
  * I'm not a writer (& my English isn't a dream anyway). If someone can help with creating Help or HOWTO articles - it would be great too
  * Currently only orthogonal map editing support is rather full. Hexagonal & Isometric maps support is planned, but can't give some explicit shedule for it
  * Tile editing - unable to move group of tiles
  * No polygons/lines support (`TilEd Qt` has such support)
  * Keybindings for some actions do not work
# Blimpy Mod for Minecraft NeoForge

![logo](./logo.png)

This was a NeoForge port of [LittleLogistics](https://littlelogistics.murad.dev/) but now it only features blimps.


## License

### Source Code / java files

LGPLv3
https://www.gnu.org/licenses/lgpl-3.0.en.html

All assets are used with permission from the original authors of the Little Logistics project. 

New models made by [pega](https://www.fiverr.com/s/38y2BGL) are published under the same license as the source code.  

## Development

#### Adding an entity

* create a recipe in ModRecipeProvider
* create an Entity Model (e.g. SubmarineEntity)
* register the entity in ModEntityTypes
* register the model for rendering in ModClientEventHandler
* register events in ModEventBusEvents
* register a creative mode tab in ModItems
* add an entry in ModItemModelProvider
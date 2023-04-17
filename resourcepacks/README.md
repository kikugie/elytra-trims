# Adding elytra trims
Default trim resourcepack works with any elytra texture and cape as long as shape of the elytra is not changed.  
If a trim is applied without matching texture it will show an error in the smithing table, **but will render as a blank elytra in the world**

## Modifying existing trims
If your resourcepack/datapack doesn't add any new trim pattern items you only need to add textures to `minecraft/textures/trims/elytra`. Files should be named as the trim itself.  

Optionally, add overlay for elytra icon in `minecraft/textures/trims/items/elytra_trim.png`.  

When making trim texture, not that it doesn't work as a mask, but as rather another texture on top of the model. For texturing use `trim_palette.png` provided in this directory. Any other color will be rendered as is, not being affected by trim material.  

Using this method both default and custom resourcepacks are required.

## Adding custom trim pattern/material support
Custom patterns have to modify `minecraft/atlases/armor_trims.json` adding paths for additional patterns and materials.  
`minecraft/atlases/armor_trims.json` and configuration files in `assets/minecraft/models/item` need to be modified to allow item overlay support for custom trim materials.

The only restriction mod has on trim patterns is textures **must** be in `minecraft/textures/trims/elytra` directory. This is hardcoded, same as vanilla armor trims.

## Custom Entity Model support
### TBA
This mod wasn't tested with custom models from OptiFine or any other mod.  
If you're a resourcepack developed having issues with it, contact me with an issue report or via Discord (KikuGie#7003)
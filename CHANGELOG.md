## Added
- Runtime trim generation

**This is an experimental feature. Please report any issues with it on the GitHub page!**  
*Like, really. I don't know how it will behave in many possible combinations of other mods and datapacks*

### Explanation:
This feature listens for every registered armor trim and attempts to register it again, but for elytra's path. As the result, you don't need to specify `armor_trims.json` atlas source.

A specialised resourcepack is still needed to provide textures for trim patterns, but it works well for datapacks or mods adding new materials.

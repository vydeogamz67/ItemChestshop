# ItemChestShop

A modern Paper Minecraft plugin for 1.21.4 that implements item-for-item chest shops without requiring an economy plugin. Built with the latest Adventure API for enhanced text formatting and future compatibility.

## ✨ Features

- **🔄 Item-for-Item Trading**: Trade items directly without needing Vault or economy plugins
- **📝 Simple Sign Format**: Easy-to-use sign format for creating shops
- **✅ Automatic Validation**: Validates item names and amounts automatically
- **📦 Stock Management**: Automatically checks shop stock and inventory space
- **🔔 Owner Notifications**: Shop owners get notified when their shops make sales
- **🔐 Permission System**: Configurable permissions for creating and using shops
- **⚙️ Admin Commands**: Reload configuration, view statistics, and manage shops
- **🎨 Modern Text System**: Uses Adventure API for rich text formatting and colors
- **🚀 Future-Proof**: Built with modern APIs for long-term compatibility

## 🛠️ How to Use

### Creating a Chest Shop

1. Place a chest where you want your shop
2. Place a sign on or above the chest
3. Write on the sign:
   - **Line 1**: Your username (or leave empty - it will auto-fill)
   - **Line 2**: `Want: [amount] [item]` (what you want to receive)
   - **Line 3**: `Give: [amount] [item]` (what you're selling)

### Example Shop Sign

```
VydeogamzXD
Want: 64 Bread
Give: 1 Diamond
```

This shop will trade 1 Diamond for 64 Bread.

### Using a Chest Shop

- **Right-click** the sign to make a purchase
- **Shift + Right-click** the sign to view shop information
- You must have the required items in your inventory to make a trade
- You cannot buy from your own shops

### Supported Item Names

The plugin supports standard Minecraft item names and common aliases:
- `Diamond`, `Gold`, `Iron`, `Coal`
- `Bread`, `Wheat`, `Apple`, `Beef`
- `Wood`, `Stone`, `Dirt`, `Cobblestone`
- `Oak_Log`, `Oak_Planks`, `Stick`
- And many more...

## 📋 Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/itemchestshop help` | Show help information | `itemchestshop.use` |
| `/itemchestshop info` | Show plugin information | `itemchestshop.use` |
| `/itemchestshop stats` | Show your shop statistics | `itemchestshop.use` |
| `/itemchestshop list [page]` | List all shops on the server | `itemchestshop.use` |
| `/itemchestshop find <item>` | Find shops selling a specific item | `itemchestshop.use` |
| `/itemchestshop reload` | Reload configuration | `itemchestshop.admin` |

## 🔐 Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `itemchestshop.create` | Allow creating chest shops | `true` |
| `itemchestshop.use` | Allow using chest shops | `true` |
| `itemchestshop.admin` | Allow admin commands and breaking others' shops | `op` |

## ⚙️ Configuration

The plugin creates a `config.yml` file with customizable:
- Messages and text colors
- Maximum shops per player
- Debug mode settings
- Item aliases and parsing options

## 📥 Installation

1. Download the plugin JAR file
2. Place it in your server's `plugins` folder
3. Restart your server
4. Configure permissions and settings as needed

## 🔧 Building from Source

### Requirements
- **Java 17** or higher
- **Maven 3.6+**
- **Paper API 1.21.4** (automatically downloaded)

### Build Commands
```bash
# Clean and compile
mvn clean compile

# Build JAR with dependencies
mvn clean package

# Skip tests (if any)
mvn clean package -DskipTests
```

The compiled JAR will be in the `target` folder as `ItemChestShop-1.0.0.jar`.

## 🏗️ Technical Details

### Dependencies
- **Paper API 1.21.4** - Core server API
- **Adventure API** - Modern text formatting (included with Paper)
- **Gson 2.10.1** - JSON serialization for data storage

### Architecture
- **Command System**: Centralized command handling with subcommands
- **Event Listeners**: Sign creation/destruction and shop interaction
- **Data Management**: JSON-based persistent storage
- **Transaction System**: Safe item exchange with rollback support
- **Permission Integration**: Full Bukkit permission system support

### Recent Updates
- ✅ **Modernized to Adventure API** - Replaced deprecated ChatColor with modern Adventure Components
- ✅ **Enhanced Maven Configuration** - Improved build process with proper dependency shading
- ✅ **Code Quality Improvements** - Fixed compilation issues and improved error handling
- ✅ **Future Compatibility** - Built for long-term compatibility with Paper updates

## 🐛 Troubleshooting

### Common Issues
- **"No permission" errors**: Check that players have `itemchestshop.use` permission
- **Shop not working**: Ensure chest is directly below the sign (1-2 blocks)
- **Items not recognized**: Use exact Minecraft item names (case-insensitive)
- **Build errors**: Ensure Java 17+ and Maven are properly installed

### Debug Mode
Enable debug mode in `config.yml` to see detailed logging:
```yaml
debug: true
```

## 📞 Support

For issues, suggestions, or questions:
- Check the plugin's documentation
- Review the configuration file
- Enable debug mode for detailed logs
- Contact the development team

## 📄 License

This plugin is provided as-is for educational and server use purposes. Built with ❤️ for the Minecraft community.

---

**Version**: 1.0.0  
**Minecraft Version**: 1.21.4  
**Server Software**: Paper (recommended), Spigot compatible  
**Java Version**: 17+
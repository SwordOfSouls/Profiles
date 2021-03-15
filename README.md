# Profiles
Profiles is a plugin that allows players to save their progress to up to 3 save slots. When a player joins the server, they will be prompted to either start a new save or continue from a previous save.

<img src="https://user-images.githubusercontent.com/52573645/111053091-f218b680-842e-11eb-98a2-7701359d01c4.png" width="45%"></img> <img src="https://user-images.githubusercontent.com/52573645/111053092-f218b680-842e-11eb-8cb9-e457870230e2.png" width="45%"></img>

<img src="https://user-images.githubusercontent.com/52573645/111102872-79dfed00-8523-11eb-8724-e72fb97eb79a.png" width="30%"></img> <img src="https://user-images.githubusercontent.com/52573645/111102873-7a788380-8523-11eb-8dd5-37b3fad58383.png" width="30%"></img> <img src="https://user-images.githubusercontent.com/52573645/111102874-7a788380-8523-11eb-800f-a7d61e9e2e0c.png" width="30%"></img>

Features
  - Players have ability to save, load and delete up to 3 saves.
  - 'Fresh start'
    - (If enabled in config.yml) the player will start from scratch at the default world's spawn location.
  - Each save contains the following data:
    - Player display name
    - Date saved was made
    - Health level
    - Hunger level
    - XP Level / XP Points towards next level
    - The Player's inventory (contents and armor)
    - The Player's ender chest inventory
    - The Player's location (world, x, y, z)
  - Preview inventories for saved profile
    - Shift left-click to view saved player inventory
    - Shift right-click to view saved ender chest inventory

Commands
  - /profiles : Main plugin command, displays list of other commands.
  - /profiles open : Opens the Profiles main menu.
  - /profiles reload : Reloads the Profiles configuration.

Permissions
  - profiles.* : Gives access to all Profiles features/commands.
  - profiles.reload : Allows you to reload the plugin configuration.

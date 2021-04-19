
# Change Log
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## [2.2.0] - 04-19-2021

Make GUI elements customizable.

## [2.1.0] - 03-28-2021

Add support for legacy versions of Minecraft (1.8.8 - 1.16.x).

## [2.0.0] - 03-25-2021

Move data section from config.yml to data.yml.

## Changed
- Major breaking change. Plugin users WILL need to copy over data section from config.yml to data.yml manually to persist old data.

## [1.9.7] - 03-21-2021

Dispatch n-commands from config.yml list on save, load or delete of profiles.

## [1.8.7] - 03-21-2021

Extend save, load and delete functionality to command-line.

### Fixed
- Bug where the delete functionality would cause a profile to be auto saved.

## [1.7.7] - 03-18-2021

Found issue where auto save was being invoked when player died. Emptying inventory, setting health to 0, etc.

### Fixed
- Added check if inventory is a 'Crafting' table before trying to auto-save.
- Added open/close logic on leave to force auto save.

## [1.7.6] - 03-18-2021

Auto save the Profile when the players inventory changes (ideally fixing most dupe issues)

## [1.6.6] - 03-17-2021

Clean unsafe data before loading the profile.

### Fixed
- IllegalArgumentException when unsafe values for health, hunger, xpLevel and xpPoints were provided from config.yml.

## [1.6.5] - 03-17-2021

Add 'Save on Player leave' feature.

### Added
- Added 'shouldSaveOnPlayerLeave' configuration option.
- Updated load / save profile messages to indicate which slot they are on.

## [1.5.5] - 03-16-2021

Fix bug where players would not see items render in inventory on load

### Changed
- Apparently, opening an inventory needs to be in a runnable on join event.

## [1.5.4] - 03-14-2021

Fix bug where players could add items to the preview inventories.

## [1.5.3] - 03-14-2021

Add 'Preview inventory' feature.

### Added
- On shift left-click of save slot, preview the saved player inventory (contents/armor).
- On shift right-click of save slot, preview the saved player ender chest inventory.

## [1.4.3] - 03-14-2021

Add 'fresh start' mechanic. If enabled, saving on an empty save slot will respawn player with everything reset.

### Added
- 'Fresh start' mechanic.
- Configurable options for 'Fresh start' mechanic.

## [1.3.3] - 03-14-2021

Remove prettify logic for dateSaved property. Need to figure out better implementation for CET timezone.

### Changed
- dateSaved property removed from lore text.

## [1.3.2] - 03-13-2021

Additional cleanup. Fix NPE for out of inventory click.

## [1.3.1] - 03-13-2021

Refactor logic to use less redundant code.

## [1.2.1] - 03-12-2021

Clean up all warnings and typos in main class

### Changed
- Applied all quick fixes from IDE to main Profiles class

## [1.2.0] - 03-12-2021

Add CHANGELOG.md to better track changes. Update date format to be prettier.

### Added
- Add CHANGELOG.md.

### Changed
- Updated date format to be prettier (23:19:12 -> 11:19:12 PM)

## [1.1.0] - 03-12-2021

Add proper versioning to plugin. Outline permissions for plugin.

### Added
- Updated project version to be 1.1.0.
- Refactored profiles-admin to profiles.* with profiles.reload as a child permission.

## [1.0.0] - 03-12-2021

First working version of the plugin. Core functionality works as intended.

## [1.2.4] - 2017-03-15

Here we would have the update steps for 1.2.4 for people to follow.

### Added

### Changed

- [PROJECTNAME-ZZZZ](http://tickets.projectname.com/browse/PROJECTNAME-ZZZZ)
  PATCH Drupal.org is now used for composer.

### Fixed

- [PROJECTNAME-TTTT](http://tickets.projectname.com/browse/PROJECTNAME-TTTT)
  PATCH Add logic to runsheet teaser delete to delete corresponding
  schedule cards.

## [1.2.3] - 2017-03-14

### Added

### Changed

### Fixed

- [PROJECTNAME-UUUU](http://tickets.projectname.com/browse/PROJECTNAME-UUUU)
  MINOR Fix module foo tests
- [PROJECTNAME-RRRR](http://tickets.projectname.com/browse/PROJECTNAME-RRRR)
  MAJOR Module foo's timeline uses the browser timezone for date resolution 

# Change Log
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

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
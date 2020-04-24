## [Unreleased]
### Added
- docker support - see readme.md for more information about docker
### Changed
- Default config file name from pathsDKTK.xml to paths.xml
- Updated dependency de.samply.common-config from 2.3.0 to 3.1.0
- Updated dependency de.samply.common-http from 3.0.0 to 6.1.1
### Fixed
- an issue caused by mixed dependencies of jersey 1 and 2. As a workaround until completely switching to jersey 2, the package jsr311-api will be excluded.

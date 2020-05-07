## [Unreleased]

## [0.0.1] - 2020-05-07
### Added
- docker support - see readme.md for more information about docker
- Support for CORS Requests. The CORS access can be specified by using PATHS_ALLOWED_ORIGINS and PATHS_ALLOWED_CORS_HEADERS environment variables.
- Support redirect response of mainzelliste
- Support conflict response of Mainzelliste (unsure cases) 
- Support the creation of tentative patients (sureness flag)
- Map web application exception without entity to HTTP error response in JSON format
### Changed
- Default config file name from pathsDKTK.xml to paths.xml
- Updated dependency de.samply.common-config from 2.3.0 to 3.1.0
- Updated dependency de.samply.common-http from 3.0.0 to 6.1.1
### Fixed
- An issue caused by mixed dependencies of jersey 1 and 2. As a workaround until completely switching to jersey 2, the package jsr311-api will be excluded.
- Remove dependency de.samply.common-http

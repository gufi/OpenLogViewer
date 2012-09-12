# Releases

Releases are performed by one person with appropriate credentials to deploy the
artifacts and upload the documentation. For a release to occur in Maven a number
of things must be true.

 - The application builds without any errors
 - Repository clean - no uncommitted changes
 - Repository shared - no local changes unpushed
 - All unit tests pass

Additionally the following conditions must be checked by the release manager.
All of these are to be done after the last commit to be included in the release.
If new commits go in to remedy issues that are found, all checks should be done
again on the latest code base.

 - All issues with target == next version are not only resolved, but also closed
 - No known regressions from last release present in current build
 - No known regressions in features added since the last release
 - No inappropriate string literals shall be present in the code base
 - All localisation files complete and contain no English, except the English one
 - No commented out code shall be present in the code base
 - No SNAPSHOT versions shall be depended upon, including plugins
 - mvn site:site style counts are equal to, or better than the last release
 - findbugs shows no serious issues and equal to, or less than the last release
 - Test data-log files load correctly and display correctly for all decoders
 - mvn install on mac produces working bin.jar, linux.tar.bz2, .deb, .exe, .dmg
 - Windows exe executes and displays properly on XP and 7
 - Linux zip and deb function correctly and display properly on a variety of wms
 - Mac dmg and app.zip function correctly and display properly on 10.6 and 10.7

Note, issues listed for future releases beyond the release in question can, and
likely are present in the code that is about to be released. This is normal and
completely OK.

# Platform Check List

 - Full-screen entry/exit for each platform
 - Button behaviour for each platform
 - Key bindings for each platform
 - Settings file/dir behaviour on each platform
 - Panel sizes make sense and don't affect functionality

# Post Release

 - Update all repos
 - Update the website
 - Update the issue tracker
 - Announce on forum and facebook
 - Tag SNAPSHOT version for git describe
 - git push --tags to all repos


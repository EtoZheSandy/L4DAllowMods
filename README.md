# L4D Allow Mods

**L4D Allow Mods** is a desktop utility for **Left 4 Dead 2** players who want finer control over Steam Workshop addons. It helps prepare selected `.vpk` addons, update the game's `gameinfo.txt`, and quickly enable or disable the prepared addon setup from a clean Windows desktop interface.

[Download latest release](https://github.com/EtoZheSandy/L4DAllowMods/releases) | [GitHub Pages](https://etozhesandy.github.io/L4DAllowMods/) | [Report an issue](https://github.com/EtoZheSandy/L4DAllowMods/issues)

If this project saved you time, helped with your setup, or just made modding less annoying, please give the repository a star. It helps other L4D2 players find the tool.

## Screenshots

![Start screen](docs/start.png)

| Addons | Settings |
| --- | --- |
| ![Addons screen](docs/addons.png) | ![Settings screen](docs/settings.png) |

![FAQ screen](docs/faq.png)

## Highlights

- Enable and disable selected Workshop addons from one place.
- Scan the `left4dead2/addons/workshop` folder and show addon details.
- Search, sort, and manage addon visibility in a dedicated Addons screen.
- Restore or refresh cached `gameinfo.txt` data when needed.
- Keep selected addons and settings between launches.
- Auto-hide prepared mods after game launch.
- Localized UI with English, Russian, Chinese, Spanish, Arabic, Japanese, French, and German resources.
- No launcher, DLL injection, or runtime memory patching.

## How It Works

L4D Allow Mods prepares selected Workshop `.vpk` files and updates the Left 4 Dead 2 `gameinfo.txt` paths so the game can load those addons. The app keeps a cached copy of the original `gameinfo.txt`, which allows you to disable addons and restore the previous state from the UI.

The workflow is file-based: the tool does not hook the game process, inject code, or patch game memory while Left 4 Dead 2 is running.

## Quick Start

1. Download the latest Windows build from [Releases](https://github.com/EtoZheSandy/L4DAllowMods/releases).
2. Open the app and choose your Left 4 Dead 2 installation folder.
3. Let the tool find `left4dead2/addons/workshop`, or apply the path manually.
4. Open the Addons screen and select the Workshop addons you want to use.
5. Click **Enable Addons** before launching the game.
6. Click **Disable Addons** when you want to restore the normal setup.

Avoid enabling every addon at once. Left 4 Dead 2 was not designed for that kind of load, and the game may crash.

## Tech Stack

- Kotlin Multiplatform
- Jetpack Compose Desktop
- Kotlin Coroutines
- Kotlinx Serialization
- Gradle
- Windows MSI/EXE packaging

## Disclaimer

This project is provided for educational and personal-use purposes. Use it responsibly, respect server rules, and keep backups of important game files when experimenting with mods.

## Star The Project

L4D Allow Mods is built for players who like tinkering with their game setup. If it helped you, please star the repository on GitHub:

[Star EtoZheSandy/L4DAllowMods](https://github.com/EtoZheSandy/L4DAllowMods)

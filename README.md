# "Storytelling Engine" demo android application

## Android app for telling character driven kinetic stories 📖.

[See the application webstie.](https://morningbird.eu/app/storytellingengine)

### About

Storyteling Engine is a native, easy to edit, base Android application for creating character driven kinetic novels.
        
The application adapts to the script in the JSON file where you can easily define characters, scenes and used sound effects. Other changes can be made by editing selected configuration files and images in the resources and assets directories. The entire application is open source and can be adapted to suite your project.

The available demo application shows all current capabilities of the "engine" - it is an example of what it can be used for and a helpful starting point for editing.

### Get it

* [Google Play Store](https://play.google.com/store/apps/details?id=eu.morningbird.storytellingengine)
* [Source code from Github](https://github.com/michaldaniel/storytelling-engine-android/archive/master.zip)

### Screenshots

<img src="https://user-images.githubusercontent.com/1345297/71464463-bb1e9c80-27b9-11ea-820e-a9c3e54b948e.png" alt="drawing" width="256"/> <img src="https://user-images.githubusercontent.com/1345297/71464464-bbb73300-27b9-11ea-9d2b-5fa338edd2d7.png" alt="drawing" width="256"/> <img src="https://user-images.githubusercontent.com/1345297/71464465-bbb73300-27b9-11ea-9238-6e5070119c2a.png" alt="drawing" width="256"/>

### Features

- Native Android applicaton written in Kotlin using best mobile development practices
- Entierly driven by the JSON defined script
- Automatic character, background and text animations
- Character speach bubbles for dialog driven stories
- Full screen narration messages
- Scene background music, leave, enter and message sound effects
- Robust, easy to configure About section
- Automatic saves and chapters unlocking
- Chapter selection and rewind menu
- Music and sound effects volumes settings
- Text size and screen reader accessibility options
- Final credits roll

### Usage
 
To create your own game based on this application you will need the Android development environment and some basic programming skills.

For adaptation, just edit the JSON scenario, images and sounds in the assets and resources directories, and several configuration files associated with the structure of the Android application. Then use Android Studio rename feature to change package name and remove any mentions of my name, developer name and contact information.

The application is written in Kotlin and uses the MVVM architecture pattern. Further editing is easy by modifying the layout XML files and the corresponding view models.

### Json structure

```
{
  "version": Int,
  "credit": [
    {
      "name": String, // Credit section title
      "member": [
        String, // Credited person
        (...)
      ]
    },
    (...)
  ],
  "character": [
    {
      "name": String, // Identifier
      "tag": String, // Displayed ingame
      "color": Hexadecimal color string, // Used as UI tint
      "sprite": [
        {
          "name": String, // Identifier
          "graphic": String // Path to assets image
        },
        (...)
      ]
    },
    (...)
  ],
  "scene": [
    {
      "background": String, // Path to assets image
      "intro": String, // Path to assets sound file
      "outro": String, // Path to assets sound file
      "music": String, // Path to assets sound file
      "chapter": Boolean,
      "name": String,
      "present": {
        "left": [
          String, // Character name
          (...)
        ],
        "right": [
          String, // Character name
          (...)
        ]
      },
      "message": [
        {
          "character": {
            "name": String, // Character identifier
            "sprite": String // Sprite identifier
          },
          "sound": String, // Path to assets sound file
          "fullscreen": Boolean
          "text": String
        },
        (...)
      ]
    },
    (...)
  ]
}              
```

#### Editing rules

- Application will reload script when version number increases.
- Scene intro, outro and music are optional.
- Music is played in a loop, intros, outros and sound once.
- Scene chapter and name elements are optional and used to mark chapters for main menu.
- Scene can have no characters present.
- Not present characters can speak in a scene.
- Not present characters don't require sprite definition.
- Message sound is optional.
- Message fullsceen flag is optional, if true will be displayed as narration and no character definition is required.
- Application will do it's best to adapt to any other missing resources and declarations

### File structure

List of notable, important files for adapting.

- `app/src/main/res/values/colors.xml`   
   Parameterized color definitions used in the application.
- `app/src/main/res/values/strings.xml`   
   Parameterized string definitions used in the application.
- `app/src/main/res/drawable/`   
   You should edit: bg_credits.jpg, bg_titlescreen.jpg, img_logo.png
- `app/src/main/res/`   
   Adaptive launcher icon files, check Android documentation.
- `app/src/main/assets/`   
   Place for files referenced in JSON and configuration.
- `app/src/main/java/eu/morningbird/storytellingengine/model/Settings.kt`   
   Default message sound, title screen music, credits music, play script file and version are defined here as constant values. Breaking version cleares save files.
- `app/build.gradle`   
   DEVELOPER_NAME configuration for "more" action, application id, version code and version name.
- `app/src/main/java/eu/morningbird/storytellingengine/viewmodel/AboutViewModel.kt`   
   Populates About section with elements.
        
##### I kindly ask everyone to modify package name and remove all mentions of my name, developer name and contact information from sources pirior to distributing modifications.


### Support

#### [Report a bug 🐛](https://github.com/michaldaniel/storytelling-engine-android/issues/new)

File a bug reports or request new features.

#### [General questions and feedback ❤️](mailto:contact@michaldaniel.eu)

Email me or contact directly if you have any questions, suggestions or general feedback regarding the application or this website.

### Contact

* My website: [www.morningbird.eu](https://morningbird.eu)
* Mail: [contact@michaldaniel.eu](mailto:contact@michaldaniel.eu)
* Telegram: [@morningbird](https://telegram.me/morningbird)
* Other: [Github](https://github.com/michaldaniel), [Linkedin](https://www.linkedin.com/in/michalpiotrdaniel)

### Planned improvements

* [ ] Refactoring version and breakage tracking
* [ ] Split JSON into smaller files for quick parsing in Chapters and Credits sceen.
* [ ] Parametrisize everything in JSON files and/or single Settings file.
* [ ] Chain scenes together using identifiers.
* [ ] Add ability to jump between sceeens using choices.
* [ ] Make choice a message level element, add ability to use basic logic for scene navigation.

### License

```
MIT License

Copyright (c) 2019 Michał Daniel

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

### Attributions

Character sprites, music from assets and icons from resources are excluded from the license. Character sprites from ["College Life"](https://puppetbomb.itch.io/college-students-sprite-pack) by puppetbomb. Full attributions are aviable in application about section.

### Legal

* [Application privacy policy 🕵️](https://morningbird.eu/app/storytellingengine/privacy)


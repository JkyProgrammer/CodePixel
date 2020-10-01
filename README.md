# CodePixel

CodePixel is a Java project aiming to produce simple and rather nice looking patterns based on random and logical growth, evolution, and movement rules.

## 1.0.0 and before
In the old version (release 1.0.0) the rules were limited to:
* agecol - colour the pixel based on how close it is to its age limit
* evocol - colour the pixel based on a hue which is passed on to children
* smrtbrd - clone the pixel automaticallly into available spaces around it
* leapbrd - clone the pixel into a available space a long way away (creating a colony)

In this version, execution was limited to single-threading.

## Beyond 1.0.0
In the new version, colouring rules will no longer instructions. The pixel cycle is going to be rewritten to make individual pixels more versatile and dynamic. It is possible that the instruction sequence will be removed entirely. The drawing mechanism will also be cleaned up (**DONE**), as will the structure of the code in general (**DONE**). Multi-threading will be implemented (**DONE**). At some point a major efficiency analysis will be conducted to slice off as much time as possible wherever it can be managed. The interface will also be improved upon, as well as the user experience.

## Releases
Sometimes.

## Credits
I used some images from a clip-art website for button images in 1.0.0 (these may be removed).

If you want me to add something, start a pull request or get in touch.

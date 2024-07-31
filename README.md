# Coalition of Powers Map Viewer

A Java desktop application that calculates and provides visualization of the relevant statistics and maps for Coalition of Powers.

## Pre-Requisites

You need Java installed. I have not tested what is the earliest compatible version but if the one you have doesn't work you should use the latest.

## Installation

1. Download mapviewer.zip from the latest release here: https://github.com/InYourHeart/mapviewer/releases
2. Unzip into your preferred folder

## Running

1. Double click the mapviewer.jar file

## Configuration

The application makes use of 5 color coded maps, found in the /maps folder in .png format. The .csv files in the /data folder specify the properties of the colors.

These are loaded on start up, so if you make alterations you will have to restart the application before the changes take effect.

Only the political and occupations maps are rendered, with the other 3 serving data purposes only.

### Political map (political.png)

Represents what pixels are owned by what Claims. 

The claims.csv file links the hex color values to the Claim names, as seen below. 

```
France;A7AAD7
Spain;C19B4A
Portugal;9BCC8B
```

Pixels with colors not found in the claims.csv are considered not owned by anyone.

### Terrain map (terrain.png)

Defines the base economic value of each pixel, primarily by a representation of its terrain.

The terrain.csv links each hex color to a name and base value of tax and manpower. Pixels with colors not found in this .csv are considered to have base values of 0.

```
Grassland;359950;10;10
River;354ca0;10;10
Dryland;B29950;6;6
```

Additionally, City pixels have their own unique colors which are linked to a name by the cities.csv file. 
The second and fourth entries are not used in calculations, but indicate the number of pixels and estimated population of the whole city.

```
Rome;16;404040;163000
Milan;12;40403F;124000
Naples;42;403F3F;419000
```

### Occupations map (occupations.png)

Represents what pixels are occupied by who. 

Uses the claims.csv file for occupations made by Claims. The 0x3F3F3F hex code is used for rebel occupations.

### Regions map (regions.png)

Can be used to represent Regions within Claims that provide a different ratio of resources.

The regions.csv file specifies the properties of the Region. It holds a name, hex, and modifiers applied to tax and manpower.

```
Lombardy;F8B972;1;0.75
```

### Devastation map (devastation.png)

Represents how devastated each pixel is. 

The value is a percentange from 0 to 100, and is represented by the pixel's saturation value in the HSV color format. 

Only pixels with the maximum red value of 255 in the RGB color format are counted.

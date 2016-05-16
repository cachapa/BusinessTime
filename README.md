# BusinessTime

[![API](https://img.shields.io/badge/API-14%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=14)
[![Build Status](https://travis-ci.org/cachapa/BusinessTime.svg?branch=master)](https://travis-ci.org/cachapa/BusinessTime)
[![License](https://img.shields.io/:license-gpl%20v3-brightgreen.svg?style=flat)](https://raw.githubusercontent.com/cachapa/BusinessTime/master/LICENSE)

A small Android app that automatically tracks your time at work.

The time tracking works by inserting timed events (at_work / left_work) into a local database. The actual time periods are calculated from the events by counting the time between at_work events and left_work for each working day.

This project includes few unit tests to help guarantee that the time calculations are accurate.

## Detection

Currently the only method to feed events into the database is via WiFi detection: new events are automatically generated when your phone (dis)connects to a pre-configured WiFi network.
Other possible methods of detection would be through the use of bluetooth beacons, NFC, geofences, or a combination of the above. It might also make sense to plug into automation apps such as Llama.

## To do
- Create new events
- Edit existing events
- More detection types

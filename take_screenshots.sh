#!/bin/sh

./gradlew :app:installDebugAndroidTest
adb shell am instrument -w -r   -e debug false -e class au.com.umranium.espconnect.Screenshots au.com.umranium.espconnect.test/au.com.umranium.espconnect.TestRunner

adb shell "cp /data/data/au.com.umranium.espconnect/cache/*.png /sdcard/DCIM/"

mkdir -p screenshots
adb pull /sdcard/DCIM/1-welcome-screen.png screenshots
adb pull /sdcard/DCIM/2-scanning-screen.png screenshots
adb pull /sdcard/DCIM/3-access-point-list-screen.png screenshots
adb pull /sdcard/DCIM/4-connecting-screen.png screenshots
adb pull /sdcard/DCIM/5-configure-screen.png screenshots
adb pull /sdcard/DCIM/6-configuring-screen.png screenshots
adb pull /sdcard/DCIM/7-end-screen.png screenshots




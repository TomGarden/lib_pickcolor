#!/usr/bin/env bash

# 关于 ： 语法 是在 PermissionDispatcher 学的，这个语法还是可以继续研究一下的
#../gradlew clean build
#../gradlew :LibPickColor:bintrayUpload

../gradlew :lib_pick_color:clean
../gradlew :lib_pick_color:build
../gradlew :lib_pick_color:publish
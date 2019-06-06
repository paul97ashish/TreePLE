#!/bin/bash

BUILD_DIR="release"

# Check if gradle is installed & in path
command -v gradle > /dev/null 2>&1 || { echo >&2 "Gradle is not installed or not in the path!"; exit 1; }
# Check if npm is installed & in path
command -v npm > /dev/null 2>&1 || { echo >&2 "npm is not installed or not in the path!"; exit 1; }

PROJECT_PATH=`pwd`
LOG_PATH="$PROJECT_PATH/build.log"
mkdir -p "$PROJECT_PATH/$BUILD_DIR"

# Build Android

cd "$PROJECT_PATH/TreePLE-Android"
echo "Building Android..."
gradle build > "$LOG_PATH" 2>&1
ANDROID_SUCCESS=$?

cd "$PROJECT_PATH/TreePLE-Backend"
echo "Building Backend..."
gradle :TreePLE-Admin:build >> "$LOG_PATH" 2>&1
ADMIN_SUCCESS=$?
gradle :TreePLE-Spring:build >> "$LOG_PATH" 2>&1
SPRING_SUCCESS=$?

cd "$PROJECT_PATH/TreePLE-Web"
echo "Building Website..."
npm install >> "$LOG_PATH" 2>&1
npm run build >> "$LOG_PATH"
WEB_SUCCESS=$?

cd "$PROJECT_PATH"
FAILURE=0
if (( ANDROID_SUCCESS == 0 )); then
    cp TreePLE-Android/app/build/outputs/apk/release/app-release-unsigned.apk $BUILD_DIR/
else
    FAILURE=$((FAILURE + 1))
fi

if (( ADMIN_SUCCESS == 0 )); then
    cp TreePLE-Backend/TreePLE-Admin/build/libs/TreePLE-Admin.jar $BUILD_DIR/
else
    FAILURE=$((FAILURE + 1))
fi

if (( SPRING_SUCCESS == 0 )); then
    cp TreePLE-Backend/TreePLE-Spring/build/libs/treePLE-1.0.0.war $BUILD_DIR/
else
    FAILURE=$((FAILURE + 1))
fi

if (( WEB_SUCCESS == 0 )); then
    cp -r TreePLE-Web/dist $BUILD_DIR/
else
    FAILURE=$((FAILURE + 1))
fi

if (( FAILURE == 0 )); then
    echo "All successfully built and copied to $BUILD_DIR!"
    rm "$LOG_PATH"
    exit 0
else
    echo "$FAILURE builds failed. Look at 'build.log' for more information."
    exit 1
fi

# Building
Use `nix develop` for seemless development without android studio.

Once the APK is ready, start the emulator, flash the APK as below:

```
# Set the paths accordin to your setup
export ANDROID_HOME=$HOME/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/emulator
export PATH=$PATH:$ANDROID_HOME/tools
export PATH=$PATH:$ANDROID_HOME/tools/bin
export PATH=$PATH:$ANDROID_HOME/platform-tools

emulator -list-avds
emulator @Pixel_7_Pro_API_35 (@<emulator_name>)

adb install <apk path>
```

# Logcat

```
adb devices
adb -s emulator-5554 logcat
```

# Sync Changes from the Public Repository
Whenever you want to update your private repository with changes from the public repository (i.e. `upstream`), follow these steps:

Where: 
Public repo: Rocket chat
Private: Privitty

*Step 1: Clone the Public Repository Locally*
Clone the Public repository to your local machine.

```
git clone https://github.com/username/public-repo.git
cd public-repo
```

*Step 2: Create a Private Repository on GitHub*
Go to your GitHub account and create a new private repository (we’ll refer to it as private-repo).

*Step 3: Add the Private Repository as a New Remote*
In your local clone of the public repository, add your private repository as a new remote named origin:

```
git remote rename origin upstream
git remote add origin https://github.com/your-username/private-repo.git
```

Now, your setup has:

upstream: The original public repository.
origin: Your private repository.

*Step 4: Push the Initial Code to Your Private Repository*
Push all local branches and tags to your private repository:

```
git push -u origin --all         # Push all branches
git push -u origin --tags        # Push all tags
```
*Step 5: Sync Changes from the Public Repository*

1. First, make sure you’re in the root directory of your private repo, then add the public Rocket.Chat repo as an upstream remote:
```
git remote add upstream https://github.com/RocketChat/Rocket.Chat.ReactNative.git
```

2. Fetch changes from the upstream repository::
```
git fetch upstream
```

2. Merge changes from the `upstream` repository into your local branches:

```
git checkout develop
git merge upstream/develop       # Replace 'develop' with the default branch name if different
```

3. Push changes to your private repository:

```
git push origin develop
```

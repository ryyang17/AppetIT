{
  description = "A Nix-flake-based Kotlin + Gradle + Android dev environment with GraalVM";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    android-nixpkgs.url = "github:tadfisher/android-nixpkgs";
  };

  outputs = { self, nixpkgs, android-nixpkgs }: let
    supportedSystems = [
      "x86_64-linux"
      "aarch64-linux"
      "x86_64-darwin"
      "aarch64-darwin"
    ];

    forEachSupportedSystem = f:
      nixpkgs.lib.genAttrs supportedSystems (system:
        f {
          pkgs = import nixpkgs {
            inherit system;
            config = {
              allowUnfree = true;
            };
          };
        }
      );
  in
  {
	packages = forEachSupportedSystem ({ pkgs }: {
      idea-launcher = pkgs.writeShellScriptBin "idea" ''
        exec ${pkgs.jetbrains.idea-ultimate}/bin/idea-ultimate \
          -Dawt.toolkit.name=WLToolkit "$@" \
          >/dev/null 2>&1 &
      '';
    });

    devShells = forEachSupportedSystem ({ pkgs }:
      {
        default =
        let
          androidSdk = android-nixpkgs.sdk.${pkgs.system} (sdkPkgs: with sdkPkgs; [
            cmdline-tools-latest
            build-tools-36-0-0
            platform-tools
            platforms-android-36
            emulator
          ]);
        in
        pkgs.mkShell {
          packages = with pkgs; [
            gcc
            gradle_9
            kotlin
            jdk24
            ncurses
            patchelf
            zlib
            libGL
            xorg.libX11
			jetbrains.idea-ultimate
			self.packages.${pkgs.system}.idea-launcher

            # Android SDK + NDK
            androidSdk
          ];

          JAVA_TOOL_OPTIONS = "--enable-native-access=ALL-UNNAMED";
          SPRING_PROFILES_ACTIVE = "dev";

          shellHook = ''
            export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:${
              pkgs.lib.makeLibraryPath [
                pkgs.libGL
                pkgs.xorg.libX11
              ]
            }

            # Java from Nix
            export JAVA_HOME=${pkgs.jdk24}/lib/openjdk
            export PATH=$JAVA_HOME/bin:$PATH

            # Gradle from Nix
            export GRADLE_HOME=${pkgs.gradle_9}/lib/gradle
            export PATH=$GRADLE_HOME/bin:$PATH

            # Android Home
            export ANDROID_HOME=${androidSdk}/share/android-sdk
            export PATH=ANDROID_HOME/bin:$PATH

            # Kotlin (if installed separately)
            export PATH=${pkgs.kotlin}/bin:$PATH

            # Optional: force IntelliJ to pick up the local Gradle instead of wrapper
            export ORG_GRADLE_PROJECT_gradleUserHome=$GRADLE_HOME
          '';
        };
      }
    );
  };
}
